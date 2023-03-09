package cats.framework.model

import cats.effect.IO

trait ErrorHandling:

  //solleva un'eccezione
  def failedIO(t: Throwable): IO[Any] = IO.raiseError[Any](t)

  //solleva eccezione quando si verifica la condizione data
  def raisedConditionIO(cond: => Boolean, t: Throwable): IO[Any] = IO.raiseWhen(cond)(t)

  //gestisce l'eccezione di un IO
  def handledErrorIO(io: IO[Any], handleErr:Throwable => Any): IO[Any] = io.handleError(handleErr)

  //gestisce l'eccezione di un IO con un altro IO
  def handledErrorWithIO(io: IO[Any], handleErr:Throwable => IO[Any]): IO[Any] = io.handleErrorWith(handleErr)

  //descrive cosa nei casi successo ed errore di un IO
  def redeemedIO(io: IO[Any], handleErr:Throwable => Any, toDoIfSuccess:Any => Any): IO[Any] = io.redeem(handleErr, toDoIfSuccess)


