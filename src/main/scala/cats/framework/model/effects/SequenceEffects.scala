package cats.framework.model.effects

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.framework.model.effects.AbsEffects

class SequenceEffects extends AbsEffects:

  type T = Any

  def runEffectsSequence(effectsList: Seq[IO[Any]]) =
    listTraverse.sequence(effectsList).unsafeRunSync()

  def runEffectsTraverse(effectsList: Seq[IO[Any]])(f: Any => Unit) =
    listTraverse.traverse(effectsList)(io => io.map(f)).unsafeRunSync()

object SequenceEffects:
  def apply(): SequenceEffects =
    SequenceEffectsImpl()
  private class SequenceEffectsImpl extends SequenceEffects