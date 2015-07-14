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
  private var isLoaded = false
  /** If the world (and every other stage) is loaded and LoadingWorldScreen is left, this is <code>true</code> */
  def hasLoaded: Boolean = isLoaded

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
    if (isLoaded)
      onLeavingScreen(GameScreen.SCREEN_INDEX)
  }

  private lazy val worldCreation = {
    val worldWidth = PfeileContext.worldSizeX()
    val worldHeight = PfeileContext.worldSizeY()
    val creator = new ContextCreator(worldWidth, worldHeight)

    // FIXME: The stage Label should set the stageName, when it begins, because the user doesn't know when to click at "GO...".
    // Every time the stage changes, the label has to be changed as well.
    creator.onStageDone += {
      stageCompleted => GUI.stageLabel.setText(stageCompleted.stage.stageName)
    }
    creator.onLastStageDone += {
      _ => {
        GUI.stageLabel.setText("Done!")
      }
    }
    // Return the creator as a property.
    Property.withValidation(creator)
  }

  private lazy val contextCreationFuture = Property[Future[PfeileContext]]()

  onScreenEnter += { () =>
    val creationProcedure: Future[PfeileContext] = worldCreation().createWorld().map(context => {
      Main.setContext(context)

      // these calls only work, if "Main.getContext()" is available
      // I want them to be ready before entering GameScreen, because at this point, the user still sees "Applying other stuff..."
      // initialize ShopWindow
      GameScreen.getInstance().getShopWindow

      // creates the visualMap; it is used for centering the map later on and creating it before entering GameScreen.
      GameScreen.getInstance().createVisualMap(context)

      // center map
      GameScreen.getInstance().getMap.centerMap(context.getActivePlayer.getGridX, context.getActivePlayer.getGridY)

      postLoadingCheck()
      isLoaded = true
      GUI.stageLabel.setText("Done!")
      goButton.acceptInput()

      context
    })
    contextCreationFuture set creationProcedure
  }

  override def draw(g: Graphics2D) = {
    super.draw(g)
    goButton.draw(g)
    GUI.stageLabel.draw(g)
  }

  private def postLoadingCheck(): Unit = {
    val dataFrom = GameScreen.getInstance().getMoneyDisplay.getData
    assert(dataFrom != null, "Money display data provider is null")
    val moneyString = GameScreen.getInstance().getMoneyDisplay.getMoneyString
    assert(moneyString != "0", "Money string still 0")
    val timeClockString = Main.getContext.getTimeClock.getTimePrintString
    assert(timeClockString != "null", "The current TimeClock TimePrintString is still null")
  }

  private[LoadingWorldScreen] object GUI {

    lazy val stageLabel = new Label(20, 20, LoadingWorldScreen, "Begin")
    stageLabel.setStatus(ComponentStatus.NO_MOUSE)
    stageLabel.setFont(new Font(Component.STD_FONT.getFontName, Font.ITALIC, 25))
    stageLabel.setFontColor(new Color(240, 100, 110))
  }
}
