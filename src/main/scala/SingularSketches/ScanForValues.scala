/*
Try my regex
.*(s|sql|sqlTask)"+.*=[\t\n\r ]*\$.*"+.*
or
.*(s|sql|sqlTask)"+.*=[\t\n\r ]*\$\{.*QVal[\t\n\r ]*\(.*\).*\}.*"+.*
pattern on https://regexr.com/
Using this dataset

sql"SELECT FROM ${vertex.name} WHERE ${field.name} = $Value AND ${field2.name} = $Value2"
s"""SELECT FROM vertex WHERE ${field.name} = $Value"""
sqlTask"SELECT FROM vertex WHERE field = $Value"
SELECT FROM $vertex WHERE $field = $Value
s"SELECT FROM vertex WHERE $field = $Value"
sql"SELECT FROM ${vertex.name} WHERE field  = $Value"
sql"SELECT FROM ${vertex.name} WHERE ${field.name} = Value"
s"SELECT FROM ${vertex.name} WHERE ${field.name} = ${Value.something}"
sql"SELECT FROM vertex WHERE field = Value"
sql"SELECT FROM vertex WHERE field =  $Value"
sqlTask"""SELECT FROM ${vertex.name} WHERE ${field.name} = ${f(Value)}"""

sql"SELECT FROM ${vertex.name} WHERE ${field.name} = ${QVal(Value)}"
sqlTask"SELECT FROM vertex WHERE ${field.name} = ${QVal(Value)}"
s"SELECT FROM vertex WHERE field = ${QVal(Value)}"
"SELECT FROM $vertex WHERE $field = ${QVal(Value)}"
sql"SELECT FROM vertex WHERE $field = ${QVal(Value)}"
s"SELECT FROM ${vertex.name} WHERE field = ${QVal(Value)}"
sql"SELECT FROM ${vertex.name} WHERE ${field.name} = ${QVal(Value.something)}"
sql"SELECT FROM ${vertex.name} WHERE ${field.name} = ${QVal(Value.something())}"
sqlTask"SELECT FROM ${vertex.name} WHERE ${field.name} = ${f(Value)}"
sql"SELECT FROM ${vertex.name} WHERE ${QVal(field.name)} = $Value AND ${field2.name} = ${QVal(Value2)}"
 */
package SingularSketches

import scala.io.StdIn
import scala.io.Source
import java.io.File

import scala.collection.mutable.ListBuffer

/**
  * Scan for (sql) query values which are not parameterized
  */
object ScanForValues {
  //regex for finding interpolated query values:                             .*=[\t\n\r ]*\$.*
  //regex for finding interpolated query values with interpolator:           .*(s|sql|sqlTask)"+.*=[\t\n\r ]*\$.*"+.*
  val valWithInterp = ".*(s|sql|sqlTask)\"+.*=[\\t\\n\\r ]*\\$.*\"+.*"
  //regex for finding interpolated query values with
  // interpolator and proper parameterization:                               .*(s|sql|sqlTask)"+.*=[\t\n\r ]*\$\{.*QVal[\t\n\r ]*\(.*\).*\}.*"+.*
  val qvalWithInterp = ".*(s|sql|sqlTask)\"+.*=[\\t\\n\\r ]*\\$\\{.*QVal[\\t\\n\\r ]*\\(.*\\).*\\}.*\"+.*"
  //note: strings without interpolator can be disregarded due to the $ being a string literal in thia case

  def main(args: Array[String]): Unit = {
    StdIn.readLine("\n':quit' to quit\nType line to check for suspicious values:\n") match {
      case ":quit"|":q"   => ()
      case s              => println(isSuspicious(s)); main(args)
    }
    StdIn.readLine("\n':quit' to quit\nType path of dir to scan:\n") match {
      case ":quit"|":q"   => ()
      case s              => scanDir(s, "txt|scala").foreach(f => println(s"${Console.RED}$f${Console.RESET}")); main(args)
    }
  }

  def isSuspicious(query : String): Boolean = {
    (query matches valWithInterp) && !(query matches qvalWithInterp)
  }

  def logResults(line: String, lineIndex: Int): Boolean = {
    val sus = isSuspicious(line)
    if (sus) {
      println(s"${Console.RED}Suspicious pattern was found on line $lineIndex.")
      println(s"${Console.YELLOW + line.substring(0, Math.min(line.length, 50))}...${Console.RESET}")
    }
    sus
  }

  /**
    * Scan file for suspicious patterns.
    *
    * @param path  Absolute path to file.
    * @return      List with every suspicious line number.
    */
  def scanFile(path : String): List[Int] = {
    println(s"${Console.GREEN}Scanning: $path${Console.RESET}")
    var i = 0
    val buff = ListBuffer.empty[Int]
    for (line <- Source.fromFile(path).getLines) {
      if(logResults(line, i)) buff += i
      i += 1
    }
    buff.toList
  }

  /**
    * Scan each file in a directory for suspicious patterns.
    *
    * @param path       Absolute path to directory.
    * @param fileTypes  Regex pattern for file types which should be scanned. Examples: "txt", "txt|scala|js|java" or "cs|css|htm|html|js|java|scala".
    * @return           List with every suspicious file path.
    */
  def scanDir(path : String, fileTypes : String): List[String] = {
    val files = getListOfFiles(path).filter(file =>
      file.isFile && file.canRead && file.getName.matches(s".*\\.($fileTypes)"))
    files.flatMap{f =>
      val path = f.getPath
      if(scanFile(path).nonEmpty) Some(path) else None
    }
  }

  /**
    * @param dir   Absolute path to directory.
    * @return      All files within a directory.
    */
  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

}