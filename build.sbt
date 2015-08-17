name := "ammonite-sshd"

organization := "com.github.dant3"

version := "0.0.1"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xlint")

javacOptions ++= Seq("-encoding", "UTF-8")

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))

libraryDependencies ++= Seq(
  "com.lihaoyi" % "ammonite-repl_2.11.6" % "0.4.5",
  "org.slf4j" % "slf4j-api" % "1.7.12",
  "org.slf4j" % "slf4j-nop" % "1.7.12",
  // -- for debug:
  // "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.bouncycastle" % "bcprov-jdk15on" % "1.50",
  "org.bouncycastle" % "bcpkix-jdk15on" % "1.50",
  "commons-codec" % "commons-codec" % "1.9",
  "org.apache.sshd" % "sshd-core" % "0.10.0"
)
