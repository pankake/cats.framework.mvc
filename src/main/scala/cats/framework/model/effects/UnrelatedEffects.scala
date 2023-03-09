package cats.framework.model.effects

import cats.effect.kernel.Outcome
import cats.effect.kernel.Outcome.{Canceled, Errored, Succeeded}
import cats.effect.{IO, Ref}
import cats.framework.model.AtomicRef

import concurrent.duration.{DurationInt, FiniteDuration}
import cats.effect.unsafe.implicits.global

private sealed trait UnrelatedEffects:
  type Effect
  type ReturnValue
  type Behaviour

  def execute(b: Behaviour): ReturnValue

private sealed trait ExpirationEffect extends UnrelatedEffects:

  type Effect = IO[Unit]
  type Behaviour = (Effect, Int, Effect)
  type ReturnValue = Unit

  def execute(b: Behaviour): ReturnValue =
    val io: Effect = b._1
    val duration: Int = b._2
    val fallback: Effect = b._3

    io.timeoutTo(duration.millis, fallback)

object ExpirationEffect:
  def apply(): ExpirationEffect =
    ExpirationEffectImpl()
  class ExpirationEffectImpl extends ExpirationEffect


private sealed trait FiberWithGuarantee extends UnrelatedEffects with AtomicRef:

  type Effect = IO[Unit]
  type Behaviour = (IO[Any], Ref[IO, Boolean], Effect, Effect, Effect)
  type ReturnValue = IO[Outcome[IO, Throwable, Any]]

  //esegue una fibra su un nuovo thread, attende che completi e restituisce il risultato della sua esecuzione
  //se la condizione non è soddisfatta la fibra viene cancellata
  //vengono applicati in maniera lazy dei finalizzatori per ogni caso possibile
  def execute(b: Behaviour): ReturnValue =
    val io: IO[Any] = b._1
    val cond: Ref[IO, Boolean] = b._2
    val ioSucc: Effect = b._3
    val ioErr: Effect = b._4
    val ioCanc: Effect = b._5

    for {
      fiber <- io.start
      _ <- IO {
        if (!refGet(cond).unsafeRunSync()) fiber.cancel
      }
      result <- fiber.join
      _ <- io.guaranteeCase {
        case Succeeded(success) =>
          success.flatMap(msg =>
            IO("IO successfully completed with value: " + msg) >> ioSucc
          )
        case Errored(ex) =>
          IO("Error occurred while processing, " + ex.getMessage) >> ioErr
        case Canceled() => IO("Processing got cancelled in between") >> ioCanc
      }
    } yield result

object FiberWithGuarantee:
  def apply(): FiberWithGuarantee =
    FiberWithGuaranteeImpl()
  class FiberWithGuaranteeImpl extends FiberWithGuarantee


private sealed trait UncancellableEffects extends UnrelatedEffects:

  type Effect = IO[Any]
  type Behaviour = (Effect, Effect)
  type ReturnValue = Unit

  def execute(b: Behaviour): ReturnValue =
    val cancellableIO: Effect = b._1
    val uncancellableIO: Effect = b._2

    IO.uncancelable(unmask => unmask(cancellableIO) >> uncancellableIO)

  def execUncancellableLazyParIO(b: Behaviour): ReturnValue =
    val cancellableIO: Effect = b._1
    val uncancellableIO: Effect = b._2

    IO.uncancelable(unmask => unmask(cancellableIO) &> uncancellableIO)

object UncancellableEffects:
  def apply(): UncancellableEffects =
    UncancellableEffectsImpl()
  class UncancellableEffectsImpl extends UncancellableEffects


private sealed trait BlockingEffects extends UnrelatedEffects:

  type Effect = IO[Any]
  type Behaviour = Effect
  type ReturnValue = Unit

  val customThreadPool = scala.concurrent.ExecutionContext.global

  def execute(b: Behaviour): ReturnValue =
    val io: Effect = b

    val blockingPoolExec = for {
      _ <- IO.blocking { io.start }
    } yield ()

    blockingPoolExec.evalOn(customThreadPool).unsafeRunSync()

object BlockingEffects:
  def apply(): BlockingEffects =
    BlockingEffectsImpl()
  class BlockingEffectsImpl extends BlockingEffects


private sealed trait RetryEffects extends UnrelatedEffects:
  type Effect = IO[Any]
  type Behaviour = (Effect, Int, FiniteDuration)
  type ReturnValue = Unit

  def retryLoop(io: Effect, times: Int, sleep: FiniteDuration): Effect =
    io.handleErrorWith { case ex =>
      if (times != 0)
        IO.println("Will retry in " + sleep)
          *> IO.sleep(sleep) >> retryLoop(io, times - 1, sleep)
      else
        IO.println("Exhausted all the retry attempts") >> IO.raiseError(ex)
    }

  def execute(b: Behaviour): ReturnValue =
    val io: Effect = b._1
    val times: Int = b._2
    val sleep: FiniteDuration = b._3

    retryLoop(io, times, sleep)

object RetryEffects:
  def apply(): RetryEffects =
    RetryEffectsImpl()
  class RetryEffectsImpl extends RetryEffects


private sealed trait RaceEffects extends UnrelatedEffects:

  type Effect = IO[Any]
  type Behaviour = (Effect, Effect)
  type ReturnValue = Any

  def execute(b: Behaviour): ReturnValue =
    val io1: Effect = b._1
    val io2: Effect = b._2

    IO.race(io1, io2).map {
    _.match {
      case Right(res) => IO.println(s"io2 finished first: `${res}` ") >> IO.pure(res)
      case Left(res) => IO.println(s"io1 finished first: `${res}` ") >> IO.pure(res)
    }
  }

  def execRacePair(b: Behaviour): ReturnValue =
    val io1: Effect = b._1
    val io2: Effect = b._2

    IO.racePair(io1, io2).map {
      _.match {
        case Right(out, fib) => IO.println(s"io2 finished first: `${out}` ") >> IO.pure(out, fib)
        case Left(out, fib) => IO.println(s"io1 finished first: `${out}` ") >> IO.pure(out, fib)
      }
    }

object RaceEffects:
  def apply(): RaceEffects =
    RaceEffectsImpl()
  class RaceEffectsImpl extends RaceEffects



