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

  //esegue un effetto di tipo IO poi si mette in attesa
  //al risveglio esegue un secondo effetto di tipo IO
  def waitingFiber(wFiber: wFiber, syncTool: SyncTool) = for {
    _ <- wFiber._1
    _ <- syncTool.await
    _ <- wFiber._2
  } yield ()

  //esegue un effetto di tipo IO e sveglia la fibra in attesa
  def awakeningFiber(aFiber: aFiber, syncTool: SyncTool) = for {
    _ <- aFiber._1
    _ <- syncTool.release
  } yield ()

  //inizializza il CountDownLatch con il numero di fibre che sono attese
  //genera una fibra di tipo waitingFiber che si mette in attesa sullo strumento di sincro.
  //genera il numero di fibre di tipo awakeningFiber richiesto per poter risvegliare la fibra in attesa
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
