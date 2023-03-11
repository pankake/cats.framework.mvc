package snake

import cats.effect.{Deferred, ExitCode, IO, Ref, Sync}
import cats.effect.std.{Console, Random, Semaphore}
import javafx.scene.input.KeyEvent
import scalafx.application.JFXApp3
import scalafx.beans.property.{IntegerProperty, ObjectProperty}
import scalafx.event.Event
import scalafx.scene.Scene
import scalafx.scene.paint.Color.*

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import cats.effect.unsafe.implicits.global
import cats.framework.controller.{ControllerModule, GS}
import cats.implicits.catsSyntaxApply
import cats.syntax.all.toFlatMapOps
import cats.Functor.ops.toAllFunctorOps
import cats.effect.kernel.Async
import scalafx.scene.control.Label
import cats.framework.model.ModelModule
import snake.configurations.cfg



trait Controller extends Model:

  override def readKeys(keyEvent: KeyEvent): Unit =
    keyEvent.getText match {
      case "w" | "W" => if(direction.value != 2) direction.value = 1
      case "s" | "S" => if(direction.value != 1) direction.value = 2
      case "a" | "A" => if(direction.value != 4) direction.value = 3
      case "d" | "D" => if(direction.value != 3) direction.value = 4
      case _ => IO.unit
    }

  def handlePause(sState: Ref[IO, State[IO, Int]]): IO[Unit] =
    for {
      _ <- modifyState(sState)
        .handleErrorWith { t =>
          Console[IO].errorln(s"Error caught: ${t.getMessage}").as(ExitCode.Error)
        }
    } yield ()

  //proprietà usata per la direzione inserita attraverso i comandi
  val direction = IntegerProperty(4) // 4 = right

  val frame = IntegerProperty(0)

  //proprietà che descrive lo stato corrente del gioco
  val gs: ObjectProperty[GS] = ObjectProperty(GameState(initialSnake, randomFood(List(EvilFood(-1, -1)), initialSnake), refCreate(initialEvilFood).unsafeRunSync(), readWithResource(cfg.scoreFilePath).unsafeRunSync(), frame))

  //ad ogni cambio di frame viene aggiornato lo stato considerando il valore corrente di direction
  def frameUpdate(stateR: Ref[IO, State[IO, Int]]): Unit =

    frame.onChange {

      val program: IO[Unit] =
        for {
          ret <- checkState(stateR)
          _ <- if (ret) updateState(gs, direction.value)
            else IO.unit
        } yield ()
      program.unsafeRunSync()
    }

  private def checkState[F[_] : Sync : Console](stateR: Ref[F, State[F, Int]]): F[Boolean] =
    stateR.modify {
      case State(queue, deferredQ)
        if queue.nonEmpty => State(queue, deferredQ) -> false

      case State(queue, deferredQ) =>
        State(queue, deferredQ) -> true
    }

  def readKey[F[_] : Sync : Console]: F[Char] =
    for {
      n <- readLine
    } yield if(n.nonEmpty)n.last else 'x'

  override def modifyState[F[_] : Sync : Console](sState: Ref[F, State[F, Int]]): F[Boolean] =
    def edit: F[Unit] =
      sState.modify {
        //se Queue non è vuota
        case State(queue, deferredQ) if queue.nonEmpty =>
          //toglie un elemento dalla coda e la restituisce
          val (elem, nQueue) = queue.dequeue
          //ritorna il nuovo stato: coda vuota e deferredQ vuota
          State(nQueue, deferredQ) -> true

        //coda vuota
        case State(queue, deferredQ) =>
          State(queue.enqueue(1), deferredQ) -> true //Sync[F].unit
      }

    for {
      ch <- readKey //sistemare stringa vuota
      _ <- ch match {
        case 'p' | 'P' => edit
        case _ => Sync[F].unit
      }
      _ <- modifyState(sState)
    } yield true
