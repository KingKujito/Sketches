package controllers

trait MyApp extends App {
  override def main(args: Array[String]): Unit = {
    onStart()
    run(true)
  }

  def onStart(): Unit =
    println("app started")

  def onEnd():   Unit =
    println("app ended")

  def loop():    Unit

  def run(x: Boolean):  Unit =
    if(x) tryLoop{()=>loop()} else onEnd()

  def tryLoop (x : () => Unit): Unit = {
    try {
      x()
      run(true)
    } catch {
      case _ : Exception => run(false)
    }
  }

  def quit: Exception = throw new Exception("Ended the loop")

}
