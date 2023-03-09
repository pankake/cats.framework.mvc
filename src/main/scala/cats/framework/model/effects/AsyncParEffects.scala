package cats.framework.model.effects

object AsyncParEffects:
  def apply(): AsyncEffects =
    AsyncParEffectsImpl()
  private class AsyncParEffectsImpl extends SequenceParEffects with AsyncEffects
