//package utils         //comment me out to run scala script, uncomment me to use in project
//runs fine in IntelliJ terminal. Mac Os High Sierra's terminal does not seem to support custom colors. Set use256 to true for them.

import java.awt.{Color, Image}
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import Console._
import scala.io.StdIn
import scala.util.Try

object ImgToAscii {

  val heightMult : Float     = 0.3f   //determines how squished or streched the image looks.
  val _8bit      : Boolean   = true

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

  object Xterm {

    case class RGB (r: Int, g: Int, b: Int, index: Int = 0) {
      def dist(that : RGB) : Int = {
        math.abs(that.r - this.r) + math.abs(that.g - this.g) + math.abs(that.b - this.b)
      }
    }

    //see https://jonasjacek.github.io/colors/
    //counting starts at 1
    val COLOR256 : Seq[RGB] = Seq(
      RGB(0  ,   0,   0,  1),
      RGB(128,   0,   0,  2),
      RGB(0  , 128,   0,  3),
      RGB(128, 128,   0,  4),
      RGB(0  ,   0, 128,  5),
      RGB(128,   0, 128,  6),
      RGB(0  , 128, 128,  7),
      RGB(192, 192, 192,  8),
      RGB(128, 128, 128,  9),
      RGB(255,   0,   0,  10),
      RGB(0  , 255,   0,  11),
      RGB(255, 255,   0,  12),
      RGB(0  ,   0, 255,  13),
      RGB(255,   0, 255,  14),
      RGB(0  , 255, 255,  15),
      RGB(255, 255, 255,  16),
      RGB(0  ,   0,   0,  17),
      RGB(0  ,   0,  95,  18),
      RGB(0  ,   0, 135,  19),
      RGB(0  ,   0, 175,  20),
      RGB(0  ,   0, 215,  21),
      RGB(0  ,   0, 255,  22),
      RGB(0  ,  95,   0,  23),
      RGB(0  ,  95,  95,  24),
      RGB(0  ,  95, 135,  25),
      RGB(0  ,  95, 175,  26),
      RGB(0  ,  95, 215,  27),
      RGB(0  ,  95, 255,  28),
      RGB(0  , 135,   0,  29),
      RGB(0  , 135,  95,  30),
      RGB(0  , 135, 135,  31),
      RGB(0  , 135, 175,  32),
      RGB(0  , 135, 215,  33),
      RGB(0  , 135, 255,  34),
      RGB(0  , 175,   0,  35),
      RGB(0  , 175,  95,  36),
      RGB(0  , 175, 135,  37),
      RGB(0  , 175, 175,  38),
      RGB(0  , 175, 215,  39),
      RGB(0  , 175, 255,  40),
      RGB(0  , 215,   0,  41),
      RGB(0  , 215,  95,  42),
      RGB(0  , 215, 135,  43),
      RGB(0  , 215, 175,  44),
      RGB(0  , 215, 215,  45),
      RGB(0  , 215, 255,  46),
      RGB(0  , 255,   0,  47),
      RGB(0  , 255,  95,  48),
      RGB(0  , 255, 135,  49),
      RGB(0  , 255, 175,  50),
      RGB(0  , 255, 215,  51),
      RGB(0  , 255, 255,  52),
      RGB(95 ,   0,   0,  53),
      RGB(95 ,   0,  95,  54),
      RGB(95 ,   0, 135,  55),
      RGB(95 ,   0, 175,  56),
      RGB(95 ,   0, 215,  57),
      RGB(95 ,   0, 255,  58),
      RGB(95 ,  95,   0,  59),
      RGB(95 ,  95,  95,  60),
      RGB(95 ,  95, 135,  61),
      RGB(95 ,  95, 175,  62),
      RGB(95 ,  95, 215,  63),
      RGB(95 ,  95, 255,  64),
      RGB(95 , 135,   0,  65),
      RGB(95 , 135,  95,  66),
      RGB(95 , 135, 135,  67),
      RGB(95 , 135, 175,  68),
      RGB(95 , 135, 215,  69),
      RGB(95 , 135, 255,  70),
      RGB(95 , 175,   0,  71),
      RGB(95 , 175,  95,  72),
      RGB(95 , 175, 135,  73),
      RGB(95 , 175, 175,  74),
      RGB(95 , 175, 215,  75),
      RGB(95 , 175, 255,  76),
      RGB(95 , 215,   0,  77),
      RGB(95 , 215,  95,  78),
      RGB(95 , 215, 135,  79),
      RGB(95 , 215, 175,  80),
      RGB(95 , 215, 215,  81),
      RGB(95 , 215, 255,  82),
      RGB(95 , 255,   0,  83),
      RGB(95 , 255,  95,  84),
      RGB(95 , 255, 135,  85),
      RGB(95 , 255, 175,  86),
      RGB(95 , 255, 215,  87),
      RGB(95 , 255, 255,  88),
      RGB(135,   0,   0,  89),
      RGB(135,   0,  95,  90),
      RGB(135,   0, 135,  91),
      RGB(135,   0, 175,  92),
      RGB(135,   0, 215,  93),
      RGB(135,   0, 255,  94),
      RGB(135,  95,   0,  95),
      RGB(135,  95,  95,  96),
      RGB(135,  95, 135,  97),
      RGB(135,  95, 175,  98),
      RGB(135,  95, 215,  99),
      RGB(135,  95, 255,  100),
      RGB(135, 135,   0,  101),
      RGB(135, 135,  95,  102),
      RGB(135, 135, 135,  103),
      RGB(135, 135, 175,  104),
      RGB(135, 135, 215,  105),
      RGB(135, 135, 255,  106),
      RGB(135, 175,   0,  107),
      RGB(135, 175,  95,  108),
      RGB(135, 175, 135,  109),
      RGB(135, 175, 175,  110),
      RGB(135, 175, 215,  111),
      RGB(135, 175, 255,  112),
      RGB(135, 215,   0,  113),
      RGB(135, 215,  95,  114),
      RGB(135, 215, 135,  115),
      RGB(135, 215, 175,  116),
      RGB(135, 215, 215,  117),
      RGB(135, 215, 255,  118),
      RGB(135, 255,   0,  119),
      RGB(135, 255,  95,  120),
      RGB(135, 255, 135,  121),
      RGB(135, 255, 175,  122),
      RGB(135, 255, 215,  123),
      RGB(135, 255, 255,  124),
      RGB(175,   0,   0,  125),
      RGB(175,   0,  95,  126),
      RGB(175,   0, 135,  127),
      RGB(175,   0, 175,  128),
      RGB(175,   0, 215,  129),
      RGB(175,   0, 255,  130),
      RGB(175,  95,   0,  131),
      RGB(175,  95,  95,  132),
      RGB(175,  95, 135,  133),
      RGB(175,  95, 175,  134),
      RGB(175,  95, 215,  135),
      RGB(175,  95, 255,  136),
      RGB(175, 135,   0,  137),
      RGB(175, 135,  95,  138),
      RGB(175, 135, 135,  139),
      RGB(175, 135, 175,  140),
      RGB(175, 135, 215,  141),
      RGB(175, 135, 255,  142),
      RGB(175, 175,   0,  143),
      RGB(175, 175,  95,  144),
      RGB(175, 175, 135,  145),
      RGB(175, 175, 175,  146),
      RGB(175, 175, 215,  147),
      RGB(175, 175, 255,  148),
      RGB(175, 215,   0,  149),
      RGB(175, 215,  95,  150),
      RGB(175, 215, 135,  151),
      RGB(175, 215, 175,  152),
      RGB(175, 215, 215,  153),
      RGB(175, 215, 255,  154),
      RGB(175, 255,   0,  155),
      RGB(175, 255,  95,  156),
      RGB(175, 255, 135,  157),
      RGB(175, 255, 175,  158),
      RGB(175, 255, 215,  159),
      RGB(175, 255, 255,  160),
      RGB(215,   0,   0,  161),
      RGB(215,   0,  95,  162),
      RGB(215,   0, 135,  163),
      RGB(215,   0, 175,  164),
      RGB(215,   0, 215,  165),
      RGB(215,   0, 255,  166),
      RGB(215,  95,   0,  167),
      RGB(215,  95,  95,  168),
      RGB(215,  95, 135,  169),
      RGB(215,  95, 175,  170),
      RGB(215,  95, 215,  171),
      RGB(215,  95, 255,  172),
      RGB(215, 135,   0,  173),
      RGB(215, 135,  95,  174),
      RGB(215, 135, 135,  175),
      RGB(215, 135, 175,  176),
      RGB(215, 135, 215,  177),
      RGB(215, 135, 255,  178),
      RGB(215, 175,   0,  179),
      RGB(215, 175,  95,  180),
      RGB(215, 175, 135,  181),
      RGB(215, 175, 175,  182),
      RGB(215, 175, 215,  183),
      RGB(215, 175, 255,  184),
      RGB(215, 215,   0,  185),
      RGB(215, 215,  95,  186),
      RGB(215, 215, 135,  187),
      RGB(215, 215, 175,  188),
      RGB(215, 215, 215,  189),
      RGB(215, 215, 255,  190),
      RGB(215, 255,   0,  191),
      RGB(215, 255,  95,  192),
      RGB(215, 255, 135,  193),
      RGB(215, 255, 175,  194),
      RGB(215, 255, 215,  195),
      RGB(215, 255, 255,  196),
      RGB(255,   0,   0,  197),
      RGB(255,   0,  95,  198),
      RGB(255,   0, 135,  199),
      RGB(255,   0, 175,  200),
      RGB(255,   0, 215,  201),
      RGB(255,   0, 255,  202),
      RGB(255,  95,   0,  203),
      RGB(255,  95,  95,  204),
      RGB(255,  95, 135,  205),
      RGB(255,  95, 175,  206),
      RGB(255,  95, 215,  207),
      RGB(255,  95, 255,  208),
      RGB(255, 135,   0,  209),
      RGB(255, 135,  95,  210),
      RGB(255, 135, 135,  211),
      RGB(255, 135, 175,  212),
      RGB(255, 135, 215,  213),
      RGB(255, 135, 255,  214),
      RGB(255, 175,   0,  215),
      RGB(255, 175,  95,  216),
      RGB(255, 175, 135,  217),
      RGB(255, 175, 175,  218),
      RGB(255, 175, 215,  219),
      RGB(255, 175, 255,  220),
      RGB(255, 215,   0,  221),
      RGB(255, 215,  95,  222),
      RGB(255, 215, 135,  223),
      RGB(255, 215, 175,  224),
      RGB(255, 215, 215,  225),
      RGB(255, 215, 255,  226),
      RGB(255, 255,   0,  227),
      RGB(255, 255,  95,  228),
      RGB(255, 255, 135,  229),
      RGB(255, 255, 175,  230),
      RGB(255, 255, 215,  231),
      RGB(255, 255, 255,  232),
      RGB(8  ,   8,   8,  233),
      RGB(18 ,  18,  18,  234),
      RGB(28 ,  28,  28,  235),
      RGB(38 ,  38,  38,  236),
      RGB(48 ,  48,  48,  237),
      RGB(58 ,  58,  58,  238),
      RGB(68 ,  68,  68,  239),
      RGB(78 ,  78,  78,  240),
      RGB(88 ,  88,  88,  241),
      RGB(98 ,  98,  98,  242),
      RGB(108, 108, 108,  243),
      RGB(118, 118, 118,  244),
      RGB(128, 128, 128,  245),
      RGB(138, 138, 138,  246),
      RGB(148, 148, 148,  247),
      RGB(158, 158, 158,  248),
      RGB(168, 168, 168,  249),
      RGB(178, 178, 178,  250),
      RGB(188, 188, 188,  251),
      RGB(198, 198, 198,  252),
      RGB(208, 208, 208,  253),
      RGB(218, 218, 218,  254),
      RGB(228, 228, 228,  255),
      RGB(238, 238, 238,  256)
    )

    def rgbToXterm(r: Int, g: Int, b: Int): Int = {
      val compRgb = RGB(r,g,b)
      COLOR256.minBy(compRgb.dist).index-1
    }
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
        if(_8bit) print"${COLOR_256_B(Xterm.rgbToXterm(shade,shade,shade))} "
        else      print"${COLOR_B(shade,shade,shade)} "
      }
      println("\n^Your width")
    }
    userCheck(Try(StdIn.readLine("Type a width (0-255):").toInt).toOption)
  }

  def printImg(path     : String,
               width    : Int,
               useChar  : Boolean = false,
               char     : String  = "\u2588",
               use256   : Boolean = false
              ): Unit = {
    /** Try to load an image */
    def image(path : String, width : Int) : Option[Image] = {
      Try {
        println"Trying to load your image..."
        val img                 = ImageIO.read(new File(path))
        println"Found image with w=${img.getWidth} and h=${img.getHeight}..."
        val newHeight           = ((width.toFloat / img.getWidth.toFloat) * img.getHeight.toFloat)*heightMult
        println"Trying to resize your image to w=$width h=${newHeight.toInt}..."
        img.getScaledInstance(width, newHeight.toInt, Image.SCALE_FAST)
      }.toOption
    }

    val myImage : Option[Image] = image(path, width)

    if(myImage.isDefined) {
      imgToAscii(myImage.get, useChar, char, use256)
    } else {
      println("Couldn't load image... Sorry!")
    }
  }

  /** Convert an Image to Ascii art in the console */
  def imgToAscii(image    : Image,
                 useChar  : Boolean = false,
                 char     : String  = "\u2588",
                 use256   : Boolean = false
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
        if(use256) {
          if (useChar)
            print"${COLOR_256(Xterm.rgbToXterm(r,g,b))}$char"
          else
            print"${COLOR_256_B(Xterm.rgbToXterm(r,g,b))} "
        } else {
          if (useChar)
            print"${COLOR(r, g, b)}$char"
          else
            print"${COLOR_B(r, g, b)} "
        }
      }
      print("\n")
    }
  }

  def main(args: Array[String]): Unit = {
    val myWidth : Int     = testWidth()
    val imgPath : String  = StdIn.readLine("Paste an image file path:")

    printImg(imgPath, myWidth, use256 = _8bit)

  }

}