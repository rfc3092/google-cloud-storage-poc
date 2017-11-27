package no.nav.pam.image;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageDownscaler {

    private final int maxSizeX;
    private final int maxSizeY;

    ImageDownscaler(int maxSizeX, int maxSizeY) {
        this.maxSizeX = maxSizeX;
        this.maxSizeY = maxSizeY;
    }

    public BufferedImage downscale(BufferedImage original) {

        boolean downscale = false;
        int newSizeX = original.getWidth();
        int newSizeY = original.getHeight();

        if (newSizeX > maxSizeX) {
            downscale = true;
            float factor = (float) maxSizeX / newSizeX;
            newSizeX = maxSizeX;
            newSizeY = Math.max(1, (int) (factor * newSizeY));
        }
        if (newSizeY > maxSizeY) {
            downscale = true;
            float factor = (float) maxSizeY / newSizeY;
            newSizeX = Math.max(1, (int) (factor * newSizeX));
            newSizeY = maxSizeY;
        }

        return downscale ? redraw(original, newSizeX, newSizeY) : original;

    }

    public byte[] downscale(String filename, byte[] content)
            throws ImageDownscalerException {

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            if (!ImageIO.write(downscale(ImageIO.read(new ByteArrayInputStream(content))), getType(filename), output)) {
                throw new ImageDownscalerException("Unable to write downscaled image content as type '" + getType(filename) + "'");
            }
            return output.toByteArray();
        } catch (IOException e) {
            throw new ImageDownscalerException(e);
        }

    }

    private static String getType(String filename)
            throws ImageDownscalerException {

        int i = filename.lastIndexOf('.');
        if (i < 0) {
            throw new ImageDownscalerException("Unable to find type from filename '" + filename + "'");
        }
        return filename.substring(i + 1);

    }

    private static BufferedImage redraw(BufferedImage original, int newSizeX, int newSizeY) {

        BufferedImage downscaled = new BufferedImage(newSizeX, newSizeY, original.getType());

        double scaleX = (double) newSizeX / original.getWidth();
        double scaleY = (double) newSizeY / original.getHeight();
        AffineTransform transformation = new AffineTransform();
        transformation.scale(scaleX, scaleY);

        return new AffineTransformOp(transformation, AffineTransformOp.TYPE_BILINEAR).filter(original, downscaled);

    }

}
