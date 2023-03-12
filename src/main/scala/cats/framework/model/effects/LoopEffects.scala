package cats.framework.model.effects

import cats.Traverse
import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Ref}
import cats.framework.controller.ControllerModule
import cats.framework.model.{AtomicRef}
import cats.syntax.parallel.*

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.*

private trait LoopEffects extends AbsEffects with AtomicRef:

  //cicla gli effetti fino a quando la condizione non è soddisfatta
  def loopEffects(effects: => Any*)(termination: Ref[IO, Boolean]): Unit =
    refGet(termination).unsafeRunSync() match {
      case false =>
        IO {
          effects.map(_ => loopEffects(effects)(termination))
        }.handleError(error => error.getMessage).unsafeRunAsync(_ => ())
      case true => IO.unit
    }

  //cicla una sequenza di effetti fino a quando la condizione non è soddisfatta
  def loopEffectsSeq(effectsList: Seq[IO[Any]])(termination: Ref[IO, Boolean]): Unit =
    refGet(termination).unsafeRunSync() match {
      case false =>
        IO {
          runEffectsSequence(effectsList)
            .map(_ => loopEffectsSeq(effectsList)(termination))
        }.handleError(error => error.getMessage).unsafeRunAsync(_ => ())
      case true => IO.unit
    }

  //cicla una sequenza di effetti fino a quando la condizione non è soddisfatta
  //applica una funzione f su ogni effetto
  def loopEffectsTraverse(effectsList: Seq[IO[Any]], f: Any => Unit)(termination: Ref[IO, Boolean]): Unit =
    refGet(termination).unsafeRunSync() match {
      case false =>
        IO {
          runEffectsTraverse(effectsList)(f)
            .map(_ => loopEffectsTraverse(effectsList, f)(termination))
        }.handleError(error => error.getMessage).unsafeRunAsync(_ => ())
      case true => IO.unit
    }

object LoopEffects:
  def apply(): LoopEffects =
    LoopEffectsImpl()
  private class LoopEffectsImpl extends SequenceEffects with LoopEffects
