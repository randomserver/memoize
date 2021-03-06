lazy val supportedVersions = List("2.13.3", "2.12.12")
enablePlugins(GitVersioning)

def publishSettings(): Seq[Def.Setting[_]] = (for {
  repo     <- sys.env.get("MAVEN_REPO_URL")
  username <- sys.env.get("MAVEN_REPO_USER")
  password <- sys.env.get("MAVEN_REPO_PASS")
  host = url(repo).getHost
} yield Seq(
  credentials += Credentials("GitHub Package Registry", host, username, password),
  publishTo := {
    Some("GitHub Package Registry" at repo)
  }
)).getOrElse(Seq.empty)

ThisBuild / name := "memoize"
ThisBuild / organization := "se.randomserver"
ThisBuild / organizationHomepage := Some(url("https://github.com/randomserver"))
ThisBuild / organizationName := "randomserver.se"
ThisBuild / scalaVersion := "2.13.3"
ThisBuild / crossScalaVersions := supportedVersions

publishSettings()
git.useGitDescribe := true

Compile / sourceGenerators += task {
  codegen.run((Compile / sourceManaged).value / "se/randomserver/memoize")
}

Compile / scalacOptions ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, n)) if n >= 13 => Seq("-Ymacro-annotations")
    case _ => Nil
  }
}

libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, n)) if n <= 12 =>
      Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full))
    case _ => Nil
  }
} ++ Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
)
