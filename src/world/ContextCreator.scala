package world

import java.awt.Point

import general._
import general.io.StageDescriptable
import gui.screen._
import misc.ItemInitialization
import newent.Player
import player.item.ore.{CopperOre, IronOre, OreRegistry}
import player.shop.ShopInitializer
import world.brush.OreBrush

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Random

class ContextCreator(val worldSizeX: Int, val worldSizeY: Int) extends StageOrganized {

  private var context: PfeileContext = _

  addStage(new InstantiatorStage)
  addStage(new PopulatorStage)
  addStage(new OreGenerationStage)
  addStage(new LootTraderGenerationStage)
  addStage(new OtherStuffStage)

  def createWorld(): Future[PfeileContext] = Future {
    execute()
    context
  }

  /**
    * Instantiates the world with terrain.
    */
  private[ContextCreator] class InstantiatorStage extends StageDescriptable[Unit] {

    override def stageName: String = Main.tr("creationStage")

    override protected def executeStageImpl(): Unit = {
      val context = new PfeileContext(new PfeileContext.Values)
      val world = new World(worldSizeX, worldSizeY)
      context.world = world
      world.generateDefault()
      ContextCreator.this.context = context
    }

  }

  /**
    * Populates the world.
    *
    * TODO The implementation is a whole mess; I am going through the code in the next time.
    */
  private[ContextCreator] class PopulatorStage extends StageDescriptable[Unit] {

    override def stageName = Main.tr("populationStage")

    /** The implementation of the stage. */
    override protected def executeStageImpl(): Unit = {
      // TODO This stage does not look nice. Very imperative and ugly.
      var spawnPoint: Point = null
      var spawnPointOpponent: Point = null
      val randomGen: Random = new Random
      var isSpawnValid: Boolean = false
      val terrain = context.world.terrain

      while (!isSpawnValid) {
        var tile: Tile = terrain.tileAt(randomGen.nextInt(terrain.width), randomGen.nextInt(terrain.height))
          .asInstanceOf[Tile]
        if (spawnPoint == null && tile.isInstanceOf[GrassTile]) {
          spawnPoint = new Point(tile.getGridX, tile.getGridY)
        }
        tile = terrain.tileAt(randomGen.nextInt(terrain.width), randomGen.nextInt(terrain.height))
          .asInstanceOf[Tile]
        if (spawnPoint != null && tile.isInstanceOf[GrassTile]) {
          if ((spawnPoint.x > tile.getGridX + 2 || spawnPoint.x < tile.getGridX - 2) && (spawnPoint.y > tile.getGridY
            + 2 || spawnPoint.y < tile.getGridY - 2)) {
            spawnPointOpponent = new Point(tile.getGridX, tile.getGridY)
            isSpawnValid = true
          }
        }
      }

      val act = new Player(context.world, spawnPoint, Main.getUser.getUsername)
      val opponent = new Player(context.world, spawnPointOpponent, "Opponent")

      act.onTurnGet += { () => context.activePlayer = act }
      opponent.onTurnGet += { () => context.activePlayer = opponent }

      context.setActivePlayer(act)

      val entityManager = context.world.entities
      entityManager.definePlayer(act)
      entityManager.defineOpponent(opponent)

      act.onTurnEnded += { () => act.resetFortuneStat() }
      opponent.onTurnEnded += { () => opponent.resetFortuneStat() }

      PreWindowScreen.correctArrowNumber(entityManager.getEntityList)

      // adding Arrows:
      LoadingWorldScreen.getInstance.getAddingArrowList(0).foreach((selectedArrow) => {
        if (!selectedArrow.equip(act))
          LogFacility.log("Cannot add " + selectedArrow + " at " + LogFacility.getCurrentMethodLocation, LogFacility.LoggingLevel.Error)
      })
      LoadingWorldScreen.getInstance.getAddingArrowList(1).foreach((selectedArrow) => {
        if (!selectedArrow.equip(opponent))
          LogFacility.log("Cannot add " + selectedArrow + " at " + LogFacility.getCurrentMethodLocation, LogFacility.LoggingLevel.Error)
      })
    }
  }

  /**
   * Generates all ore for the world.
   */
  private[ContextCreator] class OreGenerationStage extends StageDescriptable[Unit] {

    // Async ore registry loading.
    val initOreRegistry = Future {
      OreRegistry.add(new OreRegistry.RegistryEntry(classOf[IronOre], IronOre.SpawnCondition))
      OreRegistry.add(new OreRegistry.RegistryEntry(classOf[CopperOre], CopperOre.SpawnCondition))
    }

    val generateOreAmount = Random.nextInt(20) + 30
    val maximumRadius = 5
    val minimumRadius = 3

    override protected def executeStageImpl(): Unit = {

      // Checking for future completion...
      Await.result(initOreRegistry, 3.seconds)

      for(i <- 0 until generateOreAmount) {
        val brush = new OreBrush
        brush.appliedOre = OreRegistry.randomOre
        brush.radius = Random.nextInt(maximumRadius - minimumRadius) + minimumRadius
        brush.applyBrush(context.world.terrain, Random.nextInt(context.world.terrain.width), Random.nextInt(context.world.terrain.height))
      }
    }

    override def stageName: String = Main.tr("oreGenerationStage")
  }

  private[ContextCreator] class LootTraderGenerationStage extends StageDescriptable[Unit] {

    override protected def executeStageImpl(): Unit = {

      // the loot gets initialized before WorldLootList (--> LootSpawner) uses them. The Main-Thread should not need
      // to wait for the images to load.
      ItemInitialization.initializeLoots()

      // initialization of the wandering traders and spawning them can be made parallel, there're any dependencies.
      val traderGenerator = new Thread(() => {
        context.getWanderingTraders.spawnInitialTraders()
        LogFacility.log("Wandering traders initialized", LogFacility.LoggingLevel.Info)
      }, "WanderingTrader Initializer&Spawner Thread")
      traderGenerator.setPriority(Thread.MAX_PRIORITY)
      traderGenerator.start()

      // Finally, I need to ensure that WorldLootList and LootSpawner are initialized to register their methods.
      // (scala lazy val WorldLootList). Furthermore, some loots have to spawn at the beginning.
      context.getWorldLootList.getLootSpawner.spawnAtBeginning()

      // make sure, that all traders have spawned.
      // join() is important in order to avoid a seldom ConcurrentModificationException in WanderingTraderList.
      // updateVisibleWanderingTraders(), draw() and register() and TurnSystem can collide, when lots of traders are generated.
      traderGenerator.join()
    }

    override def stageName: String = Main.tr("lootGenerationStage")
  }

  private[ContextCreator] class OtherStuffStage extends StageDescriptable[Unit] {

    override protected def executeStageImpl(): Unit = {
      val threadExecute: Thread = new Thread(() => {
        context.getTurnSystem.headOfCommandTeams.foreach(player => {
          player.tightenComponentToTile(player.tileLocation)
        })

        ShopInitializer.initalizeShop()

        context.turnSystem.onTurnEnded.register("turn end screen change") { team =>
          Main.getGameWindow.getScreenManager.requestScreenChange(WaitingScreen.SCREEN_INDEX)
        }

        // initialize MoneyDisplay --> it will actualize it's string, if the money of a entity has been changed.
        GameScreen.getInstance().getMoneyDisplay.initializeDataActualization(context)

      }, "OtherStuffExecuterThread")
      threadExecute.setDaemon(true)
      threadExecute.start()

      // This initialization may take long --> Threaded internally.
      ArrowSelectionScreen.getInstance().init(context)

      // initialize TimeClock
      context.getTimeClock

      // make sure everything is loaded correctly before the first turn begins
      threadExecute.join()

      notifyAboutFirstTurn()
    }

    private def notifyAboutFirstTurn(): Unit = {
      context.turnSystem.onTurnGet(context.turnSystem.currentTeam)
    }

    override def stageName: String = Main.tr("fineTuningStage")
  }

}
