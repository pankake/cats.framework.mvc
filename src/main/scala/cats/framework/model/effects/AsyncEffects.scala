package cats.framework.model.effects

import cats.Traverse
import cats.effect.IO
import cats.effect.unsafe.implicits.global

import java.util.concurrent.{Executors, TimeUnit}

private trait AsyncEffects extends AbsEffects:

  def runAsync(right: => Any, effects: => Any*)(delay: Int): Any =
    IO.async_[Unit] { cb =>
      scheduler.schedule(new Runnable {
        def run = cb(Right(right))
      }, delay, TimeUnit.MILLISECONDS)
      effects
    }.handleError(error => error.getMessage).unsafeRunSync()

  def runAsyncSequence(right: => Any, effectsList: Seq[IO[Any]])(delay: Int): Any =
    IO.async_[Unit] { cb =>
      scheduler.schedule(new Runnable {
        def run = cb(Right(right))
      }, delay, TimeUnit.MILLISECONDS)
      runEffectsSequence(effectsList)
    }.handleError(error => error.getMessage).unsafeRunSync()

  def runAsyncTraverse(right: => Any, effectsList: Seq[IO[Any]], f: Any => Unit)(delay: Int): Any =
    IO.async_[Unit] { cb =>
      scheduler.schedule(new Runnable {
        def run = cb(Right(right))
      }, delay, TimeUnit.MILLISECONDS)
      runEffectsTraverse(effectsList)(f)
    }.handleError(error => error.getMessage).unsafeRunSync()

object AsyncEffects:
  def apply(): AsyncEffects =
    AsyncEffectsImpl()
  private class AsyncEffectsImpl extends SequenceEffects with AsyncEffects

