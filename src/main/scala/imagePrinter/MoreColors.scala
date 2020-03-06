package imagePrinter

/**Ansi codes for Console output.
  */
object MoreColors {
  //Foreground
  def BRIGHT_BLACK      = "\u001B[90m"
  def BRIGHT_RED        = "\u001B[91m"
  def BRIGHT_GREEN      = "\u001B[92m"
  def BRIGHT_YELLOW     = "\u001B[93m"
  def BRIGHT_BLUE       = "\u001B[94m"
  def BRIGHT_MAGENTA    = "\u001B[95m"
  def BRIGHT_CYAN       = "\u001B[96m"
  def BRIGHT_WHITE      = "\u001B[97m"

  //Background
  def BRIGHT_BLACK_B    = "\u001B[100m"
  def BRIGHT_RED_B      = "\u001B[101m"
  def BRIGHT_GREEN_B    = "\u001B[102m"
  def BRIGHT_YELLOW_B   = "\u001B[103m"
  def BRIGHT_BLUE_B     = "\u001B[104m"
  def BRIGHT_MAGENTA_B  = "\u001B[105m"
  def BRIGHT_CYAN_B     = "\u001B[106m"
  def BRIGHT_WHITE_B    = "\u001B[107m"

  //foreground
  def COLOR(r : Int, g : Int, b : Int)      = s"\u001B[38;2;$r;$g;${b}m"
  def COLOR_256(n : Int)                    = s"\u001B[38;5;${n}m"

  //background
  def COLOR_B(r : Int, g : Int, b : Int)    = s"\u001B[48;2;$r;$g;${b}m"
  def COLOR_256_B(n : Int)                  = s"\u001B[48;5;${n}m"
}
