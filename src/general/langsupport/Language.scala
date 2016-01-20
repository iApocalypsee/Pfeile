package general.langsupport

import java.awt.image.BufferedImage

import general.{ImmutableObjectManagerFacade, ObjectManager}

/**
  * Class representing a language.
  * @param langCode The language code for the language. Better to be an internationally recognized code.
  */
class Language(val langCode: String, val flagImage: BufferedImage) {

  Language.accessObjManager.manage(this)

  override def equals(obj: scala.Any): Boolean = obj match {
    case lang: Language => lang.langCode == this.langCode
    case _ => super.equals(obj)
  }

}

object Language {

  private val accessObjManager = new ObjectManager[Language]
  private val objectManager = new ImmutableObjectManagerFacade(accessObjManager)

  def availableLanguages = objectManager.objects

  def findLanguage(langCode: String) = availableLanguages.find(lang => lang.langCode.equals(langCode))
  def isAvailable(langCode: String) = findLanguage(langCode).isDefined

  def packLangEntryPair(kv: (String, String)): Option[(Language, String)] = {
    val (lang, string) = kv
    val langObject = findLanguage(lang)
    langObject.map { language => (language, string) }
  }

}

object English extends Language("en_EN", null) {
  def instance = this
}

object German extends Language("de_DE", null) {
  def instance = this
}
