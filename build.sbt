val scala3Version = "3.2.1"

lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
}
lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
lazy val root = project
  .in(file("."))
  .settings(
    name := "cats.mvc.framework",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "org.scalafx" %% "scalafx" % "16.0.0-R25",
    libraryDependencies ++= javaFXModules.map(m =>
      "org.openjfx" % s"javafx-$m" % "16" classifier osName
    ),
    libraryDependencies += "org.typelevel" %% "cats-effect" % "3.4.5",
    libraryDependencies += "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test,
    libraryDependencies += "org.typelevel" %% "cats-effect-cps" % "0.4.0",

    libraryDependencies += "org.typelevel" %% "cats-effect-kernel" % "3.4.5",
    libraryDependencies += "org.typelevel" %% "cats-effect-std" % "3.4.5",

    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.15",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % "test",
    libraryDependencies += "org.typelevel" %% "cats-effect-testkit" % "3.4.5" % Test
  )
