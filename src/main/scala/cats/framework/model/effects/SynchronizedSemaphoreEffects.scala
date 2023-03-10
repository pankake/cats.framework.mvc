package cats.framework.model.effects

import cats.effect.IO
import cats.effect.std.Semaphore
import cats.effect.unsafe.implicits.global

private sealed trait SynchronizedSemaphoreEffects extends SynchronizedEffects:

  type Effect = IO[A]
  type SyncTool = Semaphore[IO]
  type A = Any
  type B = Long

  type permitsInit = B
  type permitsRelease = B

  type wFiber = (Effect, Effect)
  type aFiber = (Effect, permitsRelease, permitsInit)

  //esegue un effetto di tipo IO poi si mette in attesa
  //al risveglio esegue un secondo effetto di tipo IO
  def waitingFiber(wFiber: wFiber, syncTool: SyncTool): Effect = for {
    _ <- wFiber._1.start
    _ <- syncTool.acquire
    _ <- wFiber._2.start
  } yield ()

  //esegue un effetto di tipo IO e sveglia la fibra in attesa
  def awakeningFiber(aFiber: aFiber, syncTool: SyncTool): Effect = for {
    ret <- IO(aFiber._1.unsafeRunSync())
    _ <- syncTool.releaseN(aFiber._2)
  } yield ret

  //esegue le due fibre e attende per la loro terminazione
  def performSynchronizedEffects(wFiber: wFiber, aFiber: aFiber): Effect =
    for {
    sem <- Semaphore[IO](aFiber._3)
    fib1 <- waitingFiber(wFiber, sem).start
    fib2 <- awakeningFiber(aFiber, sem).start
    _ <- fib1.join
    _ <- fib2.join
  } yield ()

object SynchronizedSemaphoreEffects:
  def apply(): SynchronizedSemaphoreEffects =
    SynchronizedSemaphoreEffectsImpl()
  class SynchronizedSemaphoreEffectsImpl extends SynchronizedSemaphoreEffects
