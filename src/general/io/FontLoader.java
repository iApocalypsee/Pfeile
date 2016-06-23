package general.io;

import general.LogFacility;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class loads the additional Fonts saved in <code>resources/data/fonts</code> and installs them to the system.
 * Only free fonts for private as well as commercial use are embedded. All embedded fonts need to end either with
 * <code>.ttf</code> or <code>.otf</codee> (use the enum {@link general.io.FontLoader.FontType}).
 */
public class FontLoader {

    /** The format of the font: either TrueType or OpenType. */
    public enum FontType {
        /** The font file is ending with <code>.ttf</code> */
        TTF,
        /** font file is ending with <code>.otf</code> */
        OTF
    }

    /** Returns a new Font using the fontName (= file path) and the size. If the name of the fontFamily doesn't exit, this font is registered.
     * If there has been an exception at loading the file, the {@link comp.Component#STD_FONT} is returned with the specified size.
     *
     * @param fontName the String where the font is saved: use for example: "anyFont".
     *            It's used to create a <code>new Font("resources\\data\\fonts\\" + fontName + fontType)</code>.
     * @param size the size of the loaded font
     * @param fontType the type of the font (and ending of the file): FontType.TTF (for .ttf) or FontType.OTF (for .otf).
     *                 Use the enum {@link general.io.FontLoader.FontType}.
     * @return a new created and registered Font from the .ttf/.otf document, if the font isn't installed or
     *              a new Font with the specified parameter (and Font.PLAIN).
     */
    public static Font loadFont (String fontName, float size, FontType fontType) {
        // if the font is already installed, it doesn't need to be registered/created
        if (comp.Component.isFontInstalled(fontName))
            return new Font(fontName, Font.PLAIN, (int) size);

        Font customFont;
        InputStream inputStream = null;
        try {
            if (fontType == FontType.TTF)
                inputStream = FontLoader.class.getClassLoader().getResourceAsStream("resources/data/fonts/" + fontName + ".ttf");
            else
                inputStream = FontLoader.class.getClassLoader().getResourceAsStream("resources/data/fonts/" + fontName + ".otf");

            if (inputStream == null) {
                if (fontType == FontType.TTF)
                    LogFacility.log("Cannot find resource at: " + "resources/data/fonts/" + fontName + ".ttf", LogFacility.LoggingLevel.Error);
                else
                    LogFacility.log("Cannot find resource at: " + "resources/data/fonts/" + fontName + ".otf", LogFacility.LoggingLevel.Error);
            }

            //create the font to use. Specify the size!
            assert inputStream != null;
            if (fontType == FontType.TTF)
                customFont = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(size);
            else
                customFont = Font.createFont(Font.TYPE1_FONT, inputStream).deriveFont(size);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

            //register the font
            ge.registerFont(customFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            if (fontType == FontType.TTF)
                LogFacility.log("Cannot find file: " + "resources/data/fonts/" + fontName + ".ttf", LogFacility.LoggingLevel.Error);
            else
                LogFacility.log("Cannot find file: " + "resources/data/fonts/" + fontName + ".otf", LogFacility.LoggingLevel.Error);
            customFont = comp.Component.STD_FONT.deriveFont(size);
        }
        finally {
            // actually this is useless, because "inputStream.close()" is an empty method.
            // Actually this is necessary, since subclasses are overriding the close method for proper disposal
            // of system resources.
            // You do not know at compile time what type the instance is going to be of, but you can definitely be
            // sure it does something for releasing the lock on the file.
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return customFont;
    }

    /** For further information see: {@link general.io.FontLoader#loadFont(String, float, general.io.FontLoader.FontType)}.
     * The additional parameter <code>int style</code> is part of {@link java.awt.Font}: <code>Font.BOLD</code>, <code>Font.ITALIC</code>,...
     *
     * @return <code>loadFont(fontName, size, fontType).deriveFont(style)</code> */
    public static Font loadFont (String fontName, float size, int style ,FontType fontType) {
        return loadFont(fontName, size, fontType).deriveFont(style);
    }
}
