package SingularSketches

import org.scalatest._
import ScanForValues._

class ScanForValuesSpec extends FlatSpec with Matchers {
  val path  : String = new java.io.File(".").getCanonicalPath
  val iqDir : String = s"$path/src/test/resources/InterpolatedQueries/"

  "A line" should "be scanned successfully" in {
    isSuspicious("""sql"SELECT FROM $vert WHERE $field = $val" """)                                     should be( true )
    isSuspicious("""sqlTask"SELECT FROM $vert WHERE $field = $val" """)                                 should be( true )
    isSuspicious("""sql"SELECT FROM vert WHERE field = $val" """)                                       should be( true )
    isSuspicious("""sql"SELECT FROM $vert WHERE $field = $val AND $field2={QVal(val2)}" """)            should be( true )

    isSuspicious("""sql"SELECT FROM $vert WHERE $field = val" """)                                      should be( false )
    isSuspicious("""sqlTask"SELECT FROM $vert WHERE $field = ${QVal(val)}" """)                         should be( false )
    isSuspicious("""sql"SELECT FROM vert WHERE field = QVal{$val}" """)                                 should be( false )
    isSuspicious("""sql"SELECT FROM $vert WHERE $field = ${QVal(val)} AND $field2=${QVal(val2)}" """)   should be( false )

    isSuspicious(""" "SELECT FROM $vert WHERE $field = $val" """)                                       should be( false )
    isSuspicious(""" "SELECT FROM $vert WHERE $field = $val" """)                                       should be( false )
    isSuspicious(""" "SELECT FROM vert WHERE field = $val" """)                                         should be( false )
    isSuspicious(""" "SELECT FROM $vert WHERE $field = ${QVal(val)} AND $field2=$val2" """)             should be( false )
  }

  "A file" should "be scanned successfully" in {
    scanFile(iqDir+"queries.txt") should be( List(0,1,2,4,5,7,9,10,20) )
  }

  "A directory" should "be scanned successfully" in {
    scanDir(iqDir, "txt") should be( List(iqDir+"queries.txt") )
  }
}
