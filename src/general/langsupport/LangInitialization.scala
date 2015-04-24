package general.langsupport

/**
 * Created by jolecaric on 24/04/15.
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
