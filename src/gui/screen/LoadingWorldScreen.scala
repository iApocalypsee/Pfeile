package gui.screen

import java.awt.event.{KeyEvent, MouseAdapter, MouseEvent}
import java.awt.{Color, Font, Graphics2D}

import animation.ImageLoader
import comp.Component.ComponentStatus
import comp.{Button, Component, Label}
import general._
import world.ContextCreator

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 *
 * @author Josip Palavra
 */
object LoadingWorldScreen extends Screen("Loading screen", 222) {

  lazy val getInstance = this
  /** If the world (and every other stage) is loaded and LoadingWorldScreen is left, this is <code>true</code> */
  private var isLoad = false
  def isLoaded: Boolean = isLoad

  // GO-Button
  val goButton = new Button(Main.getWindowWidth - 150, Main.getWindowHeight - 90, this, "GO...")
  goButton.declineInput()
  goButton.iconify(ImageLoader.load("comp/continueButton.png"))
  goButton.addMouseListener(new MouseAdapter {
    override def mouseReleased(e: MouseEvent): Unit = {
      triggerGoButton()
    }
  })

  override def keyReleased(e: KeyEvent): Unit = {
    super.keyReleased(e)
    if (e.getKeyCode == KeyEvent.VK_G) {
      triggerGoButton()
    }
  }

  private def triggerGoButton (): Unit = {
    isLoad = true
    onLeavingScreen(GameScreen.SCREEN_INDEX)
  }

  private lazy val worldCreation = {
    val worldWidth = PfeileContext.worldSizeX()
    val worldHeight = PfeileContext.worldSizeY()
    val creator = new ContextCreator(worldWidth, worldHeight)

    // FIXME: The stage Label should set the stageName, when it begins, because the user doesn't know when to click at "GO...".
    // Every time the stage changes, the label has to be changed as well.
    creator.onStageDone += { stageCompleted => GUI.stageLabel.setText(stageCompleted.stage.stageName) }
    creator.onLastStageDone += { _ => GUI.stageLabel.setText("Done!") }
    // Return the creator as a property.
    Property.withValidation(creator)
  }

  private lazy val contextCreationFuture = Property[Future[PfeileContext]]()

  onScreenEnter += { () =>
    val creationProcedure: Future[PfeileContext] = worldCreation().createWorld()

    creationProcedure onSuccess {
      case context => Main.setContext(context)
        postLoadingCheck()
        goButton.acceptInput()
        context
    }

    creationProcedure onFailure {
      case e: Exception =>
        val errorString = "Error while creating PfeileContext: " + e
        GUI.stageLabel.setText(errorString)
        LogFacility.log(errorString, "Error")
        e.printStackTrace()
    }

    contextCreationFuture set creationProcedure
  }

  override def draw(g: Graphics2D) = {
    super.draw(g)
    goButton.draw(g)
    GUI.stageLabel.draw(g)
  }

  private def postLoadingCheck(): Unit = {
  }

  private[LoadingWorldScreen] object GUI {

    lazy val stageLabel = new Label(20, 20, LoadingWorldScreen, "Begin")
    stageLabel.setStatus(ComponentStatus.NO_MOUSE)
    stageLabel.setFont(new Font(Component.STD_FONT.getFontName, Font.ITALIC, 25))
    stageLabel.setFontColor(new Color(240, 100, 110))
  }
}
