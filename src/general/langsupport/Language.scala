package general.langsupport

import java.awt.image.BufferedImage

/**
  * Class representing a language.
  * @param langCode The language code for the language. Better to be an internationally recognized code.
  */
class Language(val langCode: String, val flagImage: BufferedImage) {

  override def equals(obj: scala.Any): Boolean = obj match {
    case lang: Language => lang.langCode == this.langCode
    case _ => super.equals(obj)
  }

}

object English extends Language("en_EN", null)

object German extends Language("de_DE", null)
