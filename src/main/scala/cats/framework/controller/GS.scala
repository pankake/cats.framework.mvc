package cats.framework.controller

import cats.effect.IO
import scalafx.beans.property.ObjectProperty
import scalafx.scene.shape.{Rectangle, Shape}

//tratto che modella lo stato per la view
trait GS:
  //entità che formano il contenuto della view
  def shapes: List[Shape]
  //generazione di un nuovo stato
  def newState(s: Any): GS
  //valore generico
  val value: String



