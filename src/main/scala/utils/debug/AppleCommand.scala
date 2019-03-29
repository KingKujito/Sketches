package utils.debug

/**The functions within this code could help with debugging.
  *
  * Example use case: there are too many logs for a println or logger to provide useful information.
  */
object AppleCommand {

  /**Makes the Apple narrator say something.
    *
    * @param s string to narrate
    */
  def say(s : String): Process =
    Runtime.getRuntime.exec( Array[String]("osascript", "-e", s"""say "$s"""" ))

  /**Spawns an Apple notification on the screen.
    *
    * @param content    A description of the notification
    * @param sound      The sound this notification should make
    */
  def notification(content: String="", subtitle: String="", title: String="GMS", sound: String=""): Process =
    Runtime.getRuntime.exec( Array[String]("osascript", "-e", s"""display notification "$content" with title "$title" subtitle "$subtitle" sound name "$sound" """ ))
}
