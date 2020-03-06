package SingularSketches
import reflect.io._
import Path._
import scala.io.{Source, StdIn}
import scala.util.Try

object SqlConcatter {
  def main(args: Array[String]): Unit = {
    val versionRange =
      StdIn.readLine("Which migration version to start at? (whole numbers only)\n").trim.toInt to
        StdIn.readLine("Which migration version to end at? (whole numbers only, from {start} to {end})\n").trim.toInt

    println(s"\n$versionRange\n")

    val directoryPath = StdIn.readLine("Which directory contains your migrations? (full directory path)\n")

    println(s"\n$directoryPath\n")

    val revert = StdIn.readLine(
      """
        |Commands :
        |    {revert}   - reverts V{start to end}__*.txt back to V{start to end}__*.sql and delete V{end+1}__baseline_{start}_to_{end}.sql
        |    {convert}  - converts V{start to end}__*.sql into V{start to end}__*.txt and generates a V{end+1}__baseline_{start}_to_{end}.sql based on the queries
      """.stripMargin
    ) == "revert"

    val high     = versionRange.reverse.head
    val low      = versionRange.head
    val newFile  = s"$directoryPath/V${high + 1}__baseline_${low}_to_$high.sql"

    if(revert) {
      if(Try(new java.io.File(newFile).delete()).isFailure)
        println(s"Could not delete $newFile")
      else
        println(s"Deleted $newFile")

      if(revertQueries(versionRange, directoryPath).contains(false))
        println(s"Not all files could be renamed! Please inspect $directoryPath")
      else
        println(s"Successfully reverted $directoryPath")
    } else {
      if (Try {
        val pw = new java.io.PrintWriter(new java.io.File(newFile))
        pw.write(
          output(concatter(getQueries(versionRange, directoryPath)))
        )
        pw.close()
      }.isFailure)
        println(s"Could not create $newFile with concat queries. Please inspect the $directoryPath folder.")
      else
        println(s"Concat complete! Check out $newFile")
    }
  }

  def revertQueries(versionRange: Range, directoryPath: String): List[Boolean] = {
    val files = (directoryPath.toDirectory.files map (_.name)).toList
    println(s"Files found: \n\t${files.sorted.mkString("\n\t")}\n")
    (for {
      version <- versionRange
      f = files filter (_.matches(s"""V${version}__.*\\.txt"""))
      _ = println(s"Migrations found: $f")
    } yield f.map { filename =>
      val filePath = directoryPath + "/" + filename
      println(s"Handling $filename")
      Try(new java.io.File(filePath)
        .renameTo(new java.io.File(directoryPath + "/" + filename.replace("txt", "sql")))).isSuccess
    }).flatten.toList
  }

  def getQueries(versionRange: Range, directoryPath: String): List[String] = {
    val files = (directoryPath.toDirectory.files map (_.name)).toList
    println(s"Files found: \n\t${files.sorted.mkString("\n\t")}\n")
    (for {
      version <- versionRange
      f = files filter (_.matches(s"""V${version}__.*\\.sql"""))
      _ = println(s"Migrations found: $f")
    } yield f.map { filename =>
      val filePath = directoryPath + "/" + filename
      println(s"Handling $filename")
      val query = Source.fromFile(filePath).getLines.mkString("\n")
      if (Try(new java.io.File(filePath)
        .renameTo(new java.io.File(directoryPath + "/" + filename.replace("sql", "txt")))).isFailure)
        println(s"Could not rename $filePath")
      s"--$filename--\n" + query
    }).flatten.toList
  }

  def concatter(sqlQueries : List[String]): String = {
    println(s"Concatting ${sqlQueries.length} queries...")
    sqlQueries.mkString("\n")
  }

  def output(concattedQueries : String): String =
    s"""
       |do ${"$$"}
       |  declare
       |    flywayVersion INTEGER;
       |  begin
       |    select coalesce(max(installed_rank), 0) INTO flywayVersion FROM flyway_schema_history;
       |    IF flywayVersion = 0 THEN
       |      --These queries have been automatically concatenated
       |      --Please inspect where manual touchups are needed
       |      --Some queries can be ommitted or simplified (alter table to add column = column on initial table creation, don't insert to delete later)
       |
       |      $concattedQueries
       |
       |    END IF;
       |end${"$$"};
       |
       """.stripMargin
}
