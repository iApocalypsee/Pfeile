package general.langsupport

import java.net.URI

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import akka.{Main => AkkaMain}
import general._
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.beans.BeanProperty
import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.io.Source
import scala.language.dynamics
import scala.languageFeature.dynamics

/**
  * This class aims to resolve the English-German language problem.
  * I am going to load in some language files so that the program can switch between languages.
  *
  * The whole language dictionary structure is threaded, so responses should come up very fast and asynchronously
  * with the help of futures.
  *
  * @param owner The person (or group) to which the underlying data of the language dictionary belongs to.
  *              Use this to avoid collision between multiple authors of translations.
  * @author iApocalypsee
  */
class LangDict private (@BeanProperty val owner: String) extends Dynamic {

  import general.langsupport.LangDict._

  //<editor-fold desc="Threaded core">

  /**
    * Threaded core of the language dictionary.
    */
  private class TranslationProvider extends Actor {

    /**
      * @return An empty translation buffer. Can be used to save some typing.
      */
    def emptyTranslationMap = mutable.Map[String, mutable.Map[Language, String]]()

    private val translations = emptyTranslationMap

    def addTranslation(rememberTranslation: RememberTranslation): Unit = {

      /**
        * Called when given identifier is not registered in the translations map.
        * @param identifier The new identifier.
        * @param translation The first translation to associate with it.
        */
      def onNewIdentifier(identifier: String, translation: Translation) = {
        translations(identifier) = mutable.Map[Language, String]()
        onNewLanguage(identifier, translation)
      }

      /**
        * Called when a new translation in another language has been made available to an already existing
        * identifier.
        * @param identifier The identifier to add the translation to.
        * @param translation Ditto.
        */
      def onNewLanguage(identifier: String, translation: Translation) = {
        val langMap = translations(identifier)
        if (langMap.contains(translation.language)) {
          LogFacility.log(s"Ambiguous translation: lang=${translation.language}; old=${langMap(translation.language)}; new=${translation.translation}\n"+
            s"Using new translation...", "Warning", "langsupport")
        }
        else {
          // I can safely add the translation now, no translation has been added yet
          langMap(translation.language) = translation.translation
        }
      }

      val RememberTranslation(identifier, translation) = rememberTranslation

      if (translations.contains(identifier)) onNewLanguage(identifier, translation)
      else onNewIdentifier(identifier, translation)
    }

    def clearAllTranslations(): Unit = {
      translations.clear()
    }

    override def receive = {
      // Returns String.
      case Translate(id, lang) => sender ! translations(id)(lang)
      // Returns Unit.
      case remember: RememberTranslation =>
        sender ! addTranslation(remember)
      //case clear: LangDict.ClearAllTranslations =>
      //  sender ! clearAllTranslations()
    }
  }

  //</editor-fold>

  private val actor = Main.getActorSystem.actorOf(Props(new TranslationProvider))

  /**
    * Makes it more look like I am accessing properties.
    *
    * This is a feature enabled by the Scala Dynamic trait.
    * @param identifier The identifier. I hope that I don't have to rename this parameter to "method"
    * @param args Arguments.
    * @return Can return anything! Be aware of signature changes, this __is intended to be changed soon.__
    */
  def applyDynamic(identifier: String)(args: Any*): Future[String] = args(0) match {
    case language: Language =>
      implicit val timeout = Timeout(10.seconds)
      (actor ? Translate(identifier, language)).mapTo[String]
    case _ => ???
  }

  /**
    * Gets a translation.
    *
    * In order to extract the translation from the future, you can do it this way:
    *
    * {{{
    *   import scala.concurrent.Await
    *   import scala.concurrent.duration._
    *
    *   // Scala way of obtaining the translation of some identifier in a certain language
    *   val translationFuture = getTranslation("someIdentifier", SomeLanguage)
    *   // ...or, in Scala as well. This line is equal to the line above
    *   val translationFuture = langDict.someIdentifier(SomeLanguage)
    *   // Java way of doing that
    *   scala.concurrent.Future<String> translationFuture = getTranslation("someIdentifier", English$.MODULE$);
    *
    *   // Either this way, if you are using Scala's implicit conversions
    *   val actualTranslation = Await.result(translationFuture, 5.seconds)
    *   // Or this way, if you use "Java-style"
    *   String actualTranslation = Await.result(translationFuture, new FiniteDuration(5, TimeUnit.SECONDS)
    * }}}
    *
    * Time units are not limited to this example: milliseconds, nanoseconds, minutes and days are also valid.
    *
    * @param identifier The identifier.
    * @param language The language from which to get the translation.
    * @return A future with a possible translation.
    */
  def getTranslation(identifier: String, language: Language): Future[String] = applyDynamic(identifier)(language)

  def getTranslationNow(identifier: String, language: Language, maxWaitingTime: Duration): String = Await.result(getTranslation(identifier, language), maxWaitingTime)

  def getTranslationNow(identifier: String, language: Language): String = getTranslationNow(identifier, language, 10.seconds)

  def addTranslation(identifier: String, translation: Translation): Unit = {
    actor ! RememberTranslation(identifier, translation)
  }

  def addJsonTranslations(jsonDocument: JValue): Unit = {

    val extractedActorMessages: Seq[RememberTranslation] = for (
      JObject(langItemSeq) <- jsonDocument;
      JField(langItem, translationsList) <- langItemSeq if !langItem.equals(JsonOwnershipIdentifier);
      JObject(translations) <- translationsList;
      JField(langCode, JString(translation)) <- translations
    ) yield {
        RememberTranslation(langItem, Translation(Language.findLanguage(langCode).get, translation))
      }

    for (message <- extractedActorMessages) actor ! message
  }

  def addJsonTranslations(jsonString: String): Unit = addJsonTranslations(parse(jsonString))

  def addJsonTranslations(uri: URI): Unit = addJsonTranslations(Source.fromURI(uri).mkString)

  def addJsonTranslations(file: java.io.File): Unit = addJsonTranslations(file.toURI)

  /** <code>addJsonTranslationsStr(new File("src/resources/data/language/" + address)</code>;
    * Already begins in the language package, so only use the underlying package and the file name. Apart from the different parameter
    * this is equal to addJsonTranslations<br><br>
    * <i><u>Example</u></i><br>
    * general/CommonStrings.json <br>
    * screen/PreWindowScreen.json
    *
    * @param address the package within language and the file name: e.g. "general/CommonStrings.json"
    */
  def addJsonTranslationsStr(address: String): Unit = addJsonTranslations(new java.io.File("src/resources/data/language/" + address).toURI)

}

/**
  * Provides not only factory methods for LangDict objects, but it will provide some more if the translation
  * situation evolves.
  */
object LangDict {

  /**
    * The JSON identifier under which to find the owner's name.
    */
  val JsonOwnershipIdentifier = "ownership"

  /**
    * The string used to denote that the dictionary's JSON does not provide an "ownership" field.
    */
  val UnidentifiableOwnerString = "unknown"

  /**
    * Constructs a [[general.langsupport.LangDict]] by taking a valid JSON string in.
    * Note that directly after this call returns a new instance of LangDict, that object is not ready for use at once.
    * Especially large JSON strings take more time to add the translations to the LangDict map.
    *
    * Use this method if you want to construct a LangDict object; it's a convenient factory method.
    * @param json Valid JSON with translation data.
    * @return A lang dict object.
    */
  def fromJson(json: String): LangDict = {
    val jsonDocument = parse(json)
    val jsonDocumentOwner = jsonDocument \ JsonOwnershipIdentifier match {
      case JString(owner) => owner
      case _ => UnidentifiableOwnerString
    }

    val dictionary = LangDict.emptyDictionary(jsonDocumentOwner)
    dictionary.addJsonTranslations(jsonDocument)

    dictionary
  }

  def fromJson(uri: URI): LangDict = fromJson(Source.fromURI(uri).mkString)

  def fromJson(file: java.io.File): LangDict = fromJson(file.toURI)

  /** <code>fromJason(new File("src/resources/data/language/" + address)</code>;
    * Already begins in the language package, so only use the underlying package and the file name.<br><br>
    * <i><u>Example</u></i><br>
    * general/CommonStrings.json <br>
    * screen/PreWindowScreen.json
    * 
    * @param address the package within language and the file name: e.g. "general/CommonStrings.json"
    * @return the LangDict
    */
  def fromJsonStr(address: String): LangDict = fromJson(new java.io.File("src/resources/data/language/" + address))

  def emptyDictionary(creator: String) = new LangDict(creator)

  /**
    * A test string that can be used for testing, obviously...
    * @return A valid JSON test string, without those pipe characters at the beginning of each line.
    */
  def testJson =
    """
      |{
      |  "ownership": "Pfeile team",
      |  "singleplayer" : {
      |    "en_EN" : "Singleplayer",
      |    "de_DE" : "Einzelspieler"
      |  },
      |  "multiplayer" : {
      |    "en_EN" : "Multiplayer",
      |    "de_DE" : "Mehrspieler"
      |  }
      |}
    """.stripMargin

  /**
    * Message to the [[general.langsupport.LangDict.TranslationProvider]] actor to
    * spit out the translation in given language to the given identifier.
    * @param identifier The identifier to look for.
    * @param language Ditto.
    */
  private[LangDict] case class Translate(identifier: String, language: Language)

  /**
    * Instructs the [[general.langsupport.LangDict.TranslationProvider]] actor to save
    * the translation provided with this message.
    * @param identifier The identifier to save it to.
    * @param translation The translation itself.
    */
  private[LangDict] case class RememberTranslation(identifier: String, translation: Translation)

  //case object ClearAllTranslations

  class AmbiguousEntryException[A, B](oldKv: (A, B), newKv: (A, B)) extends Exception(s"Ambiguity: old=$oldKv; new=$newKv")

}

/**
  * Class holding translation data.
  * @param language The language in which the translation is in.
  * @param translation The translation itself.
  */
case class Translation(language: Language, translation: String)
