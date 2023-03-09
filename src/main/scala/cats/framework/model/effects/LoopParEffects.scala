package cats.framework.model.effects

object LoopParEffects:
  def apply(): LoopEffects =
    LoopParEffectsImpl()
  private class LoopParEffectsImpl extends SequenceParEffects with LoopEffects