package cats.framework.model.effects

import cats.Traverse
import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Sync}

import java.util.concurrent.Executors

abstract class AbsEffects:

  type T

  val listTraverse = Traverse[Seq]
  val scheduler = Executors.newScheduledThreadPool(1)

  def runEffectsSequence(effectsList: Seq[IO[Any]]): Seq[Any]
  def runEffectsTraverse(effectsList: Seq[IO[Any]])(f: T => Unit): Seq[Any]








