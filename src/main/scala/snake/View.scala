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

  //ridefinisce il metodo per la creazione della view
  override def createView(gs: ObjectProperty[GS]) = new JFXApp3.PrimaryStage {
    width = 600
    height = 635

    //crea una scena
    scene = new Scene {

      //titolo tel gioco e punteggio massimo raggiunto
      title = "SNAKE | Highscore: " + gs.value.value

      fill = Black //colore di background
      content = gs.value.shapes  //elementi della scena
      onKeyPressed = key => readKeys(key) //listener per i comandi

      //aggiorna il content della scena ad ogni cambiamento di stato
      gs.onChange(Platform.runLater {
        content = gs.value.shapes
      })
    }
  }
