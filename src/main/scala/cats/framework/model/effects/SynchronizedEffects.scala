package cats.framework.model.effects

import cats.effect.IO
import cats.effect.std.Semaphore

private trait SynchronizedEffects:

  type SyncTool
  type Effect
  type wFiber
  type aFiber
  

  def waitingFiber(wFiber: wFiber, syncTool: SyncTool): Effect
  def awakeningFiber(aFiber: aFiber, syncTool: SyncTool): Effect
  def performSynchronizedEffects(wFiber: wFiber, aFiber: aFiber): Effect





