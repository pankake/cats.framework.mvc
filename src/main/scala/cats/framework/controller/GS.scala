package cats.framework.controller

import scalafx.beans.property.ObjectProperty
import scalafx.scene.shape.{Rectangle, Shape}

trait GS:
  def shapes: List[Shape]
  def newState(dir: Int): GS
  val score: String



