package imagePrinter

object Characters {
  import Xterm._
  val inverse     = false
  val baseRamp    = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. "
  val ramp:String = if(inverse) baseRamp else baseRamp.reverse

  case class Character(char: Char, density: Int)

  lazy val chars: List[Character] = ramp.foldLeft[List[Character]](List.empty[Character]) {
    (x, y) => x.::(Character(y, 255 - x.length * 255 / ramp.length))
  }

  def closestChar(rgb: RGB, inverse: Boolean = false): Char = {
    val gray = (rgb.r + rgb.g + rgb.b) / 3
    val distTable = chars
      .map(c => (c.char, if(c.density == gray) 0 else if(c.density > gray) c.density - gray else gray-c.density))

    if(inverse)
      distTable.sortWith(_._2 > _._2).head._1
    else
      distTable.sortWith(_._2 < _._2).head._1
  }

}
