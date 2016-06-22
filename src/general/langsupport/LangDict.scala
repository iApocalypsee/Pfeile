package general.langsupport

import org.json4s._
import org.json4s.native.JsonMethods._

import scala.io.Source
import scala.language.dynamics

class LangDict() {
  var data: JValue = JNothing

  def this(path: String) = {
    this()
    addJSON(path)
  }

  def addJSON(path: String): LangDict = {
    val source = Source.fromFile("src/resources/data/language/" + path).getLines().mkString("\n");
    data = parse(source).merge(data)
    System.out.printf("Loaded localisation file: %s\n", path)
    this
  }

  def translate(identifier: String, locale: String): String = {
    data \ identifier \ locale match {
      case JNothing => data \ identifier \ "en_EN" match {
        case JNothing =>
          System.err.printf("Translation identifier not found: %s\n", identifier)
          identifier
        case JString(s) => s
        case _ =>
          System.err.printf("Translation identifier not found: %s\n", identifier)
          identifier
      }
      case JString(s) => s
      case _ =>
        System.err.printf("Translation identifier not found: %s\n", identifier)
        identifier
    }
  }

  def printTree(): Unit = {
    System.out.println(pretty(render(data)))
  }
}
