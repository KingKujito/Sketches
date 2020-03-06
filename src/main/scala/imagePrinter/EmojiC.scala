package imagePrinter

import Xterm._


object EmojiC {

  lazy val emoji: Seq[EmojiColors.EmojiColor] = EmojiColors.readFromFile(EmojiColors.emojiFiles(EmojiColors.Apple).getPath)

  def rgbToEmoji(r: Int, g: Int, b: Int): String = {
    val c = emoji.minBy(x => RGB(r,g,b).dist(RGB(x.color.getRed, x.color.getGreen, x.color.getBlue)))
    c.unicode
  }
}
