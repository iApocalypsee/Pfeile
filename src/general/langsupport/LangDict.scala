package general.langsupport

import java.net.URI

import akka.actor._
import akka.pattern.ask
import akka.{Main => AkkaMain}
import general._
import org.json4s.JsonAST.{JField, JString}
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.beans.BeanProperty
import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.io.Source
import scala.language.dynamics
import scala.languageFeature.dynamics

trait LanguageDictionary extends Dynamic {

  /**
    * Makes it more look like I am accessing properties.
    *
    * This is a feature enabled by the Scala Dynamic trait.
    * @param identifier The identifier. I hope that I don't have to rename this parameter to "method"
    * @param args Arguments.
    * @return Can return anything! Be aware of signature changes, this __is intended to be changed soon.__
    */
  def applyDynamic(identifier: String)(args: Any*): Future[String] = args(0) match {
    case language: Language => getTranslation(identifier, language)
    case _ => ???
  }

  /**
    * The intellectual owner of this translation set.
    * @return Should be "Pfeile team", unless some random translator comes across.
    */
  def owner: String
  def getOwner = owner

  /**
    * Retrieves a translation asynchonously from the instance.
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
    * @param identifier The unique identifier for the translation. Identifier interpretation
    *                   may vary based on implementations.
    * @param language The language to retrieve the translation for.
    * @return Async future with a possible translation outcome.
    */
  def getTranslation(identifier: String, language: Language): Future[String]

  /**
    * Gets the translation from the instance.
    * This method behaves just like [[general.langsupport.LanguageDictionary#getTranslation(java.lang.String, general.langsupport.Language)]];
    * however, unlike the `getTranslation` method, this method is blocking the calling thread until the underlying future either
    * completed with a result or threw an exception (which will rethrow in the calling thread upon calling this method).
    * @param identifier The unique identifier for the translation. Identifier interpretation
    *                   may vary based on implementations.
    * @param language The language to retrieve the translation for.
    * @param maxWaitingTime The maximum waiting time until this method finishes with an exception.
    * @return A translation.
    */
  def getTranslationNow(identifier: String, language: Language, maxWaitingTime: Duration) = Await.result(getTranslation(identifier, language), maxWaitingTime)

  /**
    * Like [[general.langsupport.LanguageDictionary#getTranslationNow(java.lang.String, general.langsupport.Language)]], but with
    * 10 seconds set as default maximum waiting time.
    * @param identifier Ditto.
    * @param language Ditto.
    * @return Ditto.
    */
  def getTranslationNow(identifier: String, language: Language): String = getTranslationNow(identifier, language, 10.seconds)

  def addJsonTranslations(jsonDocument: JValue): Unit

  def addJsonTranslations(jsonString: String): Unit = addJsonTranslations(parse(jsonString))

  def addJsonTranslations(uri: URI): Unit = addJsonTranslations(Source.fromURI(uri).mkString)

  def addJsonTranslations(file: java.io.File): Unit = addJsonTranslations(file.toURI)

  def addJsonTranslationsStr(address: String): Unit = addJsonTranslations(new java.io.File("src/resources/data/language/" + address).toURI)

}

/**
  * First implementation of a language dictionary with a list as base organization structure.
  *
  * @param owner The person (or group) to which the underlying data of the language dictionary belongs to.
  *              Use this to avoid collision between multiple authors of translations.
  * @author iApocalypsee
  */
class LangDict private(override val owner: String) extends LanguageDictionary {

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
  def getTranslation(identifier: String, language: Language): Future[String] = actor.ask(Translate(identifier, language))(10.seconds).mapTo[String]

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

}

/**
  * The current implementation of the language dictionary system utilizing a tree organization for
  * the translations.
  * @param owner Ditto.
  */
class LangTreeDict(override val owner: String) extends LanguageDictionary {

  require(owner != null)

  import LangDict._

  class TranslationProvider extends Actor {

    val root = new MutableCategoryNode("")

    override def receive = {
      // Consider this object to be added to the translation provider
      case node: LangNode => root.addNode(node)
      // Consider strings to request a node with given path
      case path: String =>
        if(path == "/") sender ! root
        else sender ! root / path
    }
  }

  private val actor = Main.getActorSystem.actorOf(Props(new TranslationProvider))

  implicit val executionContext = Main.getGlobalExecutionContext

  def getTranslation(path: String, language: Language) = actor.ask(path)(10.seconds).collect { case t: TranslationNode => t }.map(t => t.get(language)).mapTo[String]

  def getNode(path: String) = actor.ask(path)(10.seconds).mapTo[LangNode]

  def getNodeNow(path: String) = Await.result(getNode(path), 10.seconds)

  def addJsonTranslations(jsonDocument: JValue): Unit = {

    def recursiveTraversion(tuple: (String, JValue)): LangNode = {
      val (id, value) = tuple
      // Ignore the ownership part for now
      if(id == "ownership") return InvalidNode("ownership")
      val JObject(list) = value
      val isLanguageKeyword = list.exists(tuple => Language.isAvailable(tuple._1))

      if(isLanguageKeyword) {

        // Must be an actual translation, treat the JSON object as such.
        // JSON object thus required to carry some translations in specified format

        var mapping = mutable.Map.empty[Language, String]

        // Append every translation to 'mapping'
        for((name, JString(translation)) <- list) Language.findLanguage(name).foreach(lang => mapping.+=((lang, translation)))

        if(mapping.nonEmpty) new TranslationNode(id, mapping.toMap)
        // Empty nodes are considered invalid
        else InvalidNode(id)

      } else {
        // Must be a category subnode, return a category subnode with every contained subnode
        val childNodes = list.map(tuple => recursiveTraversion(tuple)).filterNot(node => node.isInstanceOf[InvalidNode])
        if(childNodes.nonEmpty) new CategoryNode(id, childNodes)
        // Empty nodes are considered invalid
        else InvalidNode(id)
      }
    }

    val JObject(langItemSeq) = jsonDocument

    actor ! new CategoryNode("", langItemSeq.map(tuple => recursiveTraversion(tuple)).filterNot(node => node.isInstanceOf[InvalidNode]))

  }

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

  val NodeDelimiter = "/"

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

  def testTreeJson =
    """
      |{
      |  "game-screen": {
      |
      |    "end-turn": {
      |      "de_DE": "Runde beenden",
      |      "en_EN": "End turn"
      |    },
      |
      |    "enter-shop": {
      |      "de_DE": "Betrete Laden",
      |      "en_EN": "Enter shop"
      |    }
      |
      |  }
      |
      |  "example": {
      |    "translation-1": {
      |      "de_DE": "Erste Beispielübersetzung",
      |      "en_EN": "First example translation"
      |    },
      |
      |    "translation-2": {
      |      "de_DE": "Zweite Beispielübersetzung",
      |      "en_EN": "Second example translation"
      |    }
      |  }
      |}
    """.stripMargin

  def testTreeJson2 =
    """
      |{
      |  "game-screen": {
      |    "additionalTranslation": {
      |      "de_DE": "Zusätzliche Übersetzung",
      |      "en_EN": "Additional translation"
      |    }
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

  // <editor-fold desc="Node types">

  /**
    * Base class for the language tree node.
    * @param id The identifier for this node.
    */
  class LangNode(@BeanProperty val id: String) {

    def asCategory = this match {
      case x: CategoryNode => Some(x)
      case _ => None
    }

    def asTranslation = this match {
      case x: TranslationNode => Some(x)
      case _ => None
    }

    def isCategory = this.isInstanceOf[CategoryNode]
    def isTranslation = this.isInstanceOf[TranslationNode]

    def /(subnodeId: String): LangNode = {
      val categoryNode = this.asCategory.getOrElse(throw new UnsupportedOperationException)
      val splitPath = subnodeId.split("/")
      val requestedNode = categoryNode.children.find(_.id == splitPath(0)).getOrElse(throw new NoSuchElementException)

      if(splitPath.length == 1) requestedNode
      else requestedNode / subnodeId.substring(subnodeId.indexOf("/") + 1)
    }

    def subnode(subnodeId: String): LangNode = this / subnodeId

  }

  class CategoryNode(id: String, initChildren: Seq[LangNode]) extends LangNode(id) {

    def children = initChildren

    protected[LangDict] def toMutableNode: MutableCategoryNode = {
      val x = new MutableCategoryNode(id)
      x.merge(this)
      x
    }
  }

  class MutableCategoryNode(id: String) extends CategoryNode(id, Seq.empty) {

    private val m_children = mutable.Buffer.empty[LangNode]

    override def children = m_children.toSeq
    override protected[LangDict] def toMutableNode = this

    def merge(x: CategoryNode): Unit = {
      x.children.foreach(node => this.addNode(node))
    }

    def addNode(x: LangNode): Unit = {
      val possibleCollision = m_children.find(_.id == x.id)

      // Catch eventual naming collisions in different ways depending on the input parameter
      possibleCollision match {

        // A naming collision is present, resolve it and notify the log about potential errors
        case Some(node) => node match {
          // Input parameter colliding with category node
          case category: MutableCategoryNode =>

            x match {
              // Normal case, categories are just overlapping.
              // Merge the two categories together.
              case addCategory: CategoryNode => addCategory.children.foreach(category.addNode)
              // Alert that a category is going to be replaced by a translation.
              case addTranslation: TranslationNode =>
                LogFacility.log(s"Category (id=${category.id}) overridden by translation (id=${addTranslation.id})!", "Warning")
                m_children.remove(m_children.indexOf(category))
                m_children += addTranslation
            }

          // Input parameter colliding with translation node
          case translation: TranslationNode =>

            x match {
              // Alert that a translation is going to be replaced by a category.
              case addCategory: CategoryNode =>
                LogFacility.log(s"Translation (id=${translation.id}) overridden by category (id=${addCategory.id})!", "Warning")
                m_children.remove(m_children.indexOf(translation))
                m_children += addCategory.toMutableNode
              // Alert that existing translation is going to be replaced with new translation.
              case addTranslation: TranslationNode =>
                LogFacility.log(s"Translation (id=${translation.id}) overridden by translation (id=${addTranslation.id})!", "Warning")
                m_children.remove(m_children.indexOf(translation))
                m_children += addTranslation
            }
        }

        // No naming collisions, proceed normally
        case None => x match {
          case category: CategoryNode =>
            if(category.id == "") this.merge(category)
            else m_children += category.toMutableNode
          case translation: TranslationNode => m_children += translation
        }
      }

    }
  }

  case class InvalidNode private[general](initId: String) extends CategoryNode(initId, Seq.empty)

  /**
    * Node representing a translatable word.
    * @param mapping The language mapping for this
    */
  class TranslationNode(id: String, private val mapping: Map[Language, String]) extends LangNode(id) {
    def get(x: Language) = mapping(x)
  }

  // </editor-fold>

}

/**
  * Class holding translation data.
  * @param language The language in which the translation is in.
  * @param translation The translation itself.
  */
case class Translation(language: Language, translation: String)
