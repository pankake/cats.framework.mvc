package cats.framework.model.effects

import cats.effect.IO
import cats.effect.std.CountDownLatch
import cats.effect.unsafe.implicits.global

trait SynchronizedCountDownLatchEffects extends SynchronizedEffects:

  type Effect = IO[A]
  type SyncTool = CountDownLatch[IO]

  type A = Any
  type wFiber = (Effect, Effect)
  type aFiber = (Effect, Approvals)
  type Approvals = Int

  def waitingFiber(wFiber: wFiber, syncTool: SyncTool) = for {
    _ <- wFiber._1
    _ <- syncTool.await
    _ <- wFiber._2
  } yield ()

  def awakeningFiber(aFiber: aFiber, syncTool: SyncTool) = for {
    _ <- aFiber._1
    _ <- syncTool.release
  } yield ()

  def performSynchronizedEffects(wFiber: wFiber, aFiber: aFiber) = for {
    approvals <- CountDownLatch[IO](aFiber._2)
    fib <- waitingFiber(wFiber, approvals).start
    _ <- IO(
      for (x <- 1 to aFiber._2) {
        awakeningFiber(aFiber, approvals).unsafeRunAsync(_ => ())
      }
    )
    //_ <- fib.join
  } yield ()

object SynchronizedCountDownLatchEffects:
  def apply(): SynchronizedCountDownLatchEffects =
    SynchronizedCountDownLatchEffectsImpl()
  class SynchronizedCountDownLatchEffectsImpl extends SynchronizedCountDownLatchEffects
