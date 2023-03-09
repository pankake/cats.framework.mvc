package cats.framework.controller.readwritefiles

import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits.catsSyntaxTuple2Semigroupal

import java.io.*
import scala.io.Source
import scala.language.reflectiveCalls

private[controller] trait ReadAndWrite:

  /* LETTURA */
  def sourceIO(path: String): IO[Source] = IO(Source.fromFile(path))

  def readLines(source: Source): IO[String] = IO(source.getLines().mkString("\n")) <* IO.println("file reading completed")

  def closeResource(source: Source): IO[Unit] = IO(source.close())


  /* SCRITTURA */
  def writerIO(path: String): IO[FileWriter] =
    IO.println("Acquiring file to write") >> IO(
      new FileWriter(path)
    )

  def writeLines(writer: FileWriter, content: String): IO[Unit] =
    IO.println("Writing the contents to file") >> IO(writer.write(content)) <* IO.println("writing completed")

  def closeWriteFile(writer: FileWriter): IO[Unit] =
    IO.println("Closing the file writer") >> IO(writer.close())


/*object Main extends IOApp with FileManager {

  override def run(args: List[String]): IO[ExitCode] =

    for {
      _ <- IO.unit
      src <- writeWithResource("1", "/home/daniele/Scrivania/pps/cats.framework.mvc/src/main/resources/highscore")
      //_ <- IO(println(src))
    } yield ExitCode.Success
}*/
