package controllers

import mapScroller.MapApp
import mover.MoverApp

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
      case _ => quit
    }
  }

  def selectApp: String = {
    StdIn.readLine(
      """
        |Select your app:
        |-'mapscroller'
        |-'mover'
      """.stripMargin) match {
      case x : String if x.toLowerCase == "mapscroller" => x.toLowerCase
      case x : String if x.toLowerCase == "mover"       => x.toLowerCase
      case x => println(s"Could not load app '$x'"); selectApp
    }
  }

}
