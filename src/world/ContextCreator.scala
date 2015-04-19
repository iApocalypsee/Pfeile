package world

import java.awt.Point

import general.io.StageDescriptable
import general.{Main, PfeileContext, Property, StageOrganized}
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
    val world = instantiateWorld()
    populateWorld(world)
    world
  }

  private def instantiateWorld(): PfeileContext = {
    val instantiator = new Instantiator
    currentStage set instantiator
    instantiator.executeStage()
  }

  private def populateWorld(context: PfeileContext): PfeileContext = {
    val populator = new Populator(context)
    currentStage set populator
    populator.executeStage()
  }

  /** Populates the world.
    *
    * TODO The implementation is a whole mess; I am going through the code in the next time.
    */
  private[ContextCreator] class Populator(val context: PfeileContext) extends StageDescriptable[PfeileContext] {

    override def stageName = "Populating..."

    /** The implementation of the stage. */
    override protected def executeStageImpl() = {
      // TODO This stage does not look nice.
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

      /*
      // gui position of player "act"
      var endComponent = context.getWorld.terrain.tileAt(spawnPoint.x, spawnPoint.y).component
      act.component.setSourceShape(endComponent.getSourceShape)
      act.component.resetPosition()
      act.component.setX(endComponent.getX)
      act.component.setY(endComponent.getY)

      // setting gui position of player "opponent"
      endComponent = context.getWorld.terrain.tileAt(spawnPointEnemy.x, spawnPointEnemy.y).component
      opponent.component.setSourceShape(endComponent.getSourceShape)
      opponent.component.resetPosition()
      opponent.component.setX(endComponent.getX)
      opponent.component.setY(endComponent.getY)
      */

      context
    }
  }

  /** Instantiates the world with its terrain. */
  private[ContextCreator] class Instantiator extends StageDescriptable[PfeileContext] {

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

}
