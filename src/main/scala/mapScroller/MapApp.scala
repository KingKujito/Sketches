package mapScroller

import controllers.CoolApp

import scala.io.StdIn

object MapApp {
  def customLoop(output : List[String]):Unit = {
    output.foreach(o => println(s"map\t$o"))
    StdIn.readLine("wasd to move, other key to quit") match {
      case "a" => customLoop(updateMap(output, Left ))
      case "d" => customLoop(updateMap(output, Right))
      case "w" => customLoop(updateMap(output, Up   ))
      case "s" => customLoop(updateMap(output, Down ))
      case x   => println(s"\nEnding app because input '$x' is not an option\n\n"); CoolApp.quit
    }
  }

  sealed  trait   Direction
  sealed  trait   Horizontal extends Direction
  sealed  trait   Vertical   extends Direction
  case    object  Right      extends Horizontal
  case    object  Left       extends Horizontal
  case    object  Up         extends Vertical
  case    object  Down       extends Vertical

  def updateMap(map: List[String], direction: Direction): List[String] = {
    direction match {
      case x : Horizontal => map.map(o => horizontalMovement(o, x))
      case x : Vertical   => verticalMovement(map, x)
    }
  }

  def horizontalMovement(string: String, horizontal: Horizontal) : String = {
    horizontal match {
      case Right =>
        val x = string.reverse
        val y = x.head
        val z = x.tail
        y + z.reverse
      case Left  =>
        string.tail + string.head
    }
  }

  def verticalMovement(map: List[String], horizontal: Vertical) : List[String] = {
    horizontal match {
      case Up =>
        map.tail.::(map.head)
      case Down  =>
        val x = map.reverse
        val y = x.head
        val z = x.tail
        z.reverse.::(y)
    }
  }
}
