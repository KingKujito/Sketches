import reflect.io._
import Path._
import scala.util.Try
import sys.process._

/**
  * Compacts entire schema and data set into one migration.
  *
  * Important: make sure the migrations you want to compact have been applied and no sample data has been loaded into the db.
  *
  * Example use:
  *   - clean db
  *   - sbt run-local (to apply migration, keep sample data on "none")
  *   - sbt compact-db {start} {end}
  */
object DbCompactor {
  val insertsFile = "target/inserts_temp.sql"
  val insertsCommand: String =
    """pg_dump --dbname=postgres://postgres:docker@127.0.0.1:5432/postgres --data-only --exclude-table=flyway_schema_history --column-inserts -w"""

  val schemaFile = "target/schema_temp.ddl"
  val schemaCommand: String =
    """pg_dump --dbname=postgres://postgres:docker@127.0.0.1:5432/postgres --schema-only --exclude-table=flyway_schema_history -w"""

  def apply(args: Seq[String]): Unit = {
    println(args)
    if(args.length >= 2 && args.length <= 3) {
      val versionRange = args.head.toInt to args(1).toInt

      val directoryPath = new java.io.File(".").getCanonicalPath + "/conf/db/migration"


      val revert = args.contains("revert")

      val high = versionRange.reverse.head
      val low = versionRange.head
      val newFile = s"$directoryPath/V${high + 1}__baseline_${low}_to_$high.sql"

      if (revert) {
        if (Try(new java.io.File(newFile).delete()).isFailure)
          println(s"Could not delete $newFile")
        else
          println(s"Deleted $newFile")

        if (revertQueries(versionRange, directoryPath).contains(false))
          println(s"Not all files could be renamed! Please inspect $directoryPath")
        else
          println(s"Successfully reverted $directoryPath")
      } else {
        if (Try {
          val pw = new java.io.PrintWriter(new java.io.File(newFile))
          pw.write(
            output(concatter(getQueries))
          )
          pw.close()
        }.isFailure)
          println(s"Could not create $newFile with compacted queries. Please inspect the $directoryPath folder.")
        else {
          renameFiles(versionRange, directoryPath)
          println(s"Compaction complete! Check out $newFile")
        }
      }
    } else println("Invalid arguments. Expected: (start: Int, end: Int) and optional argument \"revert\"")
  }

  def revertQueries(versionRange: Range, directoryPath: String): List[Boolean] = {
    val files = (directoryPath.toDirectory.files map (_.name)).toList
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

  def getQueries: List[String] = {
    println("Trying to read lines...")
    Try {
      List(schemaCommand.!!, insertsCommand.!!)
    }.toOption.get
  }

  //convert to .txt
  def renameFiles(versionRange: Range, directoryPath: String): Unit = {
    val files = (directoryPath.toDirectory.files map (_.name)).toList
    for {
      version <- versionRange
      f = files filter (_.matches(s"""V${version}__.*\\.sql"""))
      _ = println(s"Migrations found: $f")
    } yield f.foreach { filename =>
      val filePath = directoryPath + "/" + filename
      println(s"Handling $filename")
      if (Try(new java.io.File(filePath)
        .renameTo(new java.io.File(directoryPath + "/" + filename.replace("sql", "txt")))).isFailure)
        println(s"Could not rename $filePath")
    }
  }

  def concatter(sqlQueries : List[String]): String = {
    println(s"Compacting ${sqlQueries.length} queries...")
    sqlQueries.mkString("\n")
  }

  def output(concattedQueries : String): String =
    s"""
       |--compacted db queries
       |--run the following commands to manually reproduce these results
       |-- inserts:
       |--         $insertsCommand
       |-- schema:
       |--         $schemaCommand
       |$concattedQueries
       |
       """.stripMargin
}
