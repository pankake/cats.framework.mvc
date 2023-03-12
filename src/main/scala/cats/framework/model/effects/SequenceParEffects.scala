package cats.framework.model.effects

import cats.effect.IO
import cats.implicits.{catsSyntaxTuple2Parallel, catsSyntaxTuple3Parallel}
import cats.implicits.catsSyntaxParallelSequence1
import cats.implicits.catsSyntaxParallelTraverse1
import cats.effect.unsafe.implicits.global

private class SequenceParEffects extends AbsEffects:

  type T = Any

  //esegue una sequenza di effetti in parallelo
  def runEffectsSequence(effectsList: Seq[IO[Any]]) =
    effectsList.parSequence.unsafeRunSync()

  //esegue una sequenza di effetti in parallelo ed applica una funzione f ad ognuno di essi
  def runEffectsTraverse(effectsList: Seq[IO[Any]])(f: T => Unit) =
    effectsList.parTraverse(io => io.map(f)).unsafeRunSync()

object SequenceParEffects:
  def apply(): SequenceParEffects =
    ParallelEffectsImpl()
  private class ParallelEffectsImpl extends SequenceParEffects