package gui.screen

import java.awt.{Font, Color, Graphics2D}

import comp.Component.ComponentStatus
import comp.{Component, Label}
import general.{Main, PfeileContext, Property}
import misc.ItemInitialization
import world.ContextCreator

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
 *
 * @author Josip Palavra
 */
object LoadingWorldScreen extends Screen("Loading screen", 222) {

  lazy val getInstance = this

  private lazy val worldCreation = {
    val worldWidth = PfeileContext.WORLD_SIZE_X()
    val worldHeight = PfeileContext.WORLD_SIZE_Y()
    val creator = new ContextCreator(worldWidth, worldHeight)

    // Every time the stage changes, the label has to be changed as well.
    creator.currentStage.onSet += { _.newVal.map { stage => GUI.stageLabel.setText(stage.stageName) } }
    // Return the creator as a property.
    Property.withValidation(creator)
  }

  private lazy val contextCreationFuture = Property[Future[PfeileContext]]()

  onScreenEnter += { () =>
    val creationProcedure: Future[PfeileContext] = worldCreation().createWorld() andThen {
      // if the world has been computed successfully, change to the GameScreen immediately
      case s: Success[PfeileContext] =>
        // Hand the newly created world to the PfeileContext object.
        Main.setContext(s.get)

        // the loots gets initialized before WorldLootList (--> LootSpawner) uses them. The Main-Thread should not need
        // to wait for loading images.
        ItemInitialization.initializeLoots()

        ArrowSelectionScreen.getInstance().init()

        // Finally, I need to ensure that WorldLootList and LootSpawner are initialized to register their methods.
        // (scala lazy val WorldLootList). Furthermore, it's save, that the activePlayer can see loots around him.
        Main.getContext.getWorldLootList.updateVisibleLoot()

        // Switch forward to the game screen immediately. The world has been generated and
        // populated now.
        onLeavingScreen(GameScreen.SCREEN_INDEX)
      // if an exception has been thrown in the world creation thread, rethrow it in the main thread
      case f: Failure[_] => throw f.exception
    }
    contextCreationFuture() = creationProcedure
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
