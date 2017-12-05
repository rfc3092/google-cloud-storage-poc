package no.nav.pam.cloud.storage;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

/**
 * Run this test manually, as you need an actual configuration file (externally, or by adding a
 * {@code src/test/resources/application.yaml} file or what have you).
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class CloudStorageGatewayTest {

    private static final String RESOURCE_TYPE = "png";
    private static final String RESOURCE_NAME = "/nav-logo." + RESOURCE_TYPE;

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
     * Note - if this test fails, you need to do some manual cleanup in order to remove any created object (with a
     * generated UUID as its name).
     *
     * @throws CloudStorageException Hopefully not.
     */
    @Test
    public void storeObjectGetItsMediaLinkAndThenDeleteIt()
            throws IOException, CloudStorageException {

        byte[] content = loadImageFromResource();
        String name = UUID.randomUUID().toString() + "." + RESOURCE_TYPE;

        assertEquals(name, gateway.store(name, content));

        String mediaLink = gateway.getMediaLink(name);
        assertTrue(mediaLink.contains(name));

        assertTrue(gateway.delete(name));

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
