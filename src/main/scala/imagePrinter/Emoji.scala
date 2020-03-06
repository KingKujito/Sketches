package imagePrinter

import Xterm._

object Emoji {

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
