package SingularSketches

import scala.io.StdIn
import scala.language.implicitConversions

object InjectionProtection {
  /**Removes all illegal characters for a string which will be used in a SQL query.
    *
    * The illegal characters are by standard replaced with an '*' instead of an empty string to prevent usernames like
    * 'dev==={}{}\\\' from getting access to the contact with the username 'dev'. Since the '*' is an illegal character,
    * it will never match an entry in the db.
    *
    * example for empty string replacement: 'dev===' -> 'dev'    -> get access to 'dev'
    * example for '*'   string replacement: 'dev===' -> 'dev***' -> can't possibly match a user in the db
    *
    * @param string     The input to convert.
    * @param inputType  Determines which regex pattern to use.
    * @return           A string which can safely be used in a SQL query
    */
  def sanitize (string : String, inputType: InputType, replacement : String = "*"): String = {
    inputType match {
      case input : SaveMatch      =>
        val pPattern = input.persistPattern.r                               //turn pattern to Regex
        val s = sanitize(string).replaceAll(input.pattern, replacement)     //Replace all illegal chars with the replacement
        pPattern.findAllMatchIn(s).mkString                                 //Remove all illegal patterns

      case input : ReplaceMatch   =>
        sanitize(string).replaceAll(input.pattern, replacement)             //Simply replace all illegal characters
    }
  }

  def sanitize (string : String): String = {
    string.replace("\\", "\\\\")  //Make sure that escape characters are safely handled first.
          .replace("'" , "\\'")   //A single quote ' has no linguistic use. An apostrophe ’ or quotation marks ‘ ’
                                                     // are a linguisticly correct substitute for a single quote but way safer
                                                    //  to use when dealing with SQL queries.
  }

  def sanitize (string : Option[String]): Option[String] =
    string.flatMap {
      case s => Some(s.replace("'", "’"))
      case _ => None
    }


  /**For help with regex, checkout: https://regexr.com/
    */
  sealed trait InputType{ def pattern : String }
  sealed trait SaveMatch           extends InputType {  //Replace all but those matching the regex pattern
    def persistPattern : String                         //Patterns to save. Example: "pattern1|[characters]|pattern2"
                                                       // the pattern must contain all but persistPattern characters in a
                                                      //  character set. Example:
                                                     //        persistPattern="po|pi"   pattern="[^poi]"
  }
  sealed trait ReplaceMatch        extends InputType    //Replace all matching the regex pattern

  /** Saves only letters, safe single quotes and apostrophes
    */
  final case object Name           extends SaveMatch {
    //Special characters and letters from: https://stackoverflow.com/a/2385967
    //TODO replace pattern for exceptional character with https://stackoverflow.com/a/45871742 or find other solutions to support Asian, Middle-Eastern and Eastern-European names
    val persistPattern  =
      """([a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð\-., ]|(\\')|’|\*)"""  //TODO make \* a parameter to allow for different replacement strings
    val pattern         =
      """[^a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð\\'’\*\-., ]"""
  }

  final case object Alphanumeric   extends ReplaceMatch {
    val pattern = """[^a-zA-Z0-9]"""
  }
  final case object Username       extends ReplaceMatch {
    val pattern = """[^a-zA-Z0-9_\\'\-’.]"""
  }
  final case object Password       extends ReplaceMatch {
    val pattern = """[\t\n\r ]"""
  }

  def main(args: Array[String]): Unit = {
    //inputToAlphanumeric
    examples("""O’Häére's_1.5ft chickpea bush!!!...\\\'''_\\'_\--%27 /u200 ALT+0162 0024+ALT+X &#39; &quot; %20 U+002C""")
    //examples("""dev' AND 'x'='x""")
    //examples("""dev'""")
    //examples("""O’Häére-Jåson, Jr.'s good ol' round o'golf...""")
  }

  def inputToAlphanumeric(): Unit  = {
    println(sanitize(StdIn.readLine("Convert to name: "           ), Name))
    println(sanitize(StdIn.readLine("Convert to alphanumerical: " ), Alphanumeric))
    println(sanitize(StdIn.readLine("Convert to username: "       ), Username))
    println(sanitize(StdIn.readLine("Convert to password: "       ), Password))
    println(sanitize(StdIn.readLine("Convert to safe quote (and escape characters): ")))
  }

  def examples(input : String): Unit  = {
    println(s"\n\noriginal: $input\n-------------More examples:-------------")
    println("Convert to name\n"                                 + sanitize(input, Name)         +"\n-------------\n")
    println("Convert to alphanumerical: "                       + sanitize(input, Alphanumeric) +"\n-------------\n")
    println("Convert to username: "                             + sanitize(input, Username)     +"\n-------------\n")
    println("Convert to password: "                             + sanitize(input, Password)     +"\n-------------\n")
    println("Convert to safe quote (and escape characters): "   + sanitize(input)               +"\n-------------\n")
  }

}



