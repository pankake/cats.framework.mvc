package cats.framework.controller

import cats.effect.IO
import scalafx.beans.property.ObjectProperty
import scalafx.scene.shape.{Rectangle, Shape}

trait GS:
  def shapes: List[Shape]
  def newState(s: Any): GS
  val value: String



