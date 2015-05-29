package comp;

import general.Delegate;
import gui.screen.Screen;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Component that represents an ordinary character.
 */
public class CharComponent extends Component {

    private String character;
    private Font font;
    private int baselineDownMove;
    private int baselinePositionY;

    public final Delegate.Function0Delegate onLeftSideClicked = new Delegate.Function0Delegate();
    public final Delegate.Function0Delegate onRightSideClicked = new Delegate.Function0Delegate();

    public CharComponent(int x, int y, char character, Font font, Screen backing) {
        super();
        this.character = String.valueOf(character);
        this.font = font;

        final Dimension charBounds = Component.getTextBounds(this.character, font);
        final Rectangle charRect = new Rectangle(charBounds);
        charRect.translate(-charRect.width / 2, -charRect.height / 2);

        this.baselineDownMove = charBounds.height;
        this.baselinePositionY = this.baselineDownMove + y;

        setSourceShape(charRect);
        setX(x);
        setY(y);
        setBackingScreen(backing);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                routeSideDelegateCall(e);
            }
        });
    }

    private void routeSideDelegateCall(MouseEvent e) {
        final Shape bounds = getBounds();
        final int halfWidth = bounds.getBounds().width / 2;
        if(e.getX() < this.getX() + halfWidth) {
            onLeftSideClicked.apply();
        } else if(e.getX() >= this.getX() + halfWidth && e.getX() < this.getX() + this.getWidth()) {
            onRightSideClicked.apply();
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setFont(font);
        g.drawString(character, getX(), baselinePositionY);
    }
}
