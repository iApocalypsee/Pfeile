package world

import java.awt.Point

import general.io.StageDescriptable
import general.{Main, PfeileContext, Property, StageOrganized}
import gui.screen.{WaitingScreen, ArrowSelectionScreen}
import misc.ItemInitialization
import newent.Player

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

/**
 *
 * @author Josip Palavra
 */
class ContextCreator(initWidth: Int, initHeight: Int) extends StageOrganized {
  
  lazy val sizeX = Property(initWidth)
  lazy val sizeY = Property(initHeight)
  
  def createWorld(): Future[PfeileContext] = Future {
    val world = execute(new InstantiatorStage)
    execute(new PopulatorStage(world))
    execute(new OtherStuffStage(world))
    world
  }

  /** Populates the world.
    *
    * TODO The implementation is a whole mess; I am going through the code in the next time.
    */
  private[ContextCreator] class PopulatorStage(val context: PfeileContext) extends StageDescriptable[Unit] {

    override def stageName = "Populating..."

    /** The implementation of the stage. */
    override protected def executeStageImpl() = {
      // TODO This stage does not look nice. Very imperative and ugly.
      var spawnPoint: Point = null
      var spawnPointEnemy: Point = null
      val randomGen: Random = new Random
      var isSpawnValid: Boolean = false
      val terrain = context.world.terrain


      do {
        var tile: TileLike = terrain.tileAt(randomGen.nextInt(terrain.width), randomGen.nextInt(terrain.height))
          .asInstanceOf[TileLike]
        if (spawnPoint == null && tile.isInstanceOf[GrassTile]) {
          spawnPoint = new Point(tile.latticeX, tile.latticeY)
        }
        tile = terrain.tileAt(randomGen.nextInt(terrain.width), randomGen.nextInt(terrain.height))
          .asInstanceOf[TileLike]
        if (spawnPoint != null && tile.isInstanceOf[GrassTile]) {
          if ((spawnPoint.x > tile.latticeX + 2 || spawnPoint.x < tile.latticeX - 2) && (spawnPoint.y > tile.latticeY
            + 2 || spawnPoint.y < tile.latticeY - 2)) {
            spawnPointEnemy = new Point(tile.latticeX, tile.latticeY)
            isSpawnValid = true
          }
        }
      } while (!isSpawnValid)

      val act = new Player(context.world, spawnPoint, Main.getUser.getUsername)
      val opponent = new Player(context.world, spawnPointEnemy, "Opponent")

      act.onTurnGet += { () => context.activePlayer = act }
      opponent.onTurnGet += { () => context.activePlayer = opponent }

      context.setActivePlayer(act)

      context.world.entities += act
      context.world.entities += opponent
    }
  }

  /** Instantiates the world with its terrain. */
  private[ContextCreator] class InstantiatorStage extends StageDescriptable[PfeileContext] {

    /** The name of the stage. */
    override def stageName = "Creating world..."

    /** The implementation of the stage. */
    override protected def executeStageImpl() = {
      val context = new PfeileContext(new PfeileContext.Values)
      val world = new DefaultWorld {
        /** The terrain that describes the geography of the world. */
        override lazy val terrain = new DefaultTerrain(this, sizeX(), sizeY())
      }
      context.world = world
      context
    }
  }
  
  private[ContextCreator] class OtherStuffStage(val context: PfeileContext) extends StageDescriptable[Unit] {
    /** The implementation of the stage. */
    override protected def executeStageImpl() = {
      // the loot gets initialized before WorldLootList (--> LootSpawner) uses them. The Main-Thread should not need
      // to wait for the images to load.
      ItemInitialization.initializeLoots()

      ArrowSelectionScreen.getInstance().init()

      // Finally, I need to ensure that WorldLootList and LootSpawner are initialized to register their methods.
      // (scala lazy val WorldLootList). Furthermore, it's save, that the activePlayer can see loots around him.
      context.getWorldLootList.updateVisibleLoot()

      context.getTurnSystem.onTurnEnded.register(team => {
        Main.getGameWindow.getScreenManager.setActiveScreen(WaitingScreen.SCREEN_INDEX)
      })
    }

    /** The name of the stage. */
    override def stageName = "Applying other stuff..."
  }

}
