package snake

import cats.effect.{IO, IOApp}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext
import cats.effect.*
import cats.effect.unsafe.implicits.global
import cats.framework.controller.ControllerModule
import cats.framework.model.ModelModule
import cats.framework.view.ViewModule
import javafx.scene.input
import cats.implicits.{catsSyntaxParallelSequence1, showInterpolator}
import cats.syntax.all.toFlatMapOps
import cats.Functor.ops.toAllFunctorOps
import cats.effect.kernel.Async

import java.util.concurrent.TimeUnit
import scala.language.postfixOps

object SnakeFx extends View:

  override def start(): Unit =

    val game: IO[Unit] =
      for {
        _ <- IO.pure(createView(gs))
        sState <- Ref.of[IO, State[IO, Int]](State.empty[IO, Int])
        _ <- IO(handlePause(sState).unsafeRunAsync(_ => ()))
        _ <- IO(loopSequence.loopEffectsSeq(List(IO(effectsAsync.runAsyncSequence((), List(IO(frame.update(frame.value + 1))))
        (70))))(refCreate(false).unsafeRunSync()))
        _ <- IO(effectsAsync.runAsyncSequence((), List(IO(frameUpdate(sState))))(0))
          .handleErrorWith { t =>
            failedIO(t).as(ExitCode.Error)
          }
      } yield ()

    game.unsafeRunSync()





