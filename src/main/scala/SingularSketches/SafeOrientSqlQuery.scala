package SingularSketches

//import oriented._
import oriented.syntax._
import utils.InjectionProtection

/**A String interpolator which allows for safe SQL queries.
  *
  * This is merely a proof of concept. If you'd like a prepared query and a parameter list, use scalikejdbc's interpolator:
  *       sqls"SELECT FROM users WHERE name=$name AND age=$age"
  *
  * or any other proven to be safe options available.
  *
  * Note that this code has been made to work with OrientDB and that it might need some changes to work with Postgresql.
 */
object SafeOrientSqlQuery {

  implicit class SafeSql(val sc: StringContext) extends AnyVal {

    def appender(string: String, string2: String)
                (implicit stringBuffer: StringBuffer) : StringBuffer = {
      stringBuffer append string
      stringBuffer append string2
    }

    def sSql(args: Any*): SQLStatement = {
      val strings = sc.parts.iterator
      val expressions = args.iterator
      implicit val buf: StringBuffer = new StringBuffer(strings.next)
      while (strings.hasNext) {
        appender(
          InjectionProtection.sanitize(expressions.next.toString),      //Sanitize dangerous inputs like strings and automatically convert Json convertibles.
          strings.next)
      }
      SQLStatement(buf.toString)                                        //Easily modifiable for extensions such as SQLStatementT
    }
    //now replace all your sql"query" with sSql"query"
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

    println("\n\n\nsSql version:")
    println("\n\nWith name:\n")
    println(sSql"SELECT FROM users WHERE name=$name AND age=$age;")
    println(sSql"SELECT FROM users WHERE name='$name' AND age=$age;")
    println("\n\nWith attack:\n")
    println(sSql"SELECT FROM users WHERE name=$attack AND age=$age;")         //sSql without    dev quotes = invalid
    println(sSql"SELECT FROM users WHERE name='$attack' AND age=$age;")       //sSql with       dev quotes = valid & safe
  }

  //construct scala-oriented SQLStatement like this:
  //                          SQLStatement(sSql"my statement")


/*RESULTS FROM RUNNING main() with notes


name: Brother John
age: 120
attack: Bro' OR 'x'='x';/*



Plain old string version:


With name:

SELECT FROM users WHERE name=Brother John AND age=120;
SELECT FROM users WHERE name='Brother John' AND age=120;


With attack:

SELECT FROM users WHERE name=Bro' OR 'x'='x';/* AND age=120;
SELECT FROM users WHERE name='Bro' OR 'x'='x';/*' AND age=120;



sSql version:


With name:

SQLStatement(SELECT FROM users WHERE name=Brother John AND age=120;)                //sSql without    dev quotes = invalid
SQLStatement(SELECT FROM users WHERE name='Brother John' AND age=120;)              //sSql with       dev quotes = valid & safe


With attack:

SQLStatement(SELECT FROM users WHERE name=Bro\' OR \'x\'=\'x\';/* AND age=120;)     //sSql without    dev quotes = invalid
SQLStatement(SELECT FROM users WHERE name='Bro\' OR \'x\'=\'x\';/*' AND age=120;)   //sSql with       dev quotes = valid & safe

^If a new unsafe character is discovered, it could easily be secured by editing the interpolator.

*/*/*/*/*/*/

}
