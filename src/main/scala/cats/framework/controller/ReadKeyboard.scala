package cats.framework.controller

import cats.effect.{IO, Sync}
import cats.effect.std.Console
import javafx.scene.input.KeyEvent

private trait ReadKeyboard:

  def readKeys(keyEvent: KeyEvent): Any = keyEvent.getText

  def readLine[F[_]: Sync: Console]: F[String] = Console[F].readLine


