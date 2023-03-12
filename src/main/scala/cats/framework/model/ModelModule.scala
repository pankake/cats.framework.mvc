package cats.framework.model

import cats.effect.IO
import cats.effect.std.Console
import cats.framework.model
import cats.framework.model.effects.{AsyncEffects, AsyncParEffects, BlockingEffects, ExpirationEffect, FiberWithGuarantee, LoopEffects, LoopParEffects, ParTupledEffects, RaceEffects, RetryEffects, SequenceEffects, SequenceParEffects, SynchronizedCountDownLatchEffects, SynchronizedDeferredEffects, SynchronizedSemaphoreEffects, UncancellableEffects}

import scala.util.Random

object ModelModule:

  sealed trait Component extends SharedState with ErrorHandling
    with AtomicRef with ParTupledEffects:

    //istanze delle classi e tratti che gestiscono gli effetti
    protected[framework] object Effects:
      def loopSeq = LoopEffects.apply()
      def loopPar = LoopParEffects.apply()
      def effectsSeq = SequenceEffects.apply()
      def effectsPar = SequenceParEffects.apply()
      def effectsAsync = AsyncEffects.apply()
      def effectsAsyncPar = AsyncParEffects.apply()
      def effectsSynchDef = SynchronizedDeferredEffects.apply()
      def effectsSynchSem = SynchronizedSemaphoreEffects.apply()
      def effectsSynchCDLatch = SynchronizedCountDownLatchEffects.apply()
      def effectsExpiration = ExpirationEffect.apply()
      def fiberWithGuarantee = FiberWithGuarantee.apply()
      def fiberUncancellable = UncancellableEffects.apply()
      def fiberBlocking = BlockingEffects.apply()
      def fiberRetry = RetryEffects.apply()
      def fiberRace = RaceEffects.apply()


  
  //interfaccia per le altre componenti
  trait Interface extends Component