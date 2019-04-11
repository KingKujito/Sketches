import org.apache.commons.validator.routines.UrlValidator

package object utils {
  val urlValidator = new UrlValidator()

  def urlValidator(url: String): Boolean = { // Get an UrlValidator using default schemes
    urlValidator.isValid(url)
  }
}
