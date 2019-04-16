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

  val luceneVal      = """.*(LUCENE|lucene|Lucene)[ \t\n\r]*\(?'?\$.+'?\)?.*"""
  val luceneQVal     = """.*(LUCENE|lucene|Lucene)[ \t\n\r]*\(?'?\$\{[ \t\n\r]*QVal[ \t\n\r]*(.+)[ \t\n\r]*\}[ \t\n\r]*'?\)?.*"""

  def main(args: Array[String]): Unit = {
    StdIn.readLine("\n':quit' to quit\nType line to check for suspicious values:\n") match {
      case ":quit"|":q"   => ()
      case s              => println(isSuspicious(s)); main(args)
    }
    StdIn.readLine("\n':quit' to quit\nType path of dir to scan:\n") match {
      case ":quit"|":q"   => ()
      case s              => scanDirDeep(s, "txt|scala").foreach(f => println(s"${Console.RED}$f${Console.RESET}")); main(args)
    }
  }

  def isSuspicious(query : String): Boolean = {
    (query matches valWithInterp) && !(query matches qvalWithInterp) ||
    (query matches luceneVal)     && !(query matches luceneQVal)
  }

  def logResults(line: String, lineIndex: Int): Boolean = {
    val sus = isSuspicious(line)
    if (sus) {
      println(s"${Console.RED}Suspicious pattern was found on line $lineIndex.")
      println(s"${Console.YELLOW + line.substring(0, Math.min(line.length, 150))}...${Console.RESET}")
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
    * Scan each file in a directory and subdirectories for suspicious patterns.
    *
    * @param path       Absolute path to top-most directory.
    * @param fileTypes  Regex pattern for file types which should be scanned. Examples: "txt", "txt|scala|js|java" or "cs|css|htm|html|js|java|scala".
    * @return           List with every suspicious file path.
    */
  def scanDirDeep(path : String, fileTypes : String): List[String] = {
    val all   = getListOfFiles(path)
    val dirs  = all.filter(_.isDirectory)
    val files = all.filter(file =>
      file.isFile && file.canRead && file.getName.matches(s".*\\.($fileTypes)"))

    val buff = ListBuffer.empty[String]

    files.foreach{f =>
      val path = f.getPath
      if(scanFile(path).nonEmpty) buff += path
    }
    dirs.foreach(d => scanDirDeep(d.getAbsolutePath, fileTypes).foreach(f => buff += f))

    buff.toList
  }

  /**
    * @param dir   Absolute path to directory.
    * @return      All files within a directory.
    */
  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.toList
    } else {
      List[File]()
    }
  }

}
