package gui.screen;

import comp.Button;
import comp.WarningMessage;
import general.JavaInterop;
import general.Main;
import gui.FrameContainer;
import gui.FrameContainerObject;
import gui.MoneyDisplay;
import player.shop.ShopCentral;
import player.shop.ShopCentral$;
import player.shop.ShopWindow;
import player.weapon.AttackDrawer;
import player.weapon.arrow.ImpactDrawerHandler;
import scala.runtime.BoxedUnit;
import world.VisualMap;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * <b>4.1.2014 (Josip):</b> Konstruktor braucht keine ScreenManager-Instanz mehr. <br><br>
 * <b>20.1.2014:</b> GameScreen wurde an {@link Screen}-Update 20.1.2014 angepasst.
 * <b>25.1.2014:</b> Singleton pattern an die Subklassen von Screen angewendet -> Einfachere Anwendung
 * von Screen-Klassen. Um auf das GameScreen-Objekt zuzugreifen, muss {@link #getInstance()} aufgerufen
 * werden.
 * 
 * 
 * @version 20.1.2014
 * @see ScreenManager
 */
public class GameScreen extends Screen implements FrameContainer {
	
	private static GameScreen instance = null;
	
	/**
	 * Singleton-method.
	 * @return The object.
	 */
	public static GameScreen getInstance() {
		if(instance == null) {
			instance = new GameScreen();
			instance.postInit();
		}
		return instance;
	}

	public static final String SCREEN_NAME = "Game screen";
	public static final int SCREEN_INDEX = 1;

	private Button endTurnButton;
	private Button shootButton;
	private Button inventoryButton;
    private Button shopWindowButton;

    private WarningMessage message;

    private MoneyDisplay moneyDisplay;

    private ShopWindow shopWindow;

	private VisualMap map = null;
    private final AttackDrawer attackDrawer = new AttackDrawer();

	private final FrameContainerObject frameContainerObject = new FrameContainerObject();

    public AttackDrawer getAttackDrawer () {
        return attackDrawer;
    }

	/**
	 * Die Welt, die vom GameScreen gezeichnet wird.
	 */
	private GameScreen() {
		super(GameScreen.SCREEN_NAME, GameScreen.SCREEN_INDEX);

		onScreenEnter.register(JavaInterop.asScala(() -> {
            if (map == null) {
                map = new VisualMap(Main.getContext().getWorld());
                map.moveMap(120, 470);
            }
            return BoxedUnit.UNIT;
        }));
	}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        super.mouseWheelMoved(e);
        map.zoom(1 + (e.getWheelRotation() * 0.08f));
    }

    /** This method must be called just once!
	 *
	 * With postInit() I am avoiding problems that the GameScreen.getInstance() reference is still null.
	 * I know, complicated to explain, but every time I call GameScreen.getInstance() in the GameScreen constructor,
	 * getInstance() returns null. Why? Because the GameScreen instance has not been fully constructed yet.
	 * So construct it first, then do all the initialization afterwards.
	 */
	private void postInit() {

		// Initialisierung der Buttons
		endTurnButton = new Button(30, Main.getWindowHeight() - 50, this,
				"End turn");
		shootButton = new Button(endTurnButton.getX() + endTurnButton.getWidth() + 20,
                endTurnButton.getY(), this, "Shoot");
		inventoryButton = new Button(shootButton.getX() + shootButton.getWidth() + 20,
				shootButton.getY(), this, "Inventar");
        shopWindowButton = new Button(inventoryButton.getX() + inventoryButton.getWidth() + 20,
                inventoryButton.getY(), this, "Shop");

		endTurnButton.setName("End turn button");
		shootButton.setName("Shoot button");
		inventoryButton.setName("Inventory button");
        shopWindowButton.setName("Shop button");

        message = new WarningMessage("          ", 80, Main.getWindowHeight() - 95, this);
        message.setWarningColor(new Color(221, 49, 77));

        moneyDisplay = new MoneyDisplay(Main.getWindowWidth() - 192, Main.getWindowHeight() - 250, this);
        
		endTurnButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				Main.getContext().turnSystem().increment();
			}

		});

		shootButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				onLeavingScreen(ArrowSelectionScreen.SCREEN_INDEX);
			}
		});

		inventoryButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                onLeavingScreen(InventoryScreen.SCREEN_INDEX);
            }
        });

        shopWindowButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                getShopWindow().parentComponent().setVisible(!shopWindow.parentComponent().isVisible());
            }
        });
	}

    ShopWindow getShopWindow() {
        if(shopWindow == null) {
            shopWindow = new ShopWindow(ShopCentral.articles(), ShopCentral$.MODULE$);
        }
        return shopWindow;
    }

    @Override
	public void draw(Graphics2D g) {
		super.draw(g);
		if(map != null) {
			map.draw(g);
		}
		// I practically do not need the visual entity to let the entities draw on to the screen
		// BUT I still need the component objects, which reside in the visual entity object.
		// Drawing of the entities is done in VisualMap
		//visualEntity.draw(g);
        attackDrawer.draw(g);
        ImpactDrawerHandler.draw(g);
		// Zeichnet die Welt und den UserInterface, der den Player dargestellt
		endTurnButton.draw(g);
		shootButton.draw(g);
		inventoryButton.draw(g);
        shopWindowButton.draw(g);
        moneyDisplay.draw(g);
		Main.getContext().getActivePlayer().drawLifeUI(g);

		frameContainerObject.drawFrames(g);

		Main.getContext().getTimeClock().draw(g);

        message.draw(g);
	}

	@Override
	public void keyDown(KeyEvent e) {
		super.keyDown(e);
		switch(e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				onLeavingScreen(PauseScreen.SCREEN_INDEX);
				break;
			case KeyEvent.VK_S:
				onLeavingScreen(ArrowSelectionScreen.SCREEN_INDEX);
				break;
			case KeyEvent.VK_E:
				Main.getContext().turnSystem().increment();
				break;
			case KeyEvent.VK_PAGE_UP:
				map.zoom(1.05f);
				break;
			case KeyEvent.VK_PAGE_DOWN:
				map.zoom(0.95f);
				break;
            case KeyEvent.VK_I:
                onLeavingScreen(InventoryScreen.SCREEN_INDEX);
                break;

            // FIXME: remove this later
            case KeyEvent.VK_SPACE:
                Main.getContext().getActivePlayer().life().changeLife(-20);
		}
	}

    public void lockUI() {
		endTurnButton.declineInput();
		shootButton.declineInput();
		inventoryButton.declineInput();
        message.declineInput();
	}
	
	public void releaseUI() {
		endTurnButton.acceptInput();
		shootButton.acceptInput();
		inventoryButton.acceptInput();
        message.acceptInput();
	}

    /** Sets the WarningMessage printed on the screen. <p>
     * Use <code>GameScreen.getInstance().activateWarningMessage()</code> to enable the message.
     *
     * @param message the message, the user has to now
     */
    public void setWarningMessage (String message) {
        this.message.setMessage(message);
    }

    /**
     * Activates the message, so that it is printed on the screen. The message can be set with
     * <code>GameScreen.getInstance().setWarningMessage("bla bla bla...");</code>
     */
    public void activateWarningMessage () {
        message.activateMessage();
    }

	public VisualMap getMap() {
		return map;
	}

    /**
     * @return the small box, which shows the current money of the player.
     */
    MoneyDisplay getMoneyDisplay () {
        return moneyDisplay;
    }

	/**
	 * The object managing the internal frames for the screen.
	 */
	@Override
	public FrameContainerObject frameContainer() {
		return frameContainerObject;
	}

	@Override
	public FrameContainerObject getFrameContainer() {
		return frameContainer();
	}
}
