import scala.Console.RESET

package object imagePrinter {

  sealed trait PrintType
  final case object EmojiCT extends PrintType
  final case object EmojiT  extends PrintType
  final case object HtmlT   extends PrintType
  final case object PlainT  extends PrintType

  /**String interpolators for quick and easy printing with clean styles
    */
  implicit class MyInter(val sc: StringContext) extends AnyVal {
    def print(args: Any*): Unit = {
      val strings = sc.parts.iterator
      val expressions = args.iterator
      val buf = new StringBuffer(strings.next)
      while(strings.hasNext) {
        buf append expressions.next
        buf append strings.next
      }
      Console.print(s"$RESET${buf.toString}$RESET")
    }
    def println(args: Any*): Unit = {
      val strings = sc.parts.iterator
      val expressions = args.iterator
      val buf = new StringBuffer(strings.next)
      while(strings.hasNext) {
        buf append expressions.next
        buf append strings.next
      }
      Console.println(s"$RESET${buf.toString}$RESET")
    }
  }
}
