package gui.screen

import java.awt.{Color, Font, Graphics2D}

import comp.Component.ComponentStatus
import comp.{Component, Label}
import general._
import world.ContextCreator

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 *
 * @author Josip Palavra
 */
object LoadingWorldScreen extends Screen("Loading screen", 222) {

  lazy val getInstance = this

  private lazy val worldCreation = {
    val worldWidth = PfeileContext.worldSizeX()
    val worldHeight = PfeileContext.worldSizeY()
    val creator = new ContextCreator(worldWidth, worldHeight)

    // Every time the stage changes, the label has to be changed as well.
    creator.onStageDone += { stageCompleted => GUI.stageLabel.setText(stageCompleted.stage.stageName) }
    // Return the creator as a property.
    Property.withValidation(creator)
  }

  private lazy val contextCreationFuture = Property[Future[PfeileContext]]()

  onScreenEnter += { () =>
    val creationProcedure: Future[PfeileContext] = worldCreation().createWorld().map(context => {
      Main.setContext(context)
      Future { onLeavingScreen(GameScreen.SCREEN_INDEX) }
      context
    })
    contextCreationFuture set creationProcedure
  }

  override def draw(g: Graphics2D) = {
    super.draw(g)

    GUI.stageLabel.draw(g)
  }

  private[LoadingWorldScreen] object GUI {

    lazy val stageLabel = new Label(20, 20, LoadingWorldScreen, "Begin")
    stageLabel.setStatus(ComponentStatus.NO_MOUSE)
    stageLabel.setFont(new Font(Component.STD_FONT.getFontName, Font.ITALIC, 25))
    stageLabel.setFontColor(new Color(240, 100, 110))
  }
}
