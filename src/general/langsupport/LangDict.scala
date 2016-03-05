package general.langsupport

import general._
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.io.Source
import scala.language.dynamics

class LangDict() {
  var data: JValue = null

  def this(path: String) = {
    this()
    addJSON(path)
  }

  def addJSON(path: String): LangDict = {
    val source = Source.fromFile("src/resources/data/language/" + path).getLines().mkString("\n");
    data = parse(source).merge(data)
    this
  }

  def translate(identifier: String, locale: String): String = {
    val trans = compact(render(data \ identifier \ locale))
    val eng = compact(render(data \ identifier \ "en_EN"))
    if (trans != "")
      trans
    else if (eng != "")
      eng
    else
      identifier
  }

  def tr(identifier: String): String = translate(identifier, Main.getLanguage.langCode)

  @annotation.varargs
  def tr(identifier: String, args: Object*):String = String.format(tr(identifier), args:_*)
}