package cats.framework.model.effects

import cats.Traverse
import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Sync}

import java.util.concurrent.Executors

abstract class AbsEffects:

  type T

  //istanza di traverse
  val listTraverse = Traverse[Seq]
  //istanza di ExecutorService per la gestione del threadpool
  val scheduler = Executors.newScheduledThreadPool(1)

  //metodo per l'esecuzione di una sequenza di effetti
  def runEffectsSequence(effectsList: Seq[IO[Any]]): Seq[Any]

  //esegue una sequenza di effetti applicando su ognuno una funzione
  def runEffectsTraverse(effectsList: Seq[IO[Any]])(f: T => Unit): Seq[Any]








