package world

import java.awt.Point

import general._
import general.io.StageDescriptable
import gui.screen._
import misc.ItemInitialization
import newent.Player
import player.item.ore.{CopperOre, IronOre, OreRegistry}
import player.shop.ShopInitializer
import player.weapon.arrow.ArrowHelper
import world.brush.OreBrush

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

class ContextCreator(initWidth: Int, initHeight: Int) extends StageOrganized {

  lazy val sizeX = Property(initWidth)
  lazy val sizeY = Property(initHeight)

  private var context: PfeileContext = null

  addStage(new InstantiatorStage)
  addStage(new PopulatorStage)
  addStage(new OreGenerationStage)
  addStage(new LootGenerationStage)
  addStage(new OtherStuffStage)

  def createWorld(): Future[PfeileContext] = Future {
    execute()
    context
  }

  /** Instantiates the world with its terrain. */
  private[ContextCreator] class InstantiatorStage extends StageDescriptable[Unit] {

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
      ContextCreator.this.context = context
    }

  }

  /**
    * Populates the world.
    *
    * TODO The implementation is a whole mess; I am going through the code in the next time.
    */
  private[ContextCreator] class PopulatorStage extends StageDescriptable[Unit] {

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

      val entityManager = context.world.entities
      entityManager += act
      entityManager += opponent

      PreWindowScreen.correctArrowNumber(entityManager.javaEntityList)

      // adding Arrows:
      LoadingWorldScreen.getInstance.getAddingArrowList(0).forEach(JavaInterop.asJava((selectedArrow) => {
        if (!ArrowHelper.instanceArrow(selectedArrow).equip(act))
          LogFacility.log("Cannot add " + selectedArrow + " at " + LogFacility.getCurrentMethodLocation, LogFacility.LoggingLevel.Error)
      }))
      LoadingWorldScreen.getInstance.getAddingArrowList(1).forEach(JavaInterop.asJava((selectedArrow) => {
        if (!ArrowHelper.instanceArrow(selectedArrow).equip(opponent))
          LogFacility.log("Cannot add " + selectedArrow + " at " + LogFacility.getCurrentMethodLocation, LogFacility.LoggingLevel.Error)
      }))
    }
  }

  /**
   * Generates all ore for the world.
   */
  private[ContextCreator] class OreGenerationStage extends StageDescriptable[Unit] {
    // initialize the ore, while the other stages are still loading..
    initializeOreRegistry()
    var isRegistryLoaded = false

    val generateOreAmount = Random.nextInt(20) + 30
    val maximumRadius = 5
    val minimumRadius = 3

    /** The implementation of the stage. */
    override protected def executeStageImpl(): Unit = {

      // You may improve that later... Futures???
      while (!isRegistryLoaded) {
        LogFacility.log("The Registry hasn't been loaded fast enough!", "Warning")
        Thread.sleep(3)
      }

      for(i <- 0 until generateOreAmount) {
        val brush = new OreBrush
        brush.appliedOre = OreRegistry.randomOre
        brush.radius = Random.nextInt(maximumRadius - minimumRadius) + minimumRadius
        brush.applyBrush(context.world.terrain, Random.nextInt(context.world.terrain.width), Random.nextInt(context.world.terrain.height))
      }
    }

    /** adds to OreRegistry every RegistryEntry and its spawnConditions... */
    def initializeOreRegistry() = {
      val oreRegistryInitializer: Thread = new Thread (new Runnable {
        override def run(): Unit = {
          OreRegistry.add(new OreRegistry.RegistryEntry(classOf[IronOre], IronOre.SpawnCondition))
          OreRegistry.add(new OreRegistry.RegistryEntry(classOf[CopperOre], CopperOre.SpawnCondition))
          isRegistryLoaded = true
        }
      }, "OreRegistry Initializer")
      oreRegistryInitializer.setDaemon(true)
      oreRegistryInitializer.start()
    }

    /** The name of the stage. */
    override def stageName: String = "Generating ores..."
  }

  private[ContextCreator] class LootGenerationStage extends StageDescriptable[Unit] {

    /** The implementation of the stage. */
    override protected def executeStageImpl(): Unit = {

      // the loot gets initialized before WorldLootList (--> LootSpawner) uses them. The Main-Thread should not need
      // to wait for the images to load.
      ItemInitialization.initializeLoots()


      // Finally, I need to ensure that WorldLootList and LootSpawner are initialized to register their methods.
      // (scala lazy val WorldLootList). Furthermore, some loots have to spawn at the beginning.
      context.getWorldLootList.getLootSpawner.spawnAtBeginning()
    }


    /** The name of the stage. */
    override def stageName: String = "Generating loots..."
  }

  private[ContextCreator] class OtherStuffStage extends StageDescriptable[Unit] {
    /** The implementation of the stage. */
    override protected def executeStageImpl() = {
      // This initialization may take long...
      ArrowSelectionScreen.getInstance().init(context)

      ShopInitializer.initalizeShop()

      context.turnSystem.onTurnEnded.register(team => {
        Main.getGameWindow.getScreenManager.setActiveScreen(WaitingScreen.SCREEN_INDEX)
      })

      // initialize MoneyDisplay --> it will actualize it's string, if the money of a entity has been changed.
      GameScreen.getInstance().getMoneyDisplay.initializeDataActualization(context)

      // initialize TimeClock
      context.getTimeClock

      notifyAboutFirstTurn()
    }

    private def notifyAboutFirstTurn(): Unit = {
      context.turnSystem.onTurnGet(context.turnSystem.currentTeam)
    }

    /** The name of the stage. */
    override def stageName = "Applying other stuff..."
  }

}
