package controllers

import utils.Mail

import scala.concurrent.duration._
import utils.ImgToAscii._

import scala.util.Random

object PrankMailer {
  val interval : Float = 3.5f //in hours
  val images = List(
    "https://images-na.ssl-images-amazon.com/images/I/818%2BI9cEsEL._SY606_.jpg",
    "https://images-na.ssl-images-amazon.com/images/I/719PjppujCL._SY879_.jpg",
    "https://static.fnac-static.com/multimedia/Images/FR/NR/28/32/66/6697512/1540-1/tsp20150313143111/Figurine-parlante-Stuart-Minion-Moi-moche-et-mechant-20-cm.jpg",
    "https://images.halloweencostumes.com/products/29280/1-1/adult-inflatable-minion-stuart-costume.jpg",
    "https://www.runninguniversal.com/images/minion-5k/characters/character_minion1.jpg",
    "https://5.imimg.com/data5/XQ/KP/MY-40305254/kids-toy-500x500.jpg",
    "https://partycity6.scene7.com/is/image/PartyCity/_pdp_sq_?$_1000x1000_$&$product=PartyCity/P594191",
    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR207skr5rSn5IZCquziY0TgYQ9BNbyhiCNoZtSkl-UTJb4E4-O2g",
    "https://d1o51r9qdgnnlz.cloudfront.net/items/9f1ba58d-22a6-4601-b42c-023e788c59e4.png",
    "https://d1o51r9qdgnnlz.cloudfront.net/items/888ffd75-982e-4287-91ed-a35f46a57941.png",
    "https://d1o51r9qdgnnlz.cloudfront.net/items/76d988d0-fb16-4f72-b3b1-71fddf68e7c4.png",
    "https://d1o51r9qdgnnlz.cloudfront.net/items/3a7f5945-b603-4638-b60f-72300de74160.png",
    "https://target.scene7.com/is/image/Target/GUEST_90568301-534d-4c46-a9fd-e217b52ba649?wid=488&hei=488&fmt=pjpeg",
    "https://historiek.net/wp-content/uploads-phistor1/2007/11/Hakenkruis-swastika.png"
  )
  val messages = Map(
    "Docent worden" ->
      """
        |<p>Heyho. Na een poosje denken, kwam ik erachter dat ik later eigenlijk best graag docent zou willen worden. Hoe word je dat? Welk profiel moet ik kiezen als ik docent wil worden?</p>
        |<p>Mvg, Natalie Haagenzoons</p>
        |""".stripMargin -> "Natalie",

    "Hallo :)" ->
      """
        |<p>Beste collega,</p>
        |<p>Daar ga ik dan, even een snel vraagje stellen. Ik kom over een paar weken bij jullie op school werken. Weten jullie nog wat leuke manieren om een voorstelronde te houden met de leerlingen?</p>
        |<p>Groet, Sjoerd van der Kroon :)</p>
        |""".stripMargin -> "Meester Sjoerd",

    "Vraagje" ->
      """
        |<p>Ik ben laatst naar de dokter gegaan en zou graag een uitspraak hierover willen doen tijdens uw eerstvolgende les. Zou u mij dit toestaan ajb?</p>
        |<p>Mvg, Fennekke Voortschaal</p>
        |""".stripMargin -> "Fennekke",

    "Gepest" ->
      """
        |<p>Ik wordt op school gepest en weet niet zo goed bij welke docent ik het moet melden. Zou u me verder kunnen helpen?</p>
        |<p>Mvg, Ibrahim</p>
        |""".stripMargin -> "Ibrahim",

    "Agenda" ->
      """
        |<p>Wat stond er deze maand op de agenda voor uw vak? Ik ben laatst mijn agenda kwijt geraakt. Sorry.</p>
        |<p>Mvg, Salim Iskabar</p>
        |""".stripMargin -> "Salim",

    "Roddels" ->
      """
        |<p>Goede dag,</p>
        |<p>De laatste tijd worden er schadelijke roddels versprijd. Het begint me emotioneel te raken en zou graag willen bespreken of we hier op kunnen ingrijpen.</p>
        |<p>Mvg, Jashawn Laurens</p>
        |""".stripMargin -> "Jashawn",

    "Melding" ->
      """
        |<p>Hallo, Kibo hier. Ik zou graag willen melden dat ik aanstaande maandag niet op school ben wegens persoonlijke redenen.</p>
        |<p>Mvg, Kibo Klaasen</p>
        |""".stripMargin -> "Kibo",

    "Budget" ->
      """
        |<p>Ik heb voor het huidige project wat dure materialen nodig. Zou u mij ajb willen sponsoren?</p>
        |<p>Mvg, Pim</p>
        |""".stripMargin -> "Pim",

    "Grappige foto" ->
      """
        |<p>Ik heb laatst een grappige foto gevonden.</p>
        |<p>Mvg, Pip Gulden</p>
        |""".stripMargin -> "Pip",

    "Datum toets" ->
      """
        |<p>Hi, ik ben op een begrafenis op de datum van de toets. Zou ik het op een andere datum kunnen inhalen?</p>
        |<p>Mvg, Koos Karels</p>
        |""".stripMargin -> "Koos",

    "Inlevering" ->
    """
      |<p>Bij deze lever ik mijn werk in.</p>
      |<p>Mvg, Merel Kaasboer</p>
      |""".stripMargin -> "Merel",

    "Vragen Opdracht" ->
    """
      |<p>Hallo,</p>
      |<p>Ik had nog vragen over de opdracht. Is het mogelijk om deze week een momentje met u in te plannen?</p>
      |<p>Mvg, Jesse Ladderman</p>
      |""".stripMargin -> "Jesse"
  )

  def loop (): Unit = {
    /*Mail.mail(
      "It has started.",
      "It has started, my fellow.",
      Some(
        "Please view the entire message in this mail.<br>"+
        imgToHtml(Random.shuffle(images).head, "&#21325;")))
    */
    println(s"${Console.RED}Terminate this process manually in your terminal.${Console.RESET}")
    prank()
  }

  def prank (): Unit = {
    try {
      val mess = Random.shuffle(messages).head
      Mail.mail(
        mess._1._1,
        "Mail could not be loaded. Please view the entire message in this mail by enabling HTML view...",
        Some(mess._1._2 + imgToHtml(Random.shuffle(images).head, "&#21325;")),
        Some(mess._2)
      )
      Thread.sleep(Duration(interval, HOURS).toMillis)
      prank()
    }
  }
}
