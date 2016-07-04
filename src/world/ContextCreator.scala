package world

import java.awt.Point

import general._
import general.io.StageDescriptable
import general.property.IntStaticProperty
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

class ContextCreator(initWidth: Int, initHeight: Int) extends StageOrganized {

  lazy val sizeX = new IntStaticProperty(initWidth)
  lazy val sizeY = new IntStaticProperty(initHeight)

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
    override def stageName = Main.tr("creationStage")

    /** The implementation of the stage. */
    override protected def executeStageImpl() = {
      val context = new PfeileContext(new PfeileContext.Values)
      val world = new World(sizeX(), sizeY())
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

    override def stageName = Main.tr("populationStage")

    /** The implementation of the stage. */
    override protected def executeStageImpl() = {
      // TODO This stage does not look nice. Very imperative and ugly.
      var spawnPoint: Point = null
      var spawnPointEnemy: Point = null
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
            spawnPointEnemy = new Point(tile.getGridX, tile.getGridY)
            isSpawnValid = true
          }
        }
      }

      val act = new Player(context.world, spawnPoint, Main.getUser.getUsername)
      val opponent = new Player(context.world, spawnPointEnemy, "Opponent")

      act.onTurnGet += { () => context.activePlayer = act }
      opponent.onTurnGet += { () => context.activePlayer = opponent }

      context.setActivePlayer(act)

      val entityManager = context.world.entities
      entityManager += act
      entityManager += opponent

      act.onTurnEnded += { () => act.resetFortuneStat() }
      opponent.onTurnEnded += { () => opponent.resetFortuneStat() }

      PreWindowScreen.correctArrowNumber(entityManager.javaEntityList)

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

    override def stageName = Main.tr("oreGenerationStage")
  }

  private[ContextCreator] class LootGenerationStage extends StageDescriptable[Unit] {

    override protected def executeStageImpl(): Unit = {

      // the loot gets initialized before WorldLootList (--> LootSpawner) uses them. The Main-Thread should not need
      // to wait for the images to load.
      ItemInitialization.initializeLoots()

      // Finally, I need to ensure that WorldLootList and LootSpawner are initialized to register their methods.
      // (scala lazy val WorldLootList). Furthermore, some loots have to spawn at the beginning.
      context.getWorldLootList.getLootSpawner.spawnAtBeginning()
    }

    override def stageName = Main.tr("lootGenerationStage")
  }

  private[ContextCreator] class OtherStuffStage extends StageDescriptable[Unit] {

    override protected def executeStageImpl() = {
      // This initialization may take long...
      ArrowSelectionScreen.getInstance().init(context)

      def debugVarTrace = Map(
        "context" -> context,
        "turnSystem" -> context.turnSystem,
        "turnSystem.teams" -> context.turnSystem.teams(),
        "turnSystem.currentTeam" -> context.turnSystem.currentTeam,
        "turnSystem.onTurnGet" -> context.turnSystem.onTurnGet
      )

      // Trying to debug an error which says that no player gets the first turn...
      assert(context.turnSystem.headOfCommandTeams.nonEmpty, ScalaUtil.errorMessage("Assertion fail for TurnSystem", debugVarTrace))

      // Logging instruction for additional security
      LogFacility.log(ScalaUtil.infoMessage(debugVarTrace))

      context.getTurnSystem.headOfCommandTeams.foreach(player => {
        player.tightenComponentToTile(player.tileLocation)
      })

      ShopInitializer.initalizeShop()

      context.turnSystem.onTurnEnded.register("turn end screen change") { team =>
        Main.getGameWindow.getScreenManager.setActiveScreen(AttackingScreen.SCREEN_INDEX)
      }

      // initialize MoneyDisplay --> it will actualize it's string, if the money of a entity has been changed.
      GameScreen.getInstance().getMoneyDisplay.initializeDataActualization(context)

      // initialize TimeClock
      context.getTimeClock

      notifyAboutFirstTurn()
    }

    private def notifyAboutFirstTurn(): Unit = {
      context.turnSystem.onTurnGet(context.turnSystem.currentTeam)
    }

    override def stageName = Main.tr("fineTuningStage")
  }

}
