package utils         //comment me out to run scala script, uncomment me to use in project
//runs fine in IntelliJ terminal. Mac Os High Sierra's terminal does not seem to support custom colors
//TODO rgb to xterm (256 color) converter using vector 3 distance https://jonasjacek.github.io/colors/

import java.awt.{Color, Image}
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import Console._
import scala.io.StdIn
import scala.util.Try

object ImgToAscii {

  /**Ansi codes for Console output.
    */
  object MoreColors {
    //Foreground
    def BRIGHT_BLACK      = "\u001B[90m"
    def BRIGHT_RED        = "\u001B[91m"
    def BRIGHT_GREEN      = "\u001B[92m"
    def BRIGHT_YELLOW     = "\u001B[93m"
    def BRIGHT_BLUE       = "\u001B[94m"
    def BRIGHT_MAGENTA    = "\u001B[95m"
    def BRIGHT_CYAN       = "\u001B[96m"
    def BRIGHT_WHITE      = "\u001B[97m"

    //Background
    def BRIGHT_BLACK_B    = "\u001B[100m"
    def BRIGHT_RED_B      = "\u001B[101m"
    def BRIGHT_GREEN_B    = "\u001B[102m"
    def BRIGHT_YELLOW_B   = "\u001B[103m"
    def BRIGHT_BLUE_B     = "\u001B[104m"
    def BRIGHT_MAGENTA_B  = "\u001B[105m"
    def BRIGHT_CYAN_B     = "\u001B[106m"
    def BRIGHT_WHITE_B    = "\u001B[107m"

    //foreground
    def COLOR(r : Int, g : Int, b : Int)      = s"\u001B[38;2;$r;$g;${b}m"
    def COLOR_256(n : Int)                    = s"\u001B[38;5;${n}m"

    //background
    def COLOR_B(r : Int, g : Int, b : Int)    = s"\u001B[48;2;$r;$g;${b}m"
    def COLOR_256_B(n : Int)                  = s"\u001B[48;5;${n}m"
  }

  /**String interpolators for quick and easy printing with clean styles
    */
  implicit class MyInter(val sc: StringContext) extends AnyVal {
    def print(args: Any*): Unit = {
      val strings = sc.parts.iterator
      val expressions = args.iterator
      val buf = new StringBuffer(strings.next)
      while(strings.hasNext) {
        buf append expressions.next
        buf append strings.next
      }
      Console.print(s"$RESET${buf.toString}$RESET")
    }
    def println(args: Any*): Unit = {
      val strings = sc.parts.iterator
      val expressions = args.iterator
      val buf = new StringBuffer(strings.next)
      while(strings.hasNext) {
        buf append expressions.next
        buf append strings.next
      }
      Console.println(s"$RESET${buf.toString}$RESET")
    }
  }

  import MoreColors._

  /**Query the user for the desired image width.
    * @return The entered width
    */
  def testWidth() : Int = {
    /** The query process */
    def userCheck(input: Option[Int]): Int = {
      if (input.isDefined && input.get >= 0 && input.get <= 255) {
        widthChecker(input.get)                                       //display a gradient to give a sense of scale
        StdIn.readLine("This good? y/n").toLowerCase match {
          case "y" | "yes"  => println(s"Good! Your perfect width is: ${input.get}"); input.get
          case "n" | "no"   => println("Try another width.");   testWidth()
          case _            => println("What was that?");       userCheck(input)
        }
      } else {
        println("Not a valid input")
        testWidth()
      }
    }
    /** Displaying the gradient */
    def widthChecker(width: Int): Unit = {
      val min = 60                          //darkest shade allowed
      for(i<-0 to width) {
        val shade = math.min(i + min, 255)
        print"${COLOR_B(shade,shade,shade)} "
      }
      println("\n^Your width")
    }
    userCheck(Try(StdIn.readLine("Type a width (0-255):").toInt).toOption)
  }

  def printImg(path : String, width : Int, useChar : Boolean = false, char : String = "\u2588"): Unit = {
    /** Try to load an image */
    def image(path : String, width : Int) : Option[Image] = {
      Try {
        println"Trying to load your image..."
        val img                 = ImageIO.read(new File(path))
        println"Found image with w=${img.getWidth} and h=${img.getHeight}..."
        val newHeight           = ((width.toFloat / img.getWidth.toFloat) * img.getHeight.toFloat)*0.3f
        println"Trying to resize your image to w=$width h=${newHeight.toInt}..."
        img.getScaledInstance(width, newHeight.toInt, Image.SCALE_FAST)
      }.toOption
    }

    val myImage : Option[Image] = image(path, width)

    if(myImage.isDefined) {
      imgToAscii(myImage.get, useChar, char)
    } else {
      println("Couldn't load image... Sorry!")
    }
  }

  /** Convert an Image to Ascii art in the console */
  def imgToAscii(image: Image, useChar : Boolean = false, char : String = "\u2588"): Unit = {
    println"Trying to render your image..."
    val img = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB)
    img.getGraphics.drawImage(image, 0, 0 , null)
    println"Your image size is w=${img.getWidth} and h=${img.getHeight}..."
    for (y <- 0 until img.getHeight) {
      for (x <- 0 until img.getWidth) {
        val col   = new Color(img.getRGB(x, y))
        val r     = col.getRed
        val g     = col.getGreen
        val b     = col.getBlue
        if (useChar)
          print"${COLOR(r, g, b)}$char"
        else
          print"${COLOR_B(r, g, b)} "
      }
      print("\n")
    }
  }

  def main(args: Array[String]): Unit = {
    val myWidth : Int     = testWidth()
    val imgPath : String  = StdIn.readLine("Paste an image file path:")

    printImg(imgPath, myWidth)

  }

}