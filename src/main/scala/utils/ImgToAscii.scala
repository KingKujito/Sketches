package utils         //comment me out to run scala script, uncomment me to use in project
//runs fine in IntelliJ terminal. Mac Os High Sierra's terminal does not seem to support custom colors. Set use256 to true for them.
//for example images see: https://github.com/KingKujito/Sketches/tree/master/files/pdf/ImgToAscii.pdf
//TODO assign an emoji character to each 256 xterm colors to allow easy mobile image printing
//TODO add web image support

import java.awt.{Color, Image}
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL

import javax.imageio.ImageIO

import Console._
import scala.io.StdIn
import scala.util.Try

object ImgToAscii {

  val emoji      : Boolean   = true
  val _8bit      : Boolean   = if (emoji) false else checkFor8bit()
  val heightMult : Float     =
    if(emoji) 0.75f else if(_8bit) 0.5f else 0.3f   //determines how squished or streched the image looks. 0.5 for mac terminal, 0.3 for intellij terminal

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
    //counting starts at 1 because I'm a doofus
    val COLOR256 : Seq[RGB] = Seq(
      RGB(0  ,   0,   0,  1),     //black
      RGB(128,   0,   0,  2),     //maroon
      RGB(0  , 128,   0,  3),     //green
      RGB(128, 128,   0,  4),     //olive
      RGB(0  ,   0, 128,  5),     //navy
      RGB(128,   0, 128,  6),     //purple
      RGB(0  , 128, 128,  7),     //teal
      RGB(192, 192, 192,  8),     //silver
      RGB(128, 128, 128,  9),     //grey
      RGB(255,   0,   0,  10),     //red
      RGB(0  , 255,   0,  11),     //lime
      RGB(255, 255,   0,  12),     //yellow
      RGB(0  ,   0, 255,  13),     //blue
      RGB(255,   0, 255,  14),     //fuchsia
      RGB(0  , 255, 255,  15),     //aqua
      RGB(255, 255, 255,  16),     //white
      RGB(0  ,   0,   0,  17),     //grey0
      RGB(0  ,   0,  95,  18),     //navyBlue
      RGB(0  ,   0, 135,  19),     //darkBlue
      RGB(0  ,   0, 175,  20),     //blue3
      RGB(0  ,   0, 215,  21),     //blue3
      RGB(0  ,   0, 255,  22),     //blue1
      RGB(0  ,  95,   0,  23),     //darkGreen
      RGB(0  ,  95,  95,  24),     //deepSkyBlue4
      RGB(0  ,  95, 135,  25),     //deepSkyBlue4
      RGB(0  ,  95, 175,  26),     //deepSkyBlue4
      RGB(0  ,  95, 215,  27),     //dodgerBlue3
      RGB(0  ,  95, 255,  28),     //dodgerBlue2
      RGB(0  , 135,   0,  29),     //green4
      RGB(0  , 135,  95,  30),     //springGreen4
      RGB(0  , 135, 135,  31),     //turquoise4
      RGB(0  , 135, 175,  32),     //deepSkyBlue3
      RGB(0  , 135, 215,  33),     //deepSkyBlue3
      RGB(0  , 135, 255,  34),     //dodgeBlue1
      RGB(0  , 175,   0,  35),     //green3
      RGB(0  , 175,  95,  36),     //springGreen3
      RGB(0  , 175, 135,  37),     //darkCyan
      RGB(0  , 175, 175,  38),     //lightSeaGreen
      RGB(0  , 175, 215,  39),     //deepSkyBlue2
      RGB(0  , 175, 255,  40),     //deepSkyBlue1
      RGB(0  , 215,   0,  41),     //green3
      RGB(0  , 215,  95,  42),     //springGreen3
      RGB(0  , 215, 135,  43),     //springGreen2
      RGB(0  , 215, 175,  44),     //cyan3
      RGB(0  , 215, 215,  45),     //darkTurquoise
      RGB(0  , 215, 255,  46),     //turquoise2
      RGB(0  , 255,   0,  47),     //green1
      RGB(0  , 255,  95,  48),     //springGreen2
      RGB(0  , 255, 135,  49),     //springGreen1
      RGB(0  , 255, 175,  50),     //mediumSpringGreen
      RGB(0  , 255, 215,  51),     //cyan2
      RGB(0  , 255, 255,  52),     //cyan1
      RGB(95 ,   0,   0,  53),     //darkRed
      RGB(95 ,   0,  95,  54),     //deepPink4
      RGB(95 ,   0, 135,  55),     //purple4
      RGB(95 ,   0, 175,  56),     //purple4
      RGB(95 ,   0, 215,  57),     //purple3
      RGB(95 ,   0, 255,  58),     //blueViolet
      RGB(95 ,  95,   0,  59),     //orange4
      RGB(95 ,  95,  95,  60),     //grey37
      RGB(95 ,  95, 135,  61),     //mediumPurple4
      RGB(95 ,  95, 175,  62),     //slateBlue3
      RGB(95 ,  95, 215,  63),     //slateBlue3
      RGB(95 ,  95, 255,  64),     //royalBlue1
      RGB(95 , 135,   0,  65),     //chartreuse4
      RGB(95 , 135,  95,  66),     //darkSeaGreen4
      RGB(95 , 135, 135,  67),     //paleTurquoise4
      RGB(95 , 135, 175,  68),     //steelBlue
      RGB(95 , 135, 215,  69),     //steelBlue3
      RGB(95 , 135, 255,  70),     //cornflowerBlue
      RGB(95 , 175,   0,  71),     //chartreuse3
      RGB(95 , 175,  95,  72),     //darkSeaGreen4
      RGB(95 , 175, 135,  73),     //cadetBlue
      RGB(95 , 175, 175,  74),     //cadetBlue
      RGB(95 , 175, 215,  75),     //skyBlue3
      RGB(95 , 175, 255,  76),     //steelBlue1
      RGB(95 , 215,   0,  77),     //chartreuse3
      RGB(95 , 215,  95,  78),     //paleGreen3
      RGB(95 , 215, 135,  79),     //seaGreen3
      RGB(95 , 215, 175,  80),     //aquamarine3
      RGB(95 , 215, 215,  81),     //mediumTurquoise
      RGB(95 , 215, 255,  82),     //steelBlue1
      RGB(95 , 255,   0,  83),     //chartreuse2
      RGB(95 , 255,  95,  84),     //seaGreen2
      RGB(95 , 255, 135,  85),     //seaGreen1
      RGB(95 , 255, 175,  86),     //seaGreen1
      RGB(95 , 255, 215,  87),     //aquamarine1
      RGB(95 , 255, 255,  88),     //darkSlateGray2
      RGB(135,   0,   0,  89),     //darkRed
      RGB(135,   0,  95,  90),     //deepPink4
      RGB(135,   0, 135,  91),     //darkMagenta
      RGB(135,   0, 175,  92),     //darkMagenta
      RGB(135,   0, 215,  93),     //darkViolet
      RGB(135,   0, 255,  94),     //purple
      RGB(135,  95,   0,  95),     //orange4
      RGB(135,  95,  95,  96),     //lightPink4
      RGB(135,  95, 135,  97),     //plum4
      RGB(135,  95, 175,  98),     //mediumPurple3
      RGB(135,  95, 215,  99),     //mediumPurple3
      RGB(135,  95, 255,  100),     //slateBlue1
      RGB(135, 135,   0,  101),     //yellow4
      RGB(135, 135,  95,  102),     //wheat4
      RGB(135, 135, 135,  103),     //grey53
      RGB(135, 135, 175,  104),     //lightSlateGrey
      RGB(135, 135, 215,  105),     //mediumPurple
      RGB(135, 135, 255,  106),     //lightSlateBlue
      RGB(135, 175,   0,  107),     //yellow4
      RGB(135, 175,  95,  108),     //darkOliveGreen3
      RGB(135, 175, 135,  109),     //darkSeaGreen
      RGB(135, 175, 175,  110),     //lightBlueSky3
      RGB(135, 175, 215,  111),     //lightBlueSky3
      RGB(135, 175, 255,  112),     //skyBlue2
      RGB(135, 215,   0,  113),     //chartreuse2
      RGB(135, 215,  95,  114),     //darkOliveGreen3
      RGB(135, 215, 135,  115),     //paleGreen3
      RGB(135, 215, 175,  116),     //darkSeaGreen3
      RGB(135, 215, 215,  117),     //darkSlateGray3
      RGB(135, 215, 255,  118),     //skyBlue1
      RGB(135, 255,   0,  119),     //chartreuse1
      RGB(135, 255,  95,  120),     //lightGreen
      RGB(135, 255, 135,  121),     //lightGreen
      RGB(135, 255, 175,  122),     //paleGreen1
      RGB(135, 255, 215,  123),     //aquamarine1
      RGB(135, 255, 255,  124),     //darkSlateGray1
      RGB(175,   0,   0,  125),     //red3
      RGB(175,   0,  95,  126),     //deepPink4
      RGB(175,   0, 135,  127),     //mediumVioletRed
      RGB(175,   0, 175,  128),     //magenta3
      RGB(175,   0, 215,  129),     //darkViolet
      RGB(175,   0, 255,  130),     //purple
      RGB(175,  95,   0,  131),     //darkOrange3
      RGB(175,  95,  95,  132),     //indianRed
      RGB(175,  95, 135,  133),     //hotPink3
      RGB(175,  95, 175,  134),     //mediumOrchid3
      RGB(175,  95, 215,  135),     //mediumOrchid
      RGB(175,  95, 255,  136),     //mediumPurple2
      RGB(175, 135,   0,  137),     //darkGoldenrod
      RGB(175, 135,  95,  138),     //lightSalmon3
      RGB(175, 135, 135,  139),     //rosyBrown
      RGB(175, 135, 175,  140),     //grey63
      RGB(175, 135, 215,  141),     //mediumPurple2
      RGB(175, 135, 255,  142),     //mediumPurple1
      RGB(175, 175,   0,  143),     //gold3
      RGB(175, 175,  95,  144),     //darkKhaki
      RGB(175, 175, 135,  145),     //navajoWhite3
      RGB(175, 175, 175,  146),     //grey69
      RGB(175, 175, 215,  147),     //lightSteelBlue3
      RGB(175, 175, 255,  148),     //lightSteelBlue
      RGB(175, 215,   0,  149),     //yellow3
      RGB(175, 215,  95,  150),     //darkOliveGreen3
      RGB(175, 215, 135,  151),     //darkSeaGreen3
      RGB(175, 215, 175,  152),     //darkSeaGreen2
      RGB(175, 215, 215,  153),     //lightCyan3
      RGB(175, 215, 255,  154),     //lightSkyBlue1
      RGB(175, 255,   0,  155),     //greenYellow
      RGB(175, 255,  95,  156),     //darkOliveGreen2
      RGB(175, 255, 135,  157),     //paleGreen1
      RGB(175, 255, 175,  158),     //darkSeaGreen2
      RGB(175, 255, 215,  159),     //darkSeaGreen1
      RGB(175, 255, 255,  160),     //paleTurquoise
      RGB(215,   0,   0,  161),     //red3
      RGB(215,   0,  95,  162),     //deepPink3
      RGB(215,   0, 135,  163),     //deepPink3
      RGB(215,   0, 175,  164),     //magenta3
      RGB(215,   0, 215,  165),     //magenta3
      RGB(215,   0, 255,  166),     //magenta2
      RGB(215,  95,   0,  167),     //darkOrange3
      RGB(215,  95,  95,  168),     //indianRed
      RGB(215,  95, 135,  169),     //hotPink3
      RGB(215,  95, 175,  170),     //hotPink2
      RGB(215,  95, 215,  171),     //orchid
      RGB(215,  95, 255,  172),     //mediumOrchid1
      RGB(215, 135,   0,  173),     //orange3
      RGB(215, 135,  95,  174),     //lightSalmon3
      RGB(215, 135, 135,  175),     //lightPink3
      RGB(215, 135, 175,  176),     //pink3
      RGB(215, 135, 215,  177),     //plum3
      RGB(215, 135, 255,  178),     //violet
      RGB(215, 175,   0,  179),     //gold3
      RGB(215, 175,  95,  180),     //lightGoldenrod3
      RGB(215, 175, 135,  181),     //tan
      RGB(215, 175, 175,  182),     //mistyRose3
      RGB(215, 175, 215,  183),     //Thistle3
      RGB(215, 175, 255,  184),     //plum2
      RGB(215, 215,   0,  185),     //yellow3
      RGB(215, 215,  95,  186),     //khaki3
      RGB(215, 215, 135,  187),     //lightGoldenrod2
      RGB(215, 215, 175,  188),     //lightYellow3
      RGB(215, 215, 215,  189),     //grey84
      RGB(215, 215, 255,  190),     //lightSteelBlue1
      RGB(215, 255,   0,  191),     //yellow2
      RGB(215, 255,  95,  192),     //darkOliveGreen1
      RGB(215, 255, 135,  193),     //darkOliveGreen1
      RGB(215, 255, 175,  194),     //darkSeaGreen1
      RGB(215, 255, 215,  195),     //honydew2
      RGB(215, 255, 255,  196),     //lightCyan1
      RGB(255,   0,   0,  197),     //red1
      RGB(255,   0,  95,  198),     //deepPink2
      RGB(255,   0, 135,  199),     //deepPink1
      RGB(255,   0, 175,  200),     //deepPink1
      RGB(255,   0, 215,  201),     //magenta2
      RGB(255,   0, 255,  202),     //magenta1
      RGB(255,  95,   0,  203),     //orangeRed1
      RGB(255,  95,  95,  204),     //indianRed1
      RGB(255,  95, 135,  205),     //indianRed1
      RGB(255,  95, 175,  206),     //hotPink
      RGB(255,  95, 215,  207),     //hotPink
      RGB(255,  95, 255,  208),     //mediumOrchid1
      RGB(255, 135,   0,  209),     //darkOrange
      RGB(255, 135,  95,  210),     //salmon1
      RGB(255, 135, 135,  211),     //lightCoral
      RGB(255, 135, 175,  212),     //paleVioletRed1
      RGB(255, 135, 215,  213),     //orchid2
      RGB(255, 135, 255,  214),     //orchid1
      RGB(255, 175,   0,  215),     //orange1
      RGB(255, 175,  95,  216),     //sandyBrown
      RGB(255, 175, 135,  217),     //lightSalmon1
      RGB(255, 175, 175,  218),     //lightPink1
      RGB(255, 175, 215,  219),     //pink1
      RGB(255, 175, 255,  220),     //plum1
      RGB(255, 215,   0,  221),     //gold1
      RGB(255, 215,  95,  222),     //lightGoldenrod2
      RGB(255, 215, 135,  223),     //lightGoldenrod2
      RGB(255, 215, 175,  224),     //navajoWhite1
      RGB(255, 215, 215,  225),     //mistyRose1
      RGB(255, 215, 255,  226),     //thistle1
      RGB(255, 255,   0,  227),     //yellow1
      RGB(255, 255,  95,  228),     //lightGoldenrod1
      RGB(255, 255, 135,  229),     //khaki1
      RGB(255, 255, 175,  230),     //wheat1
      RGB(255, 255, 215,  231),     //cornsilk1
      RGB(255, 255, 255,  232),     //grey100
      RGB(8  ,   8,   8,  233),     //grey3
      RGB(18 ,  18,  18,  234),     //grey7
      RGB(28 ,  28,  28,  235),     //grey11
      RGB(38 ,  38,  38,  236),     //grey15
      RGB(48 ,  48,  48,  237),     //grey19
      RGB(58 ,  58,  58,  238),     //grey23
      RGB(68 ,  68,  68,  239),     //grey27
      RGB(78 ,  78,  78,  240),     //grey30
      RGB(88 ,  88,  88,  241),     //grey35
      RGB(98 ,  98,  98,  242),     //grey39
      RGB(108, 108, 108,  243),     //grey42
      RGB(118, 118, 118,  244),     //grey46
      RGB(128, 128, 128,  245),     //grey50
      RGB(138, 138, 138,  246),     //grey54
      RGB(148, 148, 148,  247),     //grey58
      RGB(158, 158, 158,  248),     //grey62
      RGB(168, 168, 168,  249),     //grey66
      RGB(178, 178, 178,  250),     //grey70
      RGB(188, 188, 188,  251),     //grey74
      RGB(198, 198, 198,  252),     //grey78
      RGB(208, 208, 208,  253),     //grey82
      RGB(218, 218, 218,  254),     //grey85
      RGB(228, 228, 228,  255),     //grey89
      RGB(238, 238, 238,  256)     //grey93
    )

    def rgbToXterm(r: Int, g: Int, b: Int): Int = {
      val compRgb = RGB(r,g,b)
      COLOR256.minBy(compRgb.dist).index-1
    }
  }

  object Emoji {
    import Xterm._

    case class EmojiRGB (emoji: String, rgb: RGB)

    //TODO make more colors
    //TODO make emoji conversion automatic
    val emojiRGBs : Seq[EmojiRGB] = Seq(
      EmojiRGB("⬛️", RGB(   0,   0,   0,    1)), //black
      EmojiRGB("🌑", RGB(   0,   0,  85,    2)), //darkBlue
      EmojiRGB("💙", RGB(   0,   0, 255,    6)), //blue
      EmojiRGB("🥦‍", RGB(   0,  85,   0,    7)), //darkGreen
      EmojiRGB("🧟‍", RGB(   0,  85,  85,    8)), //turquoise
      EmojiRGB("🌀", RGB(   0,  85, 255,    9)), //oceanBlue
      EmojiRGB("📗", RGB(   0, 255,   0,    7)), //green
      EmojiRGB("🍏‍", RGB(   0, 255,  85,    8)), //toxicGreen
      EmojiRGB("🦋", RGB(   0, 255, 255,    9)), //cyan
      EmojiRGB("👞️", RGB(   85,   0,   0,  10)), //darkRed
      EmojiRGB("🍆", RGB(  85,   0,  85,   11)), //bordeauxPurple
      EmojiRGB("👖", RGB(  85,   0, 255,   12)), //90sBlue
      EmojiRGB("🐴‍", RGB(  85,  85,   0,   13)), //greenPoop🐐
      EmojiRGB("🐨", RGB(  85,  85,  85,   14)), //darkGray
      EmojiRGB("🧢", RGB(  85,  85, 255,   15)), //marineBlue
      EmojiRGB("🍀", RGB(  85, 255,   0,   16)), //brightGreen
      EmojiRGB("🔫", RGB(  85, 255,  85,   17)), //frogGreen
      EmojiRGB("🐬", RGB(  85, 255, 255,   18)), //brightCyan
      EmojiRGB("🅰️️", RGB( 255,   0,   0,   19)), //red
      EmojiRGB("🚨", RGB( 255,   0,  85,   20)), //lipstickRed
      EmojiRGB("🌺", RGB( 255,   0, 255,   21)), //magenta
      EmojiRGB("🍊‍", RGB( 255,  85,   0,   22)), //orange
      EmojiRGB("🦐", RGB( 255,  85,  85,   23)), //fadedRed
      EmojiRGB("👛", RGB( 255,  85, 255,   24)), //pink
      EmojiRGB("🌕", RGB( 255, 255,   0,   25)), //yellow
      EmojiRGB("📒", RGB( 255, 255,  85,   26)), //brightYellow
      EmojiRGB("☁️", RGB( 255, 255, 255,   27)) //white
      )

    def rgbToEmoji(r: Int, g: Int, b: Int): Int = {
      val compRgb = RGB(r,g,b)
      emojiRGBs.minBy(x => compRgb.dist(x.rgb)).rgb.index-1
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
    userCheck(Try(StdIn.readLine("Type a width (0-255):").trim.toInt).toOption)
  }

  def printImg(path     : String,
               width    : Int,
               useChar  : Boolean = false,
               char     : String  = "\u2588",
               use256   : Boolean = false,
               useEmoji : Boolean = false
              ): Unit = {
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
      imgToAscii(myImage.get, useChar, char, use256, useEmoji)
    } else {
      println("Couldn't load image... Sorry!")
    }
  }

  def checkFor8bit(): Boolean = {                                    //display a gradient to give a sense of color
    for(i<-0 to 75) {
      val shade = math.min(i + 60, 255)
      print"${COLOR_B(shade,shade,shade)} "
    }
    StdIn.readLine("Do you see a color gradient? y/n").toLowerCase.trim match {
      case "y" | "yes"  => println(s"Good!");                     false
      case "n" | "no"   => println("Switching to 8 bit mode.");   true
      case _            => println("Not a valid input");          checkFor8bit()
    }
  }

  /** Convert an Image to Ascii art in the console */
  def imgToAscii(image    : Image,
                 useChar  : Boolean = false,
                 char     : String  = "\u2588",
                 use256   : Boolean = false,
                 useEmoji : Boolean = false
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
        if(useEmoji)
          print"${Emoji.emojiRGBs(Emoji.rgbToEmoji(r,g,b)).emoji}"
        else if(use256) {
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
    //printImg(imgPath, myWidth, use256 = _8bit)
    printImg(imgPath, myWidth, useEmoji = true)
  }

}