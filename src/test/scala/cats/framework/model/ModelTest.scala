package cats.framework.model

import cats.effect.{IO, Ref, Sync, SyncIO}
import cats.framework.controller.ControllerModule
import munit.CatsEffectSuite
import org.scalatest.matchers.should.Matchers.{convertToStringShouldWrapperForVerb, equal}
import snake.SnakeFx.initialSnake
import org.scalatest.matchers.should.Matchers.should
import cats.implicits.*

class ModelTest extends CatsEffectSuite with AtomicRef:

  test("test refGet with List") {
    def p: IO[List[(Double, Double)]] =
      for {
        ref <- refCreate(initialSnake)
        output <- refGet(ref)
      } yield output.list

    val result = p.unsafeRunSync()
    IO(assertEquals(initialSnake.list, result))
  }

  test("test refGet with Int") {
    def p: IO[Int] =
      for {
        ref <- refCreate(5)
        output <- refGet(ref)
      } yield output

    val result = p.unsafeRunSync()
    IO(assertEquals(result, 5))
  }

  test("test refUpdateAndGetNewVal with List") {
    def p: IO[List[(Double, Double)]] =
      for {
        ref <- refCreate(initialSnake.list)
        output <- refUpdateAndGetNewVal(ref)(x => (275.0, 200.0) :: x)
      } yield output

    val result = p.unsafeRunSync()

    result.head should equal ((275.0, 200.0))
    result should equal((275.0, 200.0) :: initialSnake.list)
  }

  test("test refUpdateAndGetNewVal with Int") {
    def p: IO[Int] =
      for {
        ref <- refCreate(5)
        output <- refUpdateAndGetNewVal(ref)(x => 5 + x)
      } yield output

    val result = p.unsafeRunSync()

    result should equal(10)
  }

  test("test refModifyUntilSucced with List") {
    def p: IO[List[(Double, Double)]] =
      for {
        ref <- refCreate(initialSnake)
        output <- refModifyUntilSucceed(ref)(current => (current, (275.0, 200.0) :: current.list))
      } yield output

    val result = p.unsafeRunSync()

    result.head should equal ((275.0, 200.0))
    result should equal((275.0, 200.0) :: initialSnake.list)
  }

  test("test refModifyUntilSucced with Int") {
    def p: IO[Int] =
      for {
        ref <- refCreate(5)
        output <- refModifyUntilSucceed(ref)(x => (x, x + 5))
      } yield output

    val result = p.unsafeRunSync()

    result should equal(10)
  }


