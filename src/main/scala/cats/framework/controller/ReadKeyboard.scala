package cats.framework.controller

import cats.effect.{IO, Sync}
import cats.effect.std.Console
import javafx.scene.input.KeyEvent

private trait ReadKeyboard:

  //listener per i tasti che vengono premuti
  def readKeys(keyEvent: KeyEvent): Any = keyEvent.getText

  //lettura delle stringhe immesse nella console
  def readLine[F[_]: Sync: Console]: F[String] = Console[F].readLine


