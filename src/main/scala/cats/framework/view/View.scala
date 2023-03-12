package cats.framework.view

import cats.framework.controller.GS
import cats.framework.model.AtomicRef
import javafx.collections.ObservableList
import javafx.stage.Stage
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.application.Platform
import scalafx.beans.property.ObjectProperty
import scalafx.scene.Scene
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Rectangle, Shape}

trait View:

  //crea una view
  def createView(gs: ObjectProperty[GS]): Unit = new PrimaryStage {
      title = "ScalaFX App"
      scene = createScene(gs)
    }

  //crea una scena
  def createScene(gs: ObjectProperty[GS]): Scene =
    new Scene {
      root = new StackPane {
        content = gs.value.shapes

        gs.onChange(Platform.runLater {
          content = gs.value.shapes //aggiorna il content della scena ad ogni cambiamento di stato
        })
      }
    }

object View:
  def apply(): Unit =
    ViewImpl()
  private class ViewImpl extends View
