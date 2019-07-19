package SingularSketches

object BoredAsFuck {
  val fslash3 = "/\\/ /\\/"
  val bslash3 = "\\ \\/\\ \\"
  val fslash  = "/ /\\/ /"
  val bslash  = "\\/\\ \\/\\"

  def main(args: Array[String]): Unit = {
    loop(0)
  }
  def loop(i : Int): Unit = {
    if(i%3 == 0){
      if(i%2 == 0) println(fslash3 + fslash + bslash3 + bslash) else println(bslash3 + bslash + fslash3 + fslash)
    } else {
      if(i%2 == 0) println(fslash + fslash3 + bslash + bslash3) else println(bslash + bslash3 + fslash + fslash3)
    }
    Thread.sleep(550)
    if (i < 50)
      loop(i+1)
  }
}
