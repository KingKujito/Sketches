package imagePrinter

import java.awt.image.BufferedImage
import java.awt.{Color, Image}
import java.io.File
import java.net.URL

import javax.imageio.ImageIO
import MoreColors._

import scala.io.StdIn
import scala.util.Try

object ImgToAscii {

  val lightOnDark   :   Boolean       = StdIn.readLine("Are you using a console with light text on a dark background? y/n") == "y"
  val printType     :   PrintType     = StdIn.readLine(
    """Pick a type to print in:
      |  [1] EmojiT
      |  [2] HtmlT
      |  [3] PlainT""".stripMargin) match {
    case "1" => EmojiT
    case "2" => HtmlT
    case "3" => PlainT
    case _ => println("Could not recognize input. Defaulting to PlainT"); PlainT
  }
  val useChar       :   Boolean       = StdIn.readLine("Use char? y/n") == "y"
  lazy val _8bit    :   Boolean       = printType match {
    case PlainT  => checkFor8bit
    case _       => false
  }
  //determines how squished or streched the image looks. 0.5 for mac terminal, 0.3 for intellij terminal
  val heightMult    :   Float         = printType match {
    case EmojiT  => 0.75f
    case HtmlT   => 0.3f
    case _       => if(_8bit) 0.5f else 0.3f
  }

  /**Query the user for the desired image width.
    * @return The entered width
    */
  def testWidth() : Int = {
    /** Displaying the gradient */
    def widthChecker(width: Int): Unit = {
      val min = 60                          //darkest shade allowed
      for(i<-0 to width) {
        val shade = math.min(i + min, 255)
        printType match {
          case EmojiT => print"${Emoji.emojiRGBs(Emoji.rgbToEmoji(shade,shade,shade)).emoji}"
          case _ =>
            if(_8bit) print"${COLOR_256_B(Xterm.rgbToXterm(shade,shade,shade))} "
            else      print"${COLOR_B(shade,shade,shade)} "
        }
      }
      println("\n^Your width")
    }

    /** The query process */
    @scala.annotation.tailrec
    def userCheck(input: Option[Int]): Int = {
      if (input.isDefined && input.get >= 0 && input.get <= 255) {
        widthChecker(input.get)                                       //display a gradient to give a sense of scale
        StdIn.readLine("This good? y/n").toLowerCase.trim match {
          case "y" | "yes"  => println(s"Good! Your perfect width is: ${input.get}"); input.get
          case "n" | "no"   => println("Try another width.");   testWidth()
          case _            => println("What was that?");       userCheck(input)
        }
      } else {
        println("Not a valid input")
        testWidth()
      }
    }

    userCheck(Try(StdIn.readLine("Type a width (0-255):").trim.toInt).toOption)
  }

  def printImg(path     : String,
               width    : Int,
               useChar  : Boolean = false,
               char     : String  = "\u2588",
               use256   : Boolean = false,
               printT   : PrintType
              ): Option[Image] = {
    /** Try to load an image */
    def image(path : String, width : Int) : Option[Image] = {
      Try {
        println"Trying to load your image..."
        val img                 = try ImageIO.read(new URL(path)) catch { case _: Exception => ImageIO.read(new File(path)) }
        println"Found image with w=${img.getWidth} and h=${img.getHeight}..."
        val newHeight           = ((width.toFloat / img.getWidth.toFloat) * img.getHeight.toFloat)*heightMult
        println"Trying to resize your image to w=$width h=${newHeight.toInt}..."
        img.getScaledInstance(width, newHeight.toInt, Image.SCALE_FAST)
      }.toOption
    }

    val myImage : Option[Image] = image(path, width)

    if(myImage.isDefined) {
      imgToAscii(myImage.get, useChar, char, use256, printT)
    } else {
      println("Couldn't load image... Sorry!")
    }
    myImage
  }

  @scala.annotation.tailrec
  def checkFor8bit: Boolean = { //display a gradient to give a sense of color
    for(i<-0 to 75) {
      val shade = math.min(i + 60, 255)
      print"${COLOR_B(shade,shade,shade)} "
    }
    StdIn.readLine("Do you see a color gradient? y/n").toLowerCase.trim match {
      case "y" | "yes"  => println(s"Good!");                     false
      case "n" | "no"   => println("Switching to 8 bit mode.");   true
      case _            => println("Not a valid input");          checkFor8bit
    }
  }

  /** Convert an Image to Ascii art in the console */
  def imgToAscii(image    : Image,
                 useChar  : Boolean = false,
                 char     : String  = "\u2588",
                 use256   : Boolean = false,
                 printT   : PrintType
                ): Unit = {
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

        printT match {
          case EmojiT => print"${Emoji.emojiRGBs(Emoji.rgbToEmoji(r,g,b)).emoji}"
          case _      =>
            if(use256) {
              if (useChar)
                //print"${COLOR_256(Xterm.rgbToXterm(r,g,b))}$char"
                print"${COLOR_256(Xterm.rgbToXterm(r,g,b))}${Characters.closestChar(Xterm.RGB(r,g,b), lightOnDark)}"
              else
                print"${COLOR_256_B(Xterm.rgbToXterm(r,g,b))} "
            } else {
              if (useChar)
                //print"${COLOR(r, g, b)}$char"
                print"${COLOR(r, g, b)}${Characters.closestChar(Xterm.RGB(r,g,b), lightOnDark)}"
              else
                print"${COLOR_B(r, g, b)} "
            }
        }
      }
      print("\n")
    }
  }

  def main(args: Array[String]): Unit = {
    val myWidth : Int     = testWidth()
    val imgPath : String  = StdIn.readLine("Paste an image file path:")
    printImg(imgPath, myWidth, useChar = useChar, printT = printType, use256 = _8bit)
  }


  def imgToHtml (path : String, char: String = "#") : String = {
    var s = ""
    printImg(path, 30, printT = HtmlT) match {
      case Some(image) =>
        println"Trying to render your image..."
        val img = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB)
        img.getGraphics.drawImage(image, 0, 0 , null)
        println"Your image size is w=${img.getWidth} and h=${img.getHeight}..."
        for (y <- 0 until img.getHeight) {
          //s += "<p>"
          for (x <- 0 until img.getWidth) {
            val col = new Color(img.getRGB(x, y))
            val r = col.getRed
            val g = col.getGreen
            val b = col.getBlue
            s += s"""<span style="background-color: rgb($r, $g, $b); color: rgb($r, $g, $b);">"""
            s += char
            s += "</span>"
          }
          s += "<br>"
        }
    }

    """<b>"""+ s +
    """</b>"""
  }
}
