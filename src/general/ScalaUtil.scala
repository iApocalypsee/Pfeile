package general

import java.util.Date
import java.util.concurrent.TimeUnit

import akka.actor._

import scala.collection.JavaConversions
import scala.concurrent.duration.FiniteDuration
import scala.reflect.ClassTag

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

  def message(header: String, desc: String = "No description available", stacktrace: String = "No stacktrace", vartrace: String = "No vartrace", post: String = ""): String =
    s"""
       |===== $header =====
       |Description: $desc
       |
       |Stack trace:
       |$stacktrace
       |
       |Var trace (optional):
       |$vartrace
       |
       |Generated when: ${new Date()}
       |$post
     """.stripMargin

  def errorMessage(desc: String, stacktrace: String = "No stacktrace", vartrace: String = "No vartrace"): String =
    message("ERROR MESSAGE", desc, stacktrace, vartrace, "Please describe this issue in detail when reporting.")

  def errorMessage(desc: String, stacktrace: String): String = errorMessage(desc, stacktrace, "")

  def errorMessage(desc: String, t: Throwable, vartrace: String): String = errorMessage(desc, Util.stacktraceString(t), vartrace)

  def errorMessage(desc: String, t: Throwable, vartrace: Map[String, Any]): String = errorMessage(desc, t, varTrace(vartrace))

  def errorMessage(desc: String, vartrace: Map[String, Any]): String = errorMessage(desc, "", varTrace(vartrace))

  def errorMessage(desc: String, t: Throwable): String = errorMessage(desc, Util.stacktraceString(t))

  def infoMessage(vtrace: Map[String, Any]) = message("Information", vartrace = ScalaUtil.varTrace(vtrace))

  //</editor-fold>

  //<editor-fold desc="Programmer's delight">

  def stringRepresentation(x: Map[String, Any]): String =
    s"{${x.foldLeft("") { (c, kv) => s"$c, ${kv._1}=${kv._2}" } substring 2}}"

  def stringRepresentation(objectName: String, x: Map[String, Any]): String = s"$objectName{${stringRepresentation(x)}}"

  def stringRepresentation(any: Any, x: Map[String, Any]): String = stringRepresentation(objectDesc(any), x)

  def objectDesc(x: Any) = /*s"${x.getClass.getAnnotations.foldLeft("") { (c, a) => s"$c @${a.annotationType().getName}" }}" +*/
    s"${x.getClass.getName}@${x.hashCode()}"

  def awaitReply[A](msg: AnyRef, target: ActorRef, timeout: FiniteDuration, retType: Class[A]): A = {
    awaitReply(Inbox.create(JavaInterop.Implicits.actorSystem), msg, target, timeout, retType)
  }

  def awaitReply[A](sender: Inbox, msg: AnyRef, target: ActorRef, timeout: FiniteDuration, retType: Class[A]): A = {
    require(sender != null)
    sender.send(target, msg)
    retType.cast(sender.receive(timeout))
  }

  def awaitReply[A](msg: AnyRef, target: ActorRef, timeoutMillis: Long, retType: Class[A]): A = {
    awaitReply(msg, target, FiniteDuration(timeoutMillis, TimeUnit.MILLISECONDS), retType)
  }

  def awaitReply[A](sender: Inbox, msg: AnyRef, target: ActorRef, timeout: FiniteDuration)(implicit clsEvidence: ClassTag[A]): A = {
    awaitReply(sender, msg, target, timeout, clsEvidence.runtimeClass).asInstanceOf[A]
  }

  def awaitReply[A](msg: AnyRef, target: ActorRef, timeout: FiniteDuration)(implicit clsEvidence: ClassTag[A]): A = {
    awaitReply[A](Inbox.create(JavaInterop.Implicits.actorSystem), msg, target, timeout)
  }

  //</editor-fold>


}
