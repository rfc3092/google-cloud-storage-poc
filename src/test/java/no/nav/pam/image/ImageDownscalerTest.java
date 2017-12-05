package no.nav.pam.image;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ImageDownscalerTest {

    private static final Logger LOG = LoggerFactory.getLogger(ImageDownscalerTest.class);

    private final int maxSizeX;
    private final int maxSizeY;
    private final ImageDownscaler downscaler;
    private final String suffix;

    public ImageDownscalerTest(String suffix, int maxSizeX, int maxSizeY) {

        this.maxSizeX = maxSizeX;
        this.maxSizeY = maxSizeY;
        this.downscaler = new ImageDownscaler(maxSizeX, maxSizeY);
        this.suffix = suffix;

    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
                {"bmp", 50, 100},
                {"bmp", 100, 50},
                {"gif", 50, 100},
                {"gif", 100, 50},
                {"jpg", 50, 100},
                {"jpg", 100, 50},
                {"png", 50, 100},
                {"png", 100, 50},
        });
    }

    @Test
    public void reduceImage()
            throws Exception {

        BufferedImage original = loadImageFromResource("/nav-logo." + suffix);

        // Optionally save the original image for visual comparison.
        saveImageToTemporaryFolder(original, "_original_" + maxSizeX + "_" + maxSizeY + "." + suffix);

        BufferedImage reduced = downscaler.downscale(original);

        assertEquals(original.getType(), reduced.getType());
        assertTrue(reduced.getWidth() <= maxSizeX);
        assertTrue(reduced.getHeight() <= maxSizeY);

        // Optionally save the reduced image for visual comparison.
        saveImageToTemporaryFolder(reduced, "_reduced_" + maxSizeX + "_" + maxSizeY + "." + suffix);

    }

    private BufferedImage loadImageFromResource(String name)
            throws IOException {

        URL url = this.getClass().getResource(name);
        assertNotNull(url);
        File file = new File(url.getFile());
        assertTrue(file.exists());
        return ImageIO.read(file);

    }

    private static void saveImageToTemporaryFolder(BufferedImage image, String filename)
            throws IOException {

        String type = filename.substring(filename.lastIndexOf('.') + 1);
        String path = System.getProperty("java.io.tmpdir") + ImageDownscalerTest.class.getSimpleName() + File.separator;
        File folder = new File(path);
        if (!folder.exists()) {
            assertTrue(folder.mkdir());
            LOG.info("Created folder {}, delete after visual inspection", path);
        }

        LOG.info("Saving {}x{} image to file {} as {}", image.getWidth(), image.getHeight(), path + filename, type);
        assertTrue(ImageIO.write(image, type, new File(path + filename)));

    }

}
