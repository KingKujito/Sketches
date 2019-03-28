package SingularSketches

import scalikejdbc._

object ScalikeSafeSqlQuery {

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

    println("\n\n\nsql version:")
    println("\n\nWith name:\n")
    println(sqls"SELECT FROM users WHERE name=$name AND age=$age;".toString)
    println(sqls"SELECT FROM users WHERE name='$name' AND age=$age;".toString)
    println("\n\nWith attack:\n")
    println(sqls"SELECT FROM users WHERE name=$attack AND age=$age;".toString)
    println(sqls"SELECT FROM users WHERE name='$attack' AND age=$age;".toString)
  }
}
