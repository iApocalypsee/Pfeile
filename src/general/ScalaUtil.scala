package general

import java.util.Date

import scala.collection.JavaConversions

object ScalaUtil {

  //<editor-fold desc="Functional: Function arity conversions">

  trait FunctionArityConversion[From, To] {
    def convert(from: From): To
  }

  implicit def zeroToOneArity[A1, R](f: () => R): FunctionArityConversion[() => R, A1 => R] = new FunctionArityConversion[() => R, A1 => R] {
    override def convert(from: () => R) = (_: A1) => from()
  }

  implicit def identicalArity[FunctorType](f: FunctorType): FunctionArityConversion[FunctorType, FunctorType] = new FunctionArityConversion[FunctorType, FunctorType] {
    override def convert(from: FunctorType) = from
  }

  //</editor-fold>

  //<editor-fold desc="Error/Debug handling">

  /**
    * Constructs a nicely formatted list of all entries in the map.
    * Useful for displaying states of variables in an error message.
    * Ex.:
    * {{{
    *   varTrace(Map("x" <- 2, "name" <- "John Arbuckle", "age" <- 30)) =
    *   """
    *     - x=2
    *     - name="John Arbuckle"
    *     - age=30
    *   """
    * }}}
    */
  def varTrace(varMap: Map[String, Any]): String = {
    varMap.foldLeft("") { (c, kv) =>
      val (varname, varvalue) = kv
      c + s" - $varname=$varvalue\n"
    }
  }

  def varTrace(javaVarMap: java.util.Map[String, Any]): String = varTrace(JavaConversions.mapAsScalaMap(javaVarMap).toMap)

  def infoMessageVarTrace(vartrace: String) =
    s"""
       | ===== VARTRACE =====
       | $vartrace
       | ===== VARTRACE END =====
       |
     """.stripMargin

  def errorMessage(desc: String, stacktrace: String, vartrace: String): String =
    s"""
       |===== ERROR MESSAGE =====
       |Description: $desc
       |
       |Stack trace:
       |$stacktrace
       |
       |Var trace (optional):
       |${if(vartrace == "") "No vartrace" else vartrace}
       |
       |Generated when: ${new Date()}
       |
       |Please describe the circumstances of this issue in detail when reporting.
     """.stripMargin

  def errorMessage(desc: String, stacktrace: String): String = errorMessage(desc, stacktrace, "")

  def errorMessage(desc: String, t: Throwable, vartrace: String): String = errorMessage(desc, Util.stacktraceString(t), vartrace)

  def errorMessage(desc: String, t: Throwable): String = errorMessage(desc, Util.stacktraceString(t))

  //</editor-fold>

  //<editor-fold desc="Programmer's delight">

  def stringRepresentation(x: Map[String, Any]): String =
    s"{${x.foldLeft("") { (c, kv) => s"$c, ${kv._1}=${kv._2}" }}}"

  def stringRepresentation(objectName: String, x: Map[String, Any]): String = s"$objectName{${stringRepresentation(x)}}"

  def stringRepresentation(any: Any, x: Map[String, Any]): String = stringRepresentation(objectDesc(any), x)

  /*
  def stringRepresentation(x: Any): String = stringRepresentation(x, x.getClass.getDeclaredFields.map(field => {
    field.setAccessible(true)
    (field.getName, field.get(x))
  }).toMap)
  */

  def objectDesc(x: Any) = /*s"${x.getClass.getAnnotations.foldLeft("") { (c, a) => s"$c @${a.annotationType().getName}" }}" +*/
    s"${x.getClass.getName}"

  //</editor-fold>


}
