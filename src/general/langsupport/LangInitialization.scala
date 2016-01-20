package general.langsupport

/**
  * Does language initialization. There is not much to initialize, but I'm sure
  * there will be.
  * @since 24.04.15
  */
object LangInitialization {

  def apply(): Unit = {
    // Just call those singletons so that they are being initialized. Don't
    // do anything with them yet.
    // TODO Find another way to initialize those singletons.
    English
    German
  }

}
