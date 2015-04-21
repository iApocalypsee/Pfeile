package general.langsupport

import scala.languageFeature.dynamics

/**
 * This class aims to resolve the English-German language problem.
 * I am going to load in some language files so that Pfeile can switch between languages.
 */
class LangDict extends Dynamic {



}

class LangDictEntryCollection private[langsupport](entries: Seq[LangDictEntry]) {
  for(entry <- entries) {
    require(entries.count(_ == entry) == 1, s"Multiple language entries for qualifier ${entry.uniqueIdentifier}")
  }
}

case class LangDictEntry private[langsupport](uniqueIdentifier: String, languageMapping: Map[Language, String]) {

  def inLanguage(lang: Language): Option[String] = languageMapping.get(lang)

}
