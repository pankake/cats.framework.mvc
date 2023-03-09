package cats.framework.controller

import cats.{Eval, Traverse}
import cats.effect.{Deferred, IO, Ref}
import cats.framework.model.ModelModule
import cats.framework.view.ViewModule
import munit.CatsEffectSuite

import java.util.concurrent.{Executors, TimeUnit}
import cats.effect.std.{Console, Random}
import cats.effect.testkit.TestControl

import scala.concurrent.duration.*
import cats.implicits.*
import cats.data.*
import cats.effect.kernel.Async

import scala.collection.immutable.Queue
import cats.effect.{Deferred, IO}
import cats.syntax.all.*

class ControllerTest extends CatsEffectSuite:

  val scheduler = Executors.newScheduledThreadPool(1)

  def runAsync[A](right: => A, effects: => A*)(delay: Int): IO[Unit] =
    IO.async_[Unit] { cb =>
      scheduler.schedule(new Runnable {
        def run = cb(Right(right))
      }, delay, TimeUnit.MILLISECONDS)
      effects
    }

  def gameLoop[A](asyncEffects: => A*): Unit =
    IO {
      asyncEffects
      //IO.sleep(frequency.millisecond).unsafeRunSync()
      gameLoop(asyncEffects)
    }.unsafeRunSync()

  def retry(ioa: IO[_], delay: FiniteDuration, max: Int, random: Random[IO]): IO[_] =
    if (max <= 1)
      ioa
    else
      ioa handleErrorWith { _ =>
        random.betweenLong(0L, delay.toNanos) flatMap { ns =>
          IO.sleep(ns.nanos) *> retry(ioa, delay * 2, max - 1, random)
        }
      }

  def effect: Unit =
    println("qwe")
    println("zxc")

  def effect2: Unit =
    println("asd")

  test("Test async gameLoop") {
    //gameLoop(runAsync(effect,())(1000).unsafeRunSync())
  }

  test("Test gameloop with multiple async effects") {
    runAsync(println("done"), effect, effect, effect, effect)(1000).unsafeRunSync()
  }

  test("Test parMapN") {
    //val res = (List(1, 2, 3), List(4, 5, 6)).parMapN(_ + _)

    //println(res)
    runAsync(println("done"), println((List(1, 2, 3), List(4, 5, 6)).parMapN(_ + _)), effect, effect, effect)(1000).unsafeRunSync()
  }

  test("Test blocking") {
    def start(d: Deferred[IO, Int]): IO[Unit] = {
      //val attemptCompletion: Int => IO[Unit] = n => d.complete(n).void

      List(
        //IO.race(attemptCompletion(1), attemptCompletion(2)),
        d.get.flatMap { n => IO(println(show"Result: $n")) }
      ).parSequence.void
    }
  }

  //https://yadukrishnan.live/cancellation-of-iofiber-in-cats-effect-3-part-6
  test("Test cancellation") {
    val longRunningIO =
      IO.println("Start processing") >> IO.sleep(5.seconds) >> IO.println("Task completed")
        .onCancel(IO.println("This IO got cancelled"))

    val fiberOps = for {
      fib <- longRunningIO.start
      _ <- IO.sleep(2000.millis) >> IO.println("cancelling task!")
      _ <- fib.cancel
      res <- fib.join
    } yield ()

    fiberOps.unsafeRunSync()
  }

  //https://yadukrishnan.live/lazy-and-eager-computations-in-cats-using-eval
  test("Test lazy eval") {
    val lazyNumber: Eval[Int] = Eval.later {
      println("This is a lazy evaluation")
      100
    }

    println(lazyNumber.value)
  }

  test("Test map") {
    val io1 = IO("Scala")
    val io2 = IO("Cats")
    val mapPgm: IO[String] = io1.map(_ + "")

    val stringa = io1.flatMap(s => io2.map(s + " " + _))

    println(stringa.unsafeRunSync())
  }

  test("testFibersDeferred") {

    def runAndWaitDeferred(wakeUpSignal: Deferred[IO, Any], ioBefore: IO[Any], ioAfter: IO[Any]) = for {
      _ <- ioBefore.start
      _ <- wakeUpSignal.get
      _ <- ioAfter.start
    } yield ()

    def runAndWakeUpDeferred(wakeUpSignal: Deferred[IO, Any], operations: IO[Any], value: Any) = for {
      _ <- IO(operations.unsafeRunSync())
      _ <- IO.println("runAndWakeUpDeferred doing stuff") >> IO.sleep(500.millis)
      _ <- wakeUpSignal.complete(value)
    } yield ()

    def runEffects = for {
      wakeUpSignal <- IO.deferred[Any]
      fib1 <- runAndWaitDeferred(wakeUpSignal, IO.println("run before"), IO.println("run after")).start
      fib2 <- runAndWakeUpDeferred(wakeUpSignal, IO.println("runAndWakeUpDeferred started"), "wake up!").start
      _ <- fib1.join
      _ <- fib2.join
    } yield ()

    runEffects.unsafeRunSync()
  }