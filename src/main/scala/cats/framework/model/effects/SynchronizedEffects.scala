package cats.framework.model.effects

import cats.effect.IO
import cats.effect.std.Semaphore

private trait SynchronizedEffects:

  type SyncTool
  type Effect
  type wFiber
  type aFiber


  //esegue un effetto di tipo IO poi si mette in attesa
  //al risveglio esegue un secondo effetto di tipo IO
  def waitingFiber(wFiber: wFiber, syncTool: SyncTool): Effect

  //esegue un effetto di tipo IO e sveglia la fibra in attesa
  def awakeningFiber(aFiber: aFiber, syncTool: SyncTool): Effect
  def performSynchronizedEffects(wFiber: wFiber, aFiber: aFiber): Effect





