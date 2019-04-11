package utils


import java.time.LocalDateTime

import org.apache.commons.mail._

import scala.io.StdIn
import scala.util.{Failure, Success, Try}

object Mail {

  lazy val username  : String       = StdIn.readLine("Log in to send emails. Email:")
  lazy val receivers : Seq[String]  = StdIn.readLine("Receiver addresses (comma separated):").trim.split(",").toSeq
  lazy val password  : Array[Char]  = System.console().readPassword("Log in to send emails. Password:")

  sealed abstract class MailType
  case object Plain extends MailType
  case object Rich extends MailType
  case object MultiPart extends MailType

  init()

  def init() {
    username
    password
    receivers
  }

  def mail(subject : String, content: String, richMessage: Option[String] = None) {
    Try(send a Mail(
      from    = username -> "Anon",
      to      = receivers,
      subject = subject,
      message = content,
      richMessage = richMessage
    )) match {
      case Success(_) => println(s"${Console.GREEN}${LocalDateTime.now} - Your mail, '$subject', got sent to $receivers!${Console.RESET}")
      case Failure(_) => println(s"${Console.RED}username - password, are not correct. If you're using gmail, visit: https://myaccount.google.com/lesssecureapps \nPlease close the app and try again.${Console.RESET}")
    }
  }

  case class Mail(
                   from: (String, String), // (email -> name)
                   to: Seq[String],
                   cc: Seq[String] = Seq.empty,
                   bcc: Seq[String] = Seq.empty,
                   subject: String,
                   message: String,
                   richMessage: Option[String] = None,
                   attachment: Option[java.io.File] = None
                 )

  object send {
    def a(mail: Mail) {
      val format =
        if (mail.attachment.isDefined) MultiPart
        else if (mail.richMessage.isDefined) Rich
        else Plain

      val commonsMail: Email = format match {
        case Plain => new SimpleEmail().setMsg(mail.message)
        case Rich => new HtmlEmail().setHtmlMsg(mail.richMessage.get).setTextMsg(mail.message)
        case MultiPart =>
          val attachment = new EmailAttachment()
          attachment.setPath(mail.attachment.get.getAbsolutePath)
          attachment.setDisposition(EmailAttachment.ATTACHMENT)
          attachment.setName(mail.attachment.get.getName)
          new MultiPartEmail().attach(attachment).setMsg(mail.message)
      }

      // Can't add these via fluent API because it produces exceptions
      mail.to foreach commonsMail.addTo
      mail.cc foreach commonsMail.addCc
      mail.bcc foreach commonsMail.addBcc

      //auth
      commonsMail.setHostName("smtp.googlemail.com")
      commonsMail.setSmtpPort(465)
      commonsMail.setSSLOnConnect(true)
      commonsMail.setAuthentication(username, password.mkString)

      commonsMail.
        setFrom(mail.from._1, mail.from._2).
        setSubject(mail.subject).
        send()
    }
  }
}
