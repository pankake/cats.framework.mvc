package cats.framework.model.effects

import cats.effect.IO
import cats.implicits.{catsSyntaxTuple2Parallel, catsSyntaxTuple3Parallel, catsSyntaxTuple4Parallel}

trait ParTupledEffects:

  //esegue gli IO in parallelo e restituisce i risultati come una tupla
  def parTupled2[A0, A1](io1: IO[A0], io2: IO[A1]): IO[(A0, A1)] = (io1, io2).parTupled

  implicit def toTuple2[A](x: Seq[IO[A]]): (IO[A], IO[A]) =
    x match {
      case Seq(a, b) => (a, b)
    }

  def parTupled2Seq[A](s: Seq[IO[A]]): IO[(A, A)] =
    val seq: (IO[A], IO[A]) = s
    seq.parTupled

  def parTupled3[A0, A1, A2](io1: IO[A0], io2: IO[A1], io3: IO[A2]): IO[(A0, A1, A2)] = (io1, io2, io3).parTupled

  implicit def toTuple3[A](x: Seq[IO[A]]): (IO[A], IO[A], IO[A]) =
    x match {
      case Seq(a, b, c) => (a, b, c)
    }

  def parTupled3Seq[A](s: Seq[IO[A]]): IO[(A, A, A)] =
    val seq: (IO[A], IO[A], IO[A]) = s
    seq.parTupled

  def parTupled4[A0, A1, A2, A3](io1: IO[A0], io2: IO[A1], io3: IO[A2], io4: IO[A3]): IO[(A0, A1, A2, A3)] = (io1, io2, io3, io4).parTupled

  implicit def toTuple4[A](x: Seq[IO[A]]): (IO[A], IO[A], IO[A], IO[A]) =
    x match {
      case Seq(a, b, c, d) => (a, b, c, d)
    }

  def parTupled4Seq[A](s: Seq[IO[A]]): IO[(A, A, A, A)] =
    val seq: (IO[A], IO[A], IO[A], IO[A]) = s
    seq.parTupled
