package cats.framework.controller.readwritefiles

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.framework.controller.readwritefiles.ReadAndWrite

import scala.io.Source

private[controller] trait CatsBracket extends ReadAndWrite:

  /* LETTURA */
  //acquisizione, lettura e chiusura in lettura di una risorsa
  def bracketReading(path: String): IO[String] =
    sourceIO(path).bracket(src => readLines(src))(src => closeResource(src))

  /* SCRITTURA */
  //acquisizione, lettura e chiusura in scrittura di una risorsa
  def bracketWriting(content: String, path: String): IO[Unit] =
    writerIO(path).bracket(fw => writeLines(fw, content))(fw => closeWriteFile(fw))

  /* LETTURA E SCRITTURA */
  //legge e scrive su una risorsa con bracket
  def bracketReadAndWrite(pathR: String, pathW: String) = sourceIO(pathR).bracket { src =>
    val contentsIO = readLines(src)
    writerIO(pathW).bracket(fw =>
      contentsIO.flatMap(contents => writeLines(fw, contents))
    )(fw => closeWriteFile(fw))
  } { src =>
    closeResource(src)
  }




