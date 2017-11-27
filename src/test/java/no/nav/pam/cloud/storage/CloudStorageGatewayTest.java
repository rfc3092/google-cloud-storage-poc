package no.nav.pam.cloud.storage;

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

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudStorageGatewayTest {

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

        byte[] content = loadImageFromResource("/nav-logo.png");
        String name = UUID.randomUUID().toString() + ".png";

        assertEquals(name, gateway.store(name, content));

        String mediaLink = gateway.getMediaLink(name);
        assertTrue(mediaLink.contains(name));

        assertTrue(gateway.delete(name));

    }

    private byte[] loadImageFromResource(String name)
            throws IOException {

        URL url = this.getClass().getResource(name);
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
