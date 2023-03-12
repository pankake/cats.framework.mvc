package snake

import cats.effect.kernel.Outcome
import cats.effect.std.Semaphore
import cats.effect.{IO, Ref, Sync}
import scalafx.application.JFXApp3
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle

import scala.util.Random
import cats.effect.unsafe.implicits.global
import cats.framework.controller.{ControllerModule, GS}
import cats.framework.model.ModelModule
import cats.implicits.*
import scalafx.beans.property.IntegerProperty
import snake.configurations.cfg
import cats.syntax.parallel.*

import scala.concurrent.duration.*
import scala.collection.immutable.List

trait Model extends ControllerModule.Interface:

  //rappresenta il serpente
  case class Snake(list: List[(Double, Double)])

  //rappresenta il cibo che deve essere mangiato
  case class Food(value: (Double, Double))

  //rappresenta il cibo che deve essere evitato
  case class EvilFood(value: (Double, Double))

  //rappresenta lo stato del gioco
  case class GameState(snake: Snake, food: Food, efoodRef: Ref[IO, List[EvilFood]], value: String, frame: IntegerProperty) extends GS:

    //creazione di un nuovo stato
    def newState(dir: Any): GameState =

      //calcola la nuova posiziome della testa del serpente
      def calcNewHead(snakeRef: Ref[IO, List[(Double, Double)]]): IO[(Double, Double)] =
        for {
          snake <- refGet(snakeRef)
          head <- IO(snake.head)
          (x, y) = head
          nHead <- compDirection(dir, x, y)
          (newx, newy) = nHead
        } yield (newx, newy)

      //aggiornamento del serpente
      def newSnake(snakeRef: Ref[IO, List[(Double, Double)]], newx: Double, newy: Double): IO[List[(Double, Double)]] =
        for {
          //verifica le collisioni con i limiti della scena, col cibo cattivo e col serpente stesso
          newSnake <- if (newx < 0 || newx >= 600 || newy < 0 || newy >= 600
            || refGet(snakeRef).unsafeRunSync().tail.contains(newx, newy) || efoodCollision(efoodRef, newx, newy))
            for {
              //ripristina il serpente e scrive il nuovo punteggio massimo se serve
              initSnake <- IO(initialSnake) <& writeNewHighScore(value.toInt, refGet(snakeRef).unsafeRunSync().size - 3)
            } yield (initSnake.list)
          else if (food.value == (newx, newy))
            for {
              //il serpente sta mangiando
              newSnake <- refUpdateAndGetNewVal(snakeRef)(snake => food.value :: snake)
            } yield (newSnake)
          else
            //aggiorna il serpente con la nuova testa e rimuove l'ultimo elemento in coda
            refUpdateAndGetNewVal(snakeRef)(snake => (newx, newy) :: snake.init)
        } yield newSnake

      //logica del serpente
      def snakeLogic: IO[(Snake, Food)] =
        for {
          //crea una referenza di tipo atomico
          snakeRef <- refCreate(snake.list)

          //calcola la nuova posizione della testa
          newHead <- calcNewHead(snakeRef)
          (newx, newy) = newHead

          //esegue tre operazioni in parallelo:
          //aggiornamento del serpente, creazione del cibo, sequenza asincrona per il cibo cattivo
          ret <- parTupled3(newSnake(snakeRef, newx, newy),
            if (food.value == (newx, newy))
              IO(randomFood(refGet(efoodRef).unsafeRunSync(), snake)) //crea cibo
            else IO(food), IO(if (frame.value % 200 == 0) IO(effectsSynchCDLatch.performSynchronizedEffects((
              IO.unit, efoodRef.set(initialEvilFood)), (IO(
              refGet(efoodRef)
                .flatMap(efoodList => refUpdateAndGetNewVal(efoodRef)(_ => efoodList.updated(0, randomEfood(efoodList, food, snake))))
                .flatMap(efoodList => IO.sleep(Random.between(500, 1500).millis) *> refUpdateAndGetNewVal(
                  efoodRef)(_ => efoodList.updated(1, randomEfood(efoodList, food, snake))))
                .flatMap(efoodList => IO.sleep(Random.between(500, 1500).millis) *> refUpdateAndGetNewVal(
                  efoodRef)(_ => efoodList.updated(2, randomEfood(efoodList, food, snake))))
                .flatMap(efoodList => IO.sleep(Random.between(500, 1500).millis) *> refUpdateAndGetNewVal(
                  efoodRef)(_ => efoodList.updated(3, randomEfood(efoodList, food, snake))))
                .flatMap(efoodList => IO.sleep(Random.between(500, 1500).millis) *> refUpdateAndGetNewVal(
                  efoodRef)(_ => efoodList.updated(4, randomEfood(efoodList, food, snake))))
                .flatMap(efoodList => IO.sleep(Random.between(500, 1500).millis) *> refUpdateAndGetNewVal(
                  efoodRef)(_ => efoodList.updated(5, randomEfood(efoodList, food, snake)))
                  <* IO.sleep(5.seconds))
              ).unsafeRunSync(), 1)).unsafeRunSync()).unsafeRunSync()
            )
          )
          (nSnake, nFood, ()) = ret
          //gestione di un'eccezione con condizione
          _ <- handledErrorIO(raisedConditionIO(nSnake.isEmpty, new Exception()),
            //ritenta il ripristino del serpente fino al successo
            _ => refModifyUntilSucceed(snakeRef)(current => (current, initialSnake :: current)))
        } yield (Snake(nSnake), nFood)

      //lancia in esecuzione l'effetto per la logica del serpente
      val (nSnake, nFood) = snakeLogic.unsafeRunSync()

      //ritorna un nuovo stato
      GameState(nSnake, nFood, efoodRef, value, frame)

    //lista di rettangoli da disegnare
    def shapes: List[Rectangle] = square(food.value._1, food.value._2, Red)
      :: snake.list.map {
      case (x, y) => square(x, y, Green)
    }.concat(refGet(efoodRef).unsafeRunSync().map(x => square(x.value._1, x.value._2, Purple)))

    //collisione con il cibo da evitare
    def efoodCollision(efoodRef: Ref[IO, List[EvilFood]], newx: Double, newy: Double): Boolean =
      refGet(efoodRef).unsafeRunSync().find(x => x.value == (newx, newy)) match {
        case None => false
        case _ => true
      }

    //crea i rettangoli per la scena
    def square(xr: Double, yr: Double, color: Color) = new Rectangle:
      x = xr
      y = yr
      width = 25
      height = 25
      fill = color

  //calcola la nuova direzione della testa
  def compDirection(dir: Any, x: Double, y: Double): IO[(Double, Double)] = IO(
    dir match {
      case 1 => (x, y - 25)
      case 2 => (x, y + 25)
      case 3 => (x - 25, y)
      case 4 => (x + 25, y)
      case _ => (x, y)
    }
  )

  //genera le coordinate per il cibo
  def randomFood(evilFood: List[EvilFood], snake: Snake): Food =
    val food = Food(Random.nextInt(24) * 25, Random.nextInt(24) * 25)
    dropFood(evilFood, food, snake)

  //crea il cibo
  def dropFood(evilFood: List[EvilFood], food: Food, snake: Snake): Food =
    if (snake.list.contains(food.value)
      || evilFood.contains(food.value)) randomFood(evilFood, snake)
    else
      food

  //genera le coordinate per il cibo da evitare
  def randomEfood(evilFood: List[EvilFood], food: Food, snake: Snake): EvilFood =
    val efood = EvilFood(Random.nextInt(24) * 25, Random.nextInt(24) * 25)
    dropEFood(evilFood, efood, food, snake)

  //crea il cibo da evitare
  def dropEFood(evilFood: List[EvilFood], efood: EvilFood, food: Food, snake: Snake): EvilFood =
    if (snake.list.contains(efood.value)
      || evilFood.contains(efood.value)
      || food.value.equals(efood.value)) randomEfood(evilFood, food, snake)
    else efood

  //crea il serpente
  def initialSnake: Snake = Snake(List(
    (250, 200),
    (225, 200),
    (200, 200))
  )

  //crea il cibo da evitare
  def initialEvilFood: List[EvilFood] = List(
    EvilFood((-50, -50)),
    EvilFood((-50, -50)),
    EvilFood((-50, -50)),
    EvilFood((-50, -50)),
    EvilFood((-50, -50)),
    EvilFood((-50, -50))
  )

  //scrive il punteggio su file
  def writeNewHighScore(hScore: Int, nScore: Int) = IO(
    if(hScore < nScore)
      writeWithResource(nScore.toString, cfg.scoreFilePath).unsafeRunSync()
  )