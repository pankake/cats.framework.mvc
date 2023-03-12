package cats.framework.model.effects

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.framework.model.effects.AbsEffects

private class SequenceEffects extends AbsEffects:

  type T = Any

  //esegue una sequenza di effetti IO
  def runEffectsSequence(effectsList: Seq[IO[Any]]) =
    listTraverse.sequence(effectsList).unsafeRunSync()

  //esegue una sequenza di effetti di tipo IO e applica una funzione a ognuno di essi
  def runEffectsTraverse(effectsList: Seq[IO[Any]])(f: Any => Unit) =
    listTraverse.traverse(effectsList)(io => io.map(f)).unsafeRunSync()

object SequenceEffects:
  def apply(): SequenceEffects =
    SequenceEffectsImpl()
  private class SequenceEffectsImpl extends SequenceEffects