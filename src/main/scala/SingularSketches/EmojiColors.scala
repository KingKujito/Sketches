package SingularSketches

import java.awt.{Color, Image}
import java.awt.image.BufferedImage
import java.io.{File, FileWriter}
import java.net.URL
import java.nio.ByteBuffer
import java.nio.charset.Charset

import javax.imageio.ImageIO
import org.openqa.selenium.{By, WebDriver, WebElement}
import org.openqa.selenium.safari.SafariDriver
import utils.ImgToAscii

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.Try

//TODO use shortcodes: https://github.com/lightbend/lightbend-emoji
object EmojiColors {
  val driver     : WebDriver                = new SafariDriver()
  val fileExt                               = ".csv"
  //The url hosting all emoji names, unicodes and images.
  val allEmojiPath                          = "https://unicode.org/emoji/charts/full-emoji-list.html"
  //The url hosting a UTF conversion tool.
  val converterPath                         = "https://www.branah.com/unicode-converter"
  //Should be 1
  val imgSize    : Int                      = 1
  //Path to this project
  val path       : String                   = new java.io.File(".").getCanonicalPath
  //resources path
  val resources  : String                   = s"$path/src/main/resources"
  //Path to emoji_data
  val emojiData                             = new File(resources+"/emoji_data")
  //mapped names to EmojiTypes
  val emojiTypes : Map[EmojiType, String]   = Map(Apple -> "Apple")
  //there are eleven emoji listings (0 to 10). This is the order the appear on allEmojiPath.
  val emojiOrder : Map[EmojiType, Int] = Map(
    Apple -> 0
  )
  //The files where we want to store emoji info
  lazy val emojiFiles : Map[EmojiType, File] = Map(Apple -> new File(emojiData+"/"+emojiTypes(Apple)+fileExt))

  /**
    * @param name       Emoji name for human readability.
    * @param unicode    Unicode for the emoji
    * @param color      General RGB of the emoji
    */
  case class EmojiColor(name: String, unicode: String, color: Color) {
    require(!name.contains("\""))
    def toCSV : String = s"""$name,$unicode,${color.getRed},${color.getGreen},${color.getBlue}"""
  }

  object EmojiColor {
    def fromCSV(csv: String): EmojiColor = {
      val vals = csv.split(",")
      EmojiColor(vals.head, vals(1), new Color(vals(2).toInt, vals(3).toInt, vals(4).toInt))
    }
  }

  /**
    * There are different types of emoji, which will mean different general colors. Use this type to differ.
    */
  sealed trait   EmojiType
  case   object  Apple       extends EmojiType
  //case   object  Google      extends EmojiType
  //case   object  Android     extends EmojiType

  /**
    * Make emojiData folder if it doesn't exist
    */
  def generateDir(): Unit = {
    if(!emojiData.exists || !emojiData.isDirectory) {
      println("creating dir...")
      emojiData.mkdir()
    } else println("found dir!")
  }

  /**
    * Create a file if it doesn't exist
    * @param ext file extension
    */
  def generateFiles(ext: String = fileExt): Unit = {
    emojiTypes.foreach { name =>
      val file = new File(emojiData.getPath+"/"+name._2+ext)
      if(!file.exists || !file.isFile) {
        println("creating file...")
        file.createNewFile()
      } else println("found file!")
    }
  }

  /**
    * Use this if you want to test the accuracy of readGeneralColor().
    */
  def getGeneralColorBigImg(img : BufferedImage): Color = {
    ImgToAscii.imgToAscii(img, printT = ImgToAscii.PlainT)

    var r,g,b = 0
    val xy = for {
      x <- 0 until img.getWidth
      y <- 0 until img.getHeight
    } yield (x, y)
    xy.foreach { xy =>
      val col : Color = new Color(img.getRGB(xy._1, xy._2))
      r += col.getRed
      g += col.getGreen
      b += col.getBlue
    }
    new Color(r/xy.length, g/xy.length, b/xy.length)
  }

  /**
    * @param path  Either a url or local path to the image.
    * @return      If successful: the general color of the image (which color is the image if you squint your eyes?).
    */
  def readGeneralColor(path: String):Option[Color] = {
    Try {
      val img : Image = try ImageIO.read(new URL(path)) catch { case _: Exception => ImageIO.read(new File(path)) }
      val scaled = img.getScaledInstance(imgSize, imgSize, Image.SCALE_AREA_AVERAGING)

      val i   = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_RGB)
      i.getGraphics.drawImage(scaled, 0, 0 , null)

      new Color(i.getRGB(0,0))
    }.toOption
  }

  /**
    * Write CSV representation of emoji to a file.
    */
  def addEmojiColorToFile(emojiColor: EmojiColor, emojiType: EmojiType, ext: String = fileExt): Unit = {
    if(emojiFiles(emojiType).exists && emojiFiles(emojiType).isFile) {
      print(".")
      val lines = Source.fromFile(emojiFiles(emojiType)).getLines.mkString("\n")
      val fw = new FileWriter(emojiFiles(emojiType).getPath)
      fw.write(lines+"\n"+emojiColor.toCSV)
      fw.close()
    } else println("could not find "+ emojiData+"/"+emojiTypes(emojiType)+ext)
  }

  /**
    * Read all EmojiColors from a CSV file
    */
  def readFromFile(path: String) : List[EmojiColor] = {
    var i = 0
    val buff = ListBuffer.empty[Option[EmojiColor]]
    for (line <- Source.fromFile(path).getLines) {
      if(i!=0)
        buff += Try( EmojiColor.fromCSV(line) ).toOption
      i += 1
    }
    buff.flatten.toList
  }

  /**
    * Write all emoji to the information file.
    */
  def writeAllEmoji(emojiType: EmojiType) : Unit = {
    println("Writing emoji data.\n")
    val emoji = getAllEmoji(emojiType)

    println("\n\nConverting characters...")
    driver.get(converterPath)
    emoji
      .map{e =>
        println(e.unicode)
        clearText()

        val newUnicode = e.unicode.split(" ").map{
          codeToChar
        }.mkString

        println(newUnicode)

        EmojiColor(
          e.name,
          newUnicode,
          e.color)
      }
      .foreach { e =>
        print(":")
        addEmojiColorToFile(e, emojiType)
    }
    println("\nProcess complete...")
  }

  def main(args: Array[String]): Unit = {
    generateDir()
    generateFiles()
    writeAllEmoji(Apple)

    //println(UTF32ToEmoji(completeUTF32(codeToChar("U+1F600"))))
    //println(codeToChar("U+1F92A"))
    //println(codeToChar("U+2639"))
    driver.quit()
  }

  /**
    * Return an EmojiColor based on scraped data. Unicode has not been converted yet.
    */
  def getAllEmoji(emojiType: EmojiType) : List[EmojiColor] = {
    driver.get(allEmojiPath)

    val emojiEntries = driver.findElements(By.xpath("/html/body/div[3]/table/tbody/tr"))
    emojiEntries.toArray.toList.flatMap{
      case we: WebElement =>
        Try {
          val name =
            we
              .findElement(By.className("name"))
              .getAttribute("innerHTML")
              .replace(",", " ")
              //.replace(":", "/")

          val unicode =
            we
              .findElement(By.className("code"))
              .findElement(By.tagName("A"))
              .getAttribute("innerHTML")
              //.split(" ")
              //.map(codeToChar)
              //.mkString
              //.trim

          val image =
            Try(
              we
                .findElement(By.className("andr"))
                .findElement(By.className("imga"))
                .getAttribute("src")).toOption

          if(name.head == '⊛') {
            print("⊛")
            throw new Exception("Not an actual emoji")
          }

          EmojiColor(name, unicode,
            readGeneralColor(base64ToImage(image.get, emojiData.getPath+"/"+emojiTypes(emojiType)+"/"+name)).get)
        }.toOption
    }//.foreach(x => println(x.getAttribute("innerHTML")))
  }

  /**
    * Transform format U+x into an actual character
    */
  def codeToChar(code : String): String = {
    val hex = code.substring(2)
    if(hex.length <= 4)
      Integer.parseInt(hex, 16).toChar.toString
    else {
      UTF32ToEmoji(completeUTF32(hex))
    }
  }

  def completeUTF32 (hex: String) : String = {
    require(hex.length <= 8)
    val amountOf0 = 8 - hex.length
    val zeros = for (_ <- 0 until amountOf0) yield "0"
    zeros.mkString + hex
  }

  def convertToBinary(input: String, encoding: String): String = {
    val encoded_input = Charset.forName(encoding).encode(input).array
    encoded_input
      .map{e =>
        println(encoding +"  "+e)
        String
          .format(
            "%1$" + java.lang.Byte.SIZE + "s",
            Integer.toBinaryString(e ^ 255))
          .replace(" ", "0")}
      .mkString(" ")
  }

  def decoder(bytes: List[Byte]): String = {
    val bb = ByteBuffer.wrap(bytes.toArray)
    Charset.forName("UTF-16").decode(bb).array.mkString
      .replace("\u0000", "").replace("�", "")
  }

  def convertToByteList(string : String): List[Byte] = {
    Charset.forName("UTF-16").encode(string).array.toList
  }

  def UTF16 (string: String): List[Byte] = {
    val beginning  = List[Byte](-2, -1)
    val middle     = Charset.forName("UTF-8").encode(string).array.toList.flatMap(b => List(0.toByte, b))
    //val end        = (for (_ <- 0 until (string.length * 2 -1)) yield 0.toByte).toList
    beginning ::: middle
  }

  /**
    * Use site to translate UTF32 to UTF16.
    */
  def UTF32ToEmoji (string: String) : String = {
    val utf32 = driver.findElement(By.id("utf32"))
    utf32.sendKeys(string)

    //val emoji = driver.findElement(By.id("text")).getAttribute("value")
    val emoji = driver
      .findElement(By.id("unicode"))
      .getAttribute("value")
        .split("\\\\u")
        .flatMap( c =>
          Try(Integer.parseInt(c, 16).toChar.toString).toOption)
        .mkString

    emoji
  }

  def clearText() : Unit = {
    val utf32 = driver.findElement(By.id("utf32"))
    utf32.clear()
  }
  /**
    * Create an image from base 64
    * @param sourceData   data URL to png image
    * @param name         full path of the image we're writing
    * @return             full path
    */
  def base64ToImage(sourceData: String, name: String) : String = {
    // Needed Imports
    import java.io.ByteArrayInputStream
    import sun.misc.BASE64Decoder

    // tokenize the data
    def parts = sourceData.split(",")
    def imageString = parts(1)

    val decoder = new BASE64Decoder()
    val imageByte                = decoder.decodeBuffer(imageString)
    val bis                      = new ByteArrayInputStream(imageByte)
    val image                    = ImageIO.read(bis)
    bis.close()

    // write the image to a file
    val outputfile = new File(name+".png")
    print(
      if(outputfile.exists) "."
      else ImageIO.write(image, "png", outputfile)
    )
    outputfile.getPath
  }
}
