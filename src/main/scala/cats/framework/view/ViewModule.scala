package cats.framework.view

import cats.Monad
import cats.effect.{IO, Sync}
import cats.effect.std.Console
import cats.implicits.catsSyntaxFlatMapOps
import javafx.stage.Stage
import scalafx.application.JFXApp3
import cats.Applicative.ops.toAllApplicativeOps

object ViewModule:

  sealed trait Component extends View:

    //istanza della view
    val viewObj: Unit = View.apply()

    //aggiunge l'estensione trace al tipo IO per stampare il nome del thread in esecuzione
    extension[A] (io: IO[A])
      def trace: IO[A] = for {
        res <- io
        _ = println(s"[${Thread.currentThread.getName}] " + res)
      } yield res

  trait Interface extends Component