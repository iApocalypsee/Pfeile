package animation;

import java.awt.image.BufferedImage;

/**
 * Created by jolecaric on 10/04/15.
 */
public class StaticImage implements ImageHolder {

    private final BufferedImage image;

    public StaticImage(BufferedImage image) {
        if(image == null) throw new NullPointerException();
        this.image = image;
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }
}
