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
import scalafx.scene.shape.Rectangle

trait View:

  def createView(gs: ObjectProperty[GS]): Unit =
    val stage = new PrimaryStage {
      title = "ScalaFX App"
      scene = new Scene {
        root = new StackPane {
          content = gs.value.shapes
        }
      }
    }

private object View:
  def apply(): Unit =
    JFXAppImpl()
  private class JFXAppImpl extends View
