name := "sketches"

version := "0.1"

scalaVersion := "2.11.12"

lazy val compactDb = InputKey[Unit]("compact-db")
compactDb := {
  val args: Seq[String] = complete.DefaultParsers.spaceDelimited("<arg>").parsed
  DbCompactor(args)
}

libraryDependencies ++= Seq(
  "org.scalikejdbc"          %% "scalikejdbc"        % "3.3.2",
  "org.apache.commons"        % "commons-email"      % "1.5",
  "commons-validator"         % "commons-validator"  % "1.4.0",
  "com.itsmeijers"           %% "scala-oriented"     % "0.1.2",     //does not work for scala version > 2.11.x
  "org.scalatest"            %% "scalatest"          % "3.2.0-SNAP10" % Test,
  "org.seleniumhq.selenium"   % "selenium-server"    % "3.141.59",
  "com.chuusai"              %% "shapeless"          % "2.3.3"
)