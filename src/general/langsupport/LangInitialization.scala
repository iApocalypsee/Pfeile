package general.langsupport

/**
  * Does language initialization. There is not much to initialize, but I'm sure
  * there will be.
 *
  * @since 24.04.15
  */
object LangInitialization {

  /** Returns a new LangDict with the loaded JSON directories */
  def loadLanguageFiles(): LangDict = {
    return new LangDict("general/CommonStrings.json").
                addJSON("general/EverythingElse.json").
                addJSON("general/GameMeta.json").
                addJSON("general/Messages.json").
                addJSON("item/Arrows.json").
                addJSON("item/Items.json").
                addJSON("rest/WorldStrings.json").
                addJSON("screen/ArrowSelectionScreen.json").
                addJSON("screen/ArrowSelectionScreenPreSet.json").
                addJSON("screen/GameScreen.json").
                addJSON("screen/LoadScreen.json").
                addJSON("screen/PreWindowScreen.json").
                addJSON("screen/WaitingScreen.json")
  }

  def apply(): Unit = {
    // Just call those singletons so that they are being initialized. Don't
    // do anything with them yet.
    English
    German
  }

}
