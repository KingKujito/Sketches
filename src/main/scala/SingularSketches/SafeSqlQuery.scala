package SingularSketches

/**A String interpolator which allows for safe SQL queries.
  *
  * This is merely a proof of concept. If you'd like a prepared query and a parameter list, use scalikejdbc's interpolator:
  *       sqls"SELECT FROM users WHERE name=$name AND age=$age"
  *
  * or any other proven to be safe options available.
  *
  * Note that this code has been made to work with OrientDB and that it might need some changes to work with Postgresql.
 */
object SafeSqlQuery {

  sealed trait JsonConvertable {        //an example of how Json conversion could be handled.
    def toJsonString : String          // Change this to whatever you're using.
  }

  def exceptionHandler (any: Any) : String = "TODO: Handle exceptions in a desired fashion"

  implicit class SafeSql(val sc: StringContext) extends AnyVal {

    def quotate (string: String) = s"'$string'"

    def appender(string: String, string2: String)
                (implicit stringBuffer: StringBuffer) : StringBuffer = {
      stringBuffer append string
      stringBuffer append string2
    }

    /**Sanitize dangerous inputs like strings and automatically convert Json convertibles.
      */
    def sSql(args: Any*): String = {
      val strings = sc.parts.iterator
      val expressions = args.iterator
      implicit val buf: StringBuffer = new StringBuffer(strings.next)
      while (strings.hasNext) {
        expressions.next match {
          case s: String =>
            appender(InjectionProtection.sanitize(s), strings.next)

          case jc: JsonConvertable =>
            appender(jc.toJsonString, strings.next)

          case n: Number =>
            appender(n.toString, strings.next)

          case x =>
            appender(exceptionHandler(x), strings.next)
        }
      }
      buf.toString
    }

    /**Sanitize dangerous inputs like strings and automatically convert Json convertibles. Also put quotes around string values.
      */
    def autoSql(args: Any*): String = {
      val strings = sc.parts.iterator
      val expressions = args.iterator
      implicit val buf: StringBuffer = new StringBuffer(strings.next)
      while (strings.hasNext) {
        expressions.next match {
          case s: String =>
            appender(quotate(InjectionProtection.sanitize(s)), strings.next)

          case jc: JsonConvertable =>
            appender(quotate(jc.toJsonString), strings.next)

          case n: Number =>
            appender(n.toString, strings.next)

          case x =>
            appender(exceptionHandler(x), strings.next)
        }
      }
      buf.toString
    }
  }

  val name   = "Brother John"         //A valid name
  val age    = 120                    //A valid age
  val attack = "Bro' OR 'x'='x';/*"   //The thing we're fighting

  def main(args: Array[String]): Unit = {
    println(s"name: $name\nage: $age\nattack: $attack\n\n\n")
    println("Plain old string version:")
    println("\n\nWith name:\n")
    println(s"SELECT FROM users WHERE name=$name AND age=$age;")
    println(s"SELECT FROM users WHERE name='$name' AND age=$age;")
    println("\n\nWith attack:\n")
    println(s"SELECT FROM users WHERE name=$attack AND age=$age;")
    println(s"SELECT FROM users WHERE name='$attack' AND age=$age;")

    println("\n\n\n2x sSql, 2x autoSql version:")
    println("\n\nWith name:\n")
    println(sSql"SELECT FROM users WHERE name=$name AND age=$age;")
    println(sSql"SELECT FROM users WHERE name='$name' AND age=$age;")
    println(autoSql"SELECT FROM users WHERE name=$name AND age=$age;")
    println(autoSql"SELECT FROM users WHERE name='$name' AND age=$age;")
    println("\n\nWith attack:\n")
    println(sSql"SELECT FROM users WHERE name=$attack AND age=$age;")         //sSql without    dev quotes = invalid
    println(sSql"SELECT FROM users WHERE name='$attack' AND age=$age;")       //sSql with       dev quotes = valid & safe
    println(autoSql"SELECT FROM users WHERE name=$attack AND age=$age;")      //autoSql without dev quotes = valid & safe
    println(autoSql"SELECT FROM users WHERE name='$attack' AND age=$age;")    //autoSql with    dev quotes = invalid
  }

  //construct scala-oriented SQLStatement like this:
  //                          SQLStatement(sSql"my statement")
  //or
  //                          SQLStatement(autoSql"my statement")
  //see: http://itsmeijers.com/docs/scala-oriented/#oriented.syntax.SQLStatement


/*RESULTS FROM RUNNING main() with notes


name: Brother John
age: 120
attack: Bro' OR 'x'='x';/*



Plain old string version:


With name:

SELECT FROM users WHERE name=Brother John AND age=120;            //first results are queries where devs haven't provided quotes themselves
SELECT FROM users WHERE name='Brother John' AND age=120;          //second results are queries where devs have provided quotes themselves


With attack:

SELECT FROM users WHERE name=Bro' OR 'x'='x';/* AND age=120;
SELECT FROM users WHERE name='Bro' OR 'x'='x';/*' AND age=120;



2x sSql, 2x autoSql version:


With name:

SELECT FROM users WHERE name=Brother John AND age=120;          //sSql without    dev quotes = invalid
SELECT FROM users WHERE name='Brother John' AND age=120;        //sSql with       dev quotes = valid
SELECT FROM users WHERE name='Brother John' AND age=120;        //autoSql without dev quotes = valid
SELECT FROM users WHERE name=''Brother John'' AND age=120;      //autoSql with    dev quotes = invalid


With attack:

SELECT FROM users WHERE name=Bro\' OR \'x\'=\'x\';/* AND age=120;        //sSql without    dev quotes = invalid
SELECT FROM users WHERE name='Bro\' OR \'x\'=\'x\';/*' AND age=120;      //sSql with       dev quotes = valid & safe
SELECT FROM users WHERE name='Bro\' OR \'x\'=\'x\';/*' AND age=120;      //autoSql without dev quotes = valid & safe
SELECT FROM users WHERE name=''Bro\' OR \'x\'=\'x\';/*'' AND age=120;    //autoSql with    dev quotes = invalid

^The results suggest that autoSql is a valid and safe option when checking values in a query where the dev chose to omit quotes.
 sSql is valid and safe for queries where quotes imply that a string should be used.

*/*/*/*/*/*/*/*/

}
