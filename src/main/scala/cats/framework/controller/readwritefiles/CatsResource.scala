package cats.framework.controller.readwritefiles

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.framework.controller.readwritefiles.ReadAndWrite

import java.io.FileWriter
import scala.io.Source

private[controller] trait CatsResource extends ReadAndWrite:

  /* LETTURA */
  //apertura e rilascio di una risorsa in lettura
  def makeResourceForRead(path: String): Resource[IO, Source] =
    Resource.make(sourceIO(path))(src => closeResource(src))

  //legge una risorsa
  def readWithResource(path: String): IO[String] =
    makeResourceForRead(path).use(src => readLines(src))

  /* SCRITTURA */
  //apertura e rilascio di una risorsa in scrittura
  def makeResourceForWrite(path: String): Resource[IO, FileWriter] =
    Resource.make(writerIO(path))(fw => closeWriteFile(fw))

  //scrittura di una risorsa
  def writeWithResource(content: String, path: String): IO[Unit] = for {
    _ <- IO.println("entrato")
    _ <- makeResourceForWrite(path).use(fw => writeLines(fw, content))
  } yield ()


