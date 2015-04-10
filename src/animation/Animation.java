package animation;

import java.awt.image.BufferedImage;

/**
 * Making out of a buffered image and a specified amount of keyframes an animation.
 * @author Josip
 * @version 2/10/14
 */
public class Animation implements ImageHolder {

    private int keyframes;
    private BufferedImage[] animationData;
    private int currentFrame = 0;

    public Animation(BufferedImage srcImg, int framesAmt) {
        // assigning instance vars and creating the image array
        animationData = new BufferedImage[framesAmt];
        keyframes = framesAmt;
        // retrieving some data
        int longwidth = srcImg.getWidth();
        int frame_width = longwidth / framesAmt;
        for (int x = 0, i = 0; x < longwidth; x += frame_width, i++) {
            animationData[i] = srcImg.getSubimage(x, 0, x + frame_width - 1, srcImg.getHeight());
        }
    }

    /**
     * Returns the amount of keyframes.
     * @return The amount of keyframes.
     */
    public int getKeyframes() {
        return keyframes;
    }

    /**
     * Returns the pictures for each frame.
     * @return The pictures for each frame.
     */
    public BufferedImage[] getAnimationData() {
        return animationData;
    }

    /**
     * Returns the frame currently being displayed. The returned keyframe is calculated
     * relatively to time.
     * @return The frame being displayed currently.
     */
    @Override
    public BufferedImage getImage() {
        return animationData[currentFrame];
    }

    public void update() {
        currentFrame++;
    }
}
