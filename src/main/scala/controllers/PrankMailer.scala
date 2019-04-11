package controllers

import utils.Mail

import scala.concurrent.duration._
import scala.io.StdIn
import utils.ImgToAscii._

object PrankMailer {
  def loop (): Unit = {
    Mail.mail(
      "It has started.",
      "It has started, my fellow.",
      Some(
        "Please view the entire message in this mail.<br>"+
        imgToHtml("/Users/lunatech/Desktop/desktop/davedes2_1.jpg")))

    println(s"${Console.RED}Terminate this process manually in your terminal.${Console.RESET}")
    prank(
      StdIn.readLine("Your subject:"),
      StdIn.readLine("Your content:"),
      StdIn.readLine("Your rich message:")
    )
  }

  def prank (sub: String, cont: String, mess: String): Unit = {
    try {
      Mail.mail(sub, cont, Some(mess))
      Thread.sleep(Duration(1, HOURS).toMillis)
      prank(sub, cont, mess)
    }
  }
}
