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

        return downscaleY((downscaleX(original)));

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

    private BufferedImage downscaleX(BufferedImage original) {

        if (original.getWidth() <= maxSizeX) {
            return original;
        }

        float factor = (float) maxSizeX / (float) original.getWidth();
        int newSizeY = Math.max(1, (int) (factor * original.getHeight()));
        return redraw(original, maxSizeX, newSizeY);

    }

    private BufferedImage downscaleY(BufferedImage original) {

        if (original.getHeight() <= maxSizeY) {
            return original;
        }

        float factor = (float) maxSizeY / (float) original.getHeight();
        int newSizeX = Math.max(1, (int) (factor * original.getWidth()));
        return redraw(original, newSizeX, maxSizeY);

    }

    private static BufferedImage redraw(BufferedImage original, int newSizeX, int newSizeY) {

        BufferedImage reduced = new BufferedImage(newSizeX, newSizeY, original.getType());

        double scaleX = (double) newSizeX / original.getWidth();
        double scaleY = (double) newSizeY / original.getHeight();
        AffineTransform transformation = new AffineTransform();
        transformation.scale(scaleX, scaleY);

        return new AffineTransformOp(transformation, AffineTransformOp.TYPE_BILINEAR).filter(original, reduced);

    }

}
