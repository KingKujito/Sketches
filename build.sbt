name := "sketches"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.scalikejdbc"   %% "scalikejdbc"        % "3.3.2",
  "com.itsmeijers"    %% "scala-oriented"     % "0.1.2"     //does not work for scala version > 2.11.x
)