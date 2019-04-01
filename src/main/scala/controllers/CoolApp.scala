package controllers

import mapScroller.MapApp
import mover.MoverApp
import utils.ImgToAscii
import utils.Mail

import scala.io.StdIn

object CoolApp extends MyApp {

  var app = ""

  override def onStart(): Unit = {
    super.onStart()
    app = selectApp
  }

  override def loop(): Unit = {
    app match {
      case "mapscroller" =>
        MapApp.customLoop(List(
          "###########",
          "#         #",
          "#         #",
          "#    1    #",
          "#         #",
          "#         #",
          "##########"
        ))
      case "mover"      =>
        MoverApp.customLoop(0)
      case "imgtoascii" =>
        ImgToAscii.main(Array.empty)
      case "prankmailer" =>
        Mail.mail("Funny mail", "asdghfjkl")
        quit
      case _ => quit
    }
  }

  def selectApp: String = {
    StdIn.readLine(
      """
        |Select your app:
        |-'mapscroller'
        |-'mover'
        |-'ImgToAscii'
        |-'PrankMailer'
      """.stripMargin) match {
      case x : String if x.toLowerCase == "mapscroller" ||
                         x.toLowerCase == "mover"       ||
                         x.toLowerCase == "imgtoascii"  ||
                         x.toLowerCase == "prankmailer" => x.toLowerCase
      case x => println(s"Could not load app '$x'"); selectApp
    }
  }

}
