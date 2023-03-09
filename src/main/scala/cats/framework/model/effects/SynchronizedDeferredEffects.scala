package cats.framework.model.effects

import cats.effect.{Deferred, IO}
import cats.effect.unsafe.implicits.global


private sealed trait SynchronizedDeferredEffects extends SynchronizedEffects:

  type Effect = IO[A]
  type SyncTool = Deferred[IO, A]

  type A = Any
  type wFiber = (Effect, Effect)
  type aFiber = (Effect, A)

  def waitingFiber(wFiber: wFiber, syncTool: SyncTool): Effect = for {
    _ <- wFiber._1.start
    defValue <- syncTool.get
    _ <- wFiber._2.start
  } yield defValue

  def awakeningFiber(aFiber: aFiber, syncTool: SyncTool): Effect = for {
    ret <- IO(aFiber._1.unsafeRunSync())
    _ <- syncTool.complete(aFiber._2)
  } yield ret

  def performSynchronizedEffects(wFiber: wFiber, aFiber: aFiber): Effect =
    for {
      deferred <- IO.deferred[A]
      _ <- waitingFiber((wFiber._1, wFiber._2), deferred).start
      _ <- awakeningFiber(aFiber, deferred).start
    } yield ()

object SynchronizedDeferredEffects:
  def apply(): SynchronizedDeferredEffects =
    SynchronizedDeferredEffectsImpl()
  class SynchronizedDeferredEffectsImpl extends SynchronizedDeferredEffects
