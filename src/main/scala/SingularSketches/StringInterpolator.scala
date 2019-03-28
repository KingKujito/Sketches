package SingularSketches

object StringInterpolator {

  implicit class MyInter(val sc: StringContext) extends AnyVal {
    def intifier(args: Any*): Int = {
      val strings = sc.parts.iterator
      val expressions = args.iterator
      val buf = new StringBuffer(strings.next)
      while(strings.hasNext) {
        buf append expressions.next
        buf append strings.next
      }
      println(buf)
      buf.length
    }
  }

  def isThisLong(x: Int): Unit = println(s"is $x long")

  val name = "CAMERON"

  def main(args: Array[String]): Unit = {
    isThisLong(intifier"1234")
    isThisLong(intifier"12345678")
    isThisLong(intifier"{name: $name}")
  }
}
