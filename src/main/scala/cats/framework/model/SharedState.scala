package cats.framework.model

import cats.effect.std.Console
import cats.effect.{Deferred, IO, Ref, Sync}
import cats.effect.unsafe.implicits.global

import scala.collection.immutable.Queue
import cats.syntax.all.toFlatMapOps
import cats.Functor.ops.toAllFunctorOps
import cats.effect.kernel.Async


trait SharedState:

  //stato condivisibile composto da due code, una per i valori e una per gli elem. di tipo Deferred
  case class State[F[_], A](queue: Queue[A], takers: Queue[Deferred[F,A]])

  //inizializza le due code come vuote
  object State {
    def empty[F[_], A]: State[F, A] = State(Queue.empty, Queue.empty)
  }

  //modifica dell'oggetto state: non fa niente deve essere sovrascritto
  def modifyState[F[_] : Sync : Console](sState: Ref[F, State[F, Int]]): Any =
  //modifica lo stato condiviso sState
    sState.modify {
      case State(queue, deferredQ) => State(queue, deferredQ) -> Sync[F].unit
    }

  //legge dall'oggetto state: deve essere sovrascritto
  def getState[F[_] : Sync : Console](sState: Ref[F, State[F, Int]]): Any =
    sState.get
