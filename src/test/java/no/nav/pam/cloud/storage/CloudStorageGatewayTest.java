package no.nav.pam.cloud.storage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

/**
 * Run this test manually, as you need an actual configuration file (externally, or by adding a
 * {@code src/test/resources/application.yaml} file or what have you).
 */
@RunWith(SpringRunner.class)
@SpringBootTest
//@Ignore
public class CloudStorageGatewayTest {

    private static final Logger LOG = LoggerFactory.getLogger(CloudStorageGatewayTest.class);
    private static final String RESOURCE_TYPE = "png";
    private static final String RESOURCE_NAME = "/nav-logo." + RESOURCE_TYPE;
    private static final int MULTIPLE_IMAGES_LIMIT = 10;

    @Autowired
    private CloudStorageGateway gateway;

    @Test
    public void getNonExistingMediaLink() {

        assertNull(gateway.getMediaLink(null));
        assertNull(gateway.getMediaLink(""));
        assertNull(gateway.getMediaLink("This is a name we're sure doesn't exist in our bucket"));

    }

    @Test
    public void deleteNonExistingObject() {

        assertFalse(gateway.delete(null));
        assertFalse(gateway.delete(""));
        assertFalse(gateway.delete("This is a name we're sure doesn't exist in our bucket"));

    }

    /**
     * Test storing a blob, getting its media link and then deleting it.
     * <br/><br/>
     * Note - if this test fails, you need to do some manual cleanup in order to remove any created object (with a
     * generated UUID as its name).
     *
     * @throws CloudStorageException Hopefully not.
     */
    @Test
    public void storeObjectGetItsMediaLinkAndThenDeleteIt()
            throws Exception {

        byte[] content = loadImageFromResource();
        String name = UUID.randomUUID().toString() + "." + RESOURCE_TYPE;

        assertEquals(name, gateway.store(name, content));

        String mediaLink = gateway.getMediaLink(name);
        LOG.info("Stored blob with name {} and media link {}", name, mediaLink);
        assertTrue(mediaLink.contains(name));

        assertTrue(gateway.delete(name));
        LOG.info("Deleted blob with name {} and media link {}", name, mediaLink);

    }

    /**
     * Not really a test, but demonstrates the use of {@link CloudStorageGateway#getAllMediaLinks()} and
     * {@link CloudStorageGateway#deleteAllMediaLinks(Set)} by uploading a number of images, as set by
     * {@link #MULTIPLE_IMAGES_LIMIT}.
     * <br/><br/>
     * Note - if this test fails, you need to do some manual cleanup in order to remove any created object (with a
     * name on the pattern {@code imageX.png}).
     *
     * @throws CloudStorageException Hopefully not.
     */
    @Test
    public void logAllMediaLinks()
            throws Exception {

        byte[] content = loadImageFromResource();
        Set<String> mediaLinks = new HashSet<>(MULTIPLE_IMAGES_LIMIT);
        for (int i = 0; i < MULTIPLE_IMAGES_LIMIT; i++) {
            String name = getNameFrom(i);
            assertEquals(name, gateway.store(name, content));
            String mediaLink = gateway.getMediaLink(name);
            LOG.info("Created blob with name {} and media link {}", name, mediaLink);
            mediaLinks.add(mediaLink);
        }

        gateway.getAllMediaLinks().forEach(mediaLink -> LOG.info("Found blob with media link {}", mediaLink));

        gateway.deleteAllMediaLinks(mediaLinks);

    }

    private static String getNameFrom(int number) {

        return "test_image" + number + "." + RESOURCE_TYPE;

    }

    private byte[] loadImageFromResource()
            throws IOException {

        URL url = this.getClass().getResource(RESOURCE_NAME);
        assertNotNull(url);
        File file = new File(url.getFile());
        assertTrue(file.exists());
        int length = (int) file.length();
        byte[] content = new byte[length];
        try (InputStream input = new FileInputStream(file)) {
            assertEquals(length, input.read(content));
        }
        return content;

    }

}
