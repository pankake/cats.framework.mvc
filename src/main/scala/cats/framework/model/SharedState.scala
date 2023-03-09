package cats.framework.model

import cats.effect.std.Console
import cats.effect.{Deferred, IO, Ref, Sync}
import cats.effect.unsafe.implicits.global

import scala.collection.immutable.Queue
import cats.syntax.all.toFlatMapOps
import cats.Functor.ops.toAllFunctorOps
import cats.effect.kernel.Async


trait SharedState:

  case class State[F[_], A](queue: Queue[A], takers: Queue[Deferred[F,A]])

  object State {
    def empty[F[_], A]: State[F, A] = State(Queue.empty, Queue.empty)
  }

  def modifyState[F[_] : Sync : Console](sState: Ref[F, State[F, Int]]): Any =
  //modifica lo stato condiviso sState
    sState.modify {
      case State(queue, deferredQ) => State(queue, deferredQ) -> Sync[F].unit
    }

  def getState[F[_] : Sync : Console](sState: Ref[F, State[F, Int]]): Any =
    sState.get
