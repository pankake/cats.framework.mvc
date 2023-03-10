package snake

import cats.effect.IO
import cats.framework.model.ModelModule
import cats.framework.view.ViewModule
import javafx.stage.Stage
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.property.{IntegerProperty, ObjectProperty}
import scalafx.scene.control.{Label, TextField}
import scalafx.scene.{Group, Scene, text}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Font, FontPosture, FontWeight, Text}
import cats.effect.unsafe.implicits.global
import cats.framework.controller.GS

trait View extends JFXApp3 with Controller:

  override def createView(gs: ObjectProperty[GS]) = new JFXApp3.PrimaryStage {
    width = 600
    height = 635

    scene = new Scene {
      title = "SNAKE | Highscore: " + gs.value.value

      fill = Black //colore di background
      content = gs.value.shapes  //elementi della scena
      onKeyPressed = key => readKeys(key) //listener per i comandi

      gs.onChange(Platform.runLater {
        content = gs.value.shapes //aggiorna il content della scena ad ogni cambiamento di stato
      })
    }
  }
