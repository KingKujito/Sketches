package mover

import java.awt.event.{KeyEvent, KeyListener}
import controllers.CoolApp

object MoverApp extends KeyListener {

  var jump  = false
  var right = false
  var left  = false

  def customLoop(time: Int): Unit = {
    if(time == 0) {
      val ground = 7
      println(s"\n\njump=$jump right=$right left=$left\n")
      updatePhysics(ground)
      renderScene(ground)
    }
    customLoop(time+1)
  }

  def renderScene(ground: Int) = {
    for(y<-0 to 6) {
      print("\n")
      for (x <- 0 to ground)
        print(if (y == Player.yPos && x == Player.xPos) "#" else "-")
    }
  }

  def updatePhysics(ground: Int): Unit = {
    val oldY = Player.yPos

    if(right) Player.xPos += 1 else if(left) Player.xPos -= 1
    if(Player.yPos == 0 && jump && !Player.jumping) Player.jumping = true

    if(Player.jumping && Player.yPos < 5) Player.yPos += 1
    else if(!Player.jumping && Player.yPos > 0) Player.yPos -= 1

    if(Player.xPos < 0) Player.xPos = 0
    else if(Player.xPos > ground) Player.xPos = ground

    if(oldY == 1 && Player.yPos == 0) Player.jumping = false
  }

  def keyTyped(e: KeyEvent): Unit = ()

  def keyPressed(e: KeyEvent): Unit = {
    if(e.getKeyChar == 'w') jump = true

    if(e.getKeyChar == 'a') left  = true else
    if(e.getKeyChar == 'd') right = true
    if(e.getKeyChar == 's') CoolApp.quit
  }

  /** Handle the key-released event from the text field. */
  def keyReleased(e : KeyEvent): Unit = {
    if(e.getKeyChar == 'w') jump = false

    if(e.getKeyChar == 'a') left  = false else
    if(e.getKeyChar == 'd') right = false
  }

  object Player{
    var xPos : Int = 0
    var yPos : Int = 0
    var jumping : Boolean = false
  }
}
