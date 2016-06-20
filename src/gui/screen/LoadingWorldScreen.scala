package gui.screen

import java.awt.event.{KeyEvent, MouseAdapter, MouseEvent}
import java.awt.{Color, Font, Graphics2D}

import animation.ImageLoader
import comp.Component.ComponentStatus
import comp.{Button, Component, Label}
import general._
import player.weapon.arrow.AbstractArrow
import world.ContextCreator

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 *
 * @author Josip Palavra
 */
object LoadingWorldScreen extends Screen("LoadingScreen", 222) {

  lazy val getInstance = this

  /** If the world (and every other stage) is loaded and LoadingWorldScreen is left, this is <code>true</code> */
  private var isLoaded = false
  /** If the world (and every other stage) is loaded and LoadingWorldScreen is left, this is <code>true</code> */
  def hasLoaded: Boolean = isLoaded

  // GO-Button
  val goButton = new Button(Main.getWindowWidth - 150, Main.getWindowHeight - 90, ImageLoader.load("comp/continueButton.png"), this, "GO...")
  goButton.declineInput()
  goButton.addMouseListener(new MouseAdapter {
    override def mouseReleased(e: MouseEvent): Unit = {
      triggerGoButton()
    }
  })

  private val addingArrowList = new Array[Seq[AbstractArrow]](2)
  addingArrowList(0) = Seq()
  addingArrowList(1) = Seq()

  /** The key is the name of the player: either <code>Main.getUser().getUsername()</code> or "Opponent". */
  def setAddingArrowList (key: String, selectedArrows: java.util.List[AbstractArrow]) = {
    if (key.equals(Main.getUser.getUsername)) {
      addingArrowList(0) = selectedArrows.asScala.toSeq
    } else if (key.equals("Opponent")) {
      addingArrowList(1) = selectedArrows.asScala.toSeq
    } else {
      throw new IllegalArgumentException("Unknown player name/key " + key + ": Must be either " + Main.getUser.getUsername + " or " + "Opponent.")
    }
  }

  /**
   * At position 0: Main.getUser().getUsername()
   * <n>At position 1: "Opponent" </n>
   *
   * @return the list the selected arrows
   */
  def getAddingArrowList = addingArrowList


  override def keyReleased(e: KeyEvent): Unit = {
    super.keyReleased(e)
    if (e.getKeyCode == KeyEvent.VK_G) {
      triggerGoButton()
    }
  }

  private def triggerGoButton (): Unit = {
    if (isLoaded)
      onLeavingScreen(GameScreen.SCREEN_INDEX)
  }

  private lazy val worldCreation = {
    val worldWidth = PfeileContext.worldSizeX()
    val worldHeight = PfeileContext.worldSizeY()
    val creator = new ContextCreator(worldWidth, worldHeight)

    // Every time the stage changes, the label has to be changed as well.
    creator.onStageDone += { stageCompleted => 
      GUI.stageLabel.setText(stageCompleted.stage.stageName)
    }
    creator.onLastStageDone += { _ =>
      if (Main.isEnglish)
        GUI.stageLabel.setText("Done!")
      else
        GUI.stageLabel.setText("Fertig!")
    }
    // Return the creator as a property.
    Property.withValidation(creator)
  }

  private lazy val contextCreationFuture = Property[Future[PfeileContext]]()

  onScreenEnter += { () =>
    val creationProcedure: Future[PfeileContext] = worldCreation().createWorld()

    creationProcedure onSuccess {
      case context => 
        
        Main.setContext(context)

        // creates the visualMap; it is used for centering the map later on and creating it before entering GameScreen.
        GameScreen.getInstance().createVisualMap(context)

        // center map
        GameScreen.getInstance().getMap.centerMap(context.getActivePlayer.getGridX, context.getActivePlayer.getGridY)
        
        postLoadingCheck()
        isLoaded = true
        LogFacility.log("Game begins now...", "Info")
        LogFacility.putSeparationLine()

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

  onScreenLeft.register(event => {
    Main.getContext.onStartRunningTimeClock.apply()
  })

  override def draw(g: Graphics2D) = {
    super.draw(g)
    goButton.draw(g)
    GUI.stageLabel.draw(g)
  }

  private def postLoadingCheck(): Unit = {
    val timeClockString = Main.getContext.getTimeClock.getTimePrintString
    assert(timeClockString != "null", "The current TimeClock TimePrintString is still null")
  }

  private[LoadingWorldScreen] object GUI {

    lazy val stageLabel = new Label(20, 20, LoadingWorldScreen, "Begin!")
    if (!Main.isEnglish)
      stageLabel.setText("Starte!")
    stageLabel.setStatus(ComponentStatus.NO_MOUSE)
    stageLabel.setFont(new Font(Component.STD_FONT.getFontName, Font.ITALIC, 25))
    stageLabel.setFontColor(new Color(240, 100, 110))
  }
}
