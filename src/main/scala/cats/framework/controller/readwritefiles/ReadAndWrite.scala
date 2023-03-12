package cats.framework.controller.readwritefiles

import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits.catsSyntaxTuple2Semigroupal

import java.io.*
import scala.io.Source
import scala.language.reflectiveCalls

private[controller] trait ReadAndWrite:

  /* LETTURA */
  //apertura di un file
  def sourceIO(path: String): IO[Source] = IO(Source.fromFile(path))

  //lettura di un file
  def readLines(source: Source): IO[String] = IO(source.getLines().mkString("\n")) <* IO.println("file reading completed")

  //chiusura di un file
  def closeResource(source: Source): IO[Unit] = IO(source.close())


  /* SCRITTURA */
  //apre un file in scrittura
  def writerIO(path: String): IO[FileWriter] =
    IO.println("Acquiring file to write") >> IO(
      new FileWriter(path)
    )

  //scrive su un file
  def writeLines(writer: FileWriter, content: String): IO[Unit] =
    IO.println("Writing the contents to file") >> IO(writer.write(content)) <* IO.println("writing completed")

  //chiusura del file aperto in scrittura
  def closeWriteFile(writer: FileWriter): IO[Unit] =
    IO.println("Closing the file writer") >> IO(writer.close())
