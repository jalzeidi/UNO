/* ImageProcessor.java
 * This class handles images
 * It includes methods that load and rotate images
 * Additional image processing methods may be added as needed
 */

package uno;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageProcessor {

    //Loads an image from the project's resource folder
    public static BufferedImage loadImage(String name) {
        InputStream inputStream = ImageProcessor.class.getResourceAsStream(name);
        BufferedImage img = null;
        try {
            img = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    /* Returns a new rotated image with the degree of rotation set by the parameter "deg"
     * This method was taken from an answer on StackOverFlow (not my own code)
     */
    public static BufferedImage rotateImage(BufferedImage img, double deg) {
        if(deg == 0)
            return img;
        double rad = Math.toRadians(deg);
        double sinTheta = Math.abs(Math.sin(rad));
        double cosTheta = Math.abs(Math.cos(rad));
        int width = (int) Math.floor(img.getWidth() * cosTheta + img.getHeight() * sinTheta);
        int height = (int) Math.floor(img.getWidth() * sinTheta + img.getHeight() * cosTheta);

        BufferedImage rotatedImg = new BufferedImage(width, height, img.getType());
        AffineTransform at = new AffineTransform();
        at.translate(width / 2, height / 2);
        at.rotate(rad, 0, 0);
        at.translate(-img.getWidth() / 2, -img.getHeight() / 2);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        atop.filter(img, rotatedImg);

        return rotatedImg;
    }
}