package se.randomserver.memoize

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox
@compileTimeOnly("")
class memoize extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro memoizeMacro.impl
}

object memoizeMacro {
  case class TooManyArgumentsException(msg: String) extends Throwable

  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    def wrapFunction(funDef: DefDef): c.Expr[Any] = {
      try {
        val q"..$mods def ${ename: TermName}(..${paramss: List[ValDef]}): ${tpeopt} = $expr" = funDef
        val memoName = TermName(ename.toString() ++ "Memo")
        val memoClass = TypeName(s"Memoize${paramss.size}")
        val vals = paramss.map(p => p.name)
        val types: List[Type] = paramss.map(p => p.tpe) :+ funDef.tpe

        val tree = q"""
           val $memoName = new se.randomserver.memoize.$memoClass( (..$paramss) => {$expr})
           def $ename(..$paramss): $tpeopt = $memoName(..$vals)
         """
        print(tree)
        c.Expr[Any](tree)
      } catch {
        case _: MatchError => c.abort(c.enclosingPosition, "Could not unlift function definition")
      }
    }

    annottees.map(_.tree) match {
      case (funDef: DefDef) :: _ => wrapFunction(funDef)
      case _ => c.abort(c.enclosingPosition, "Invalid annottee")
    }
  }
}
