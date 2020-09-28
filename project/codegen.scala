import java.io.File

import sbt.IO

object codegen {
  def genMemo(path: File, n: Int): File = {
    val types: Seq[String] = 1.to(n).map(nn => s"T$nn")
    val args: Seq[String] = 1.to(n).map(nn => s"v$nn")
    val typeArgs = types.mkString(", ")
    val typedArgs = args.zip(types).map { case (tn, t) => s"$tn: $t"}.mkString(", ")
    val argNames = args.mkString(", ")
    val code = s"""
       | package se.randomserver.memoize
       |
       | class Memoize$n[$typeArgs, R](f: ($typeArgs) => R) extends (($typeArgs) => R) {
       |  val memo = new Memoize1[($typeArgs), R]({
       |    case ($argNames) => f($argNames)
       |  })
       |  def apply($typedArgs): R = memo(($argNames))
       | }
       |""".stripMargin
    val file = new File(path, s"Memoize$n.scala")
    IO.write(file, code)
    file
  }

  def run(path: File): Seq[File] = 2.to(15).map(n => genMemo(path, n))
}
