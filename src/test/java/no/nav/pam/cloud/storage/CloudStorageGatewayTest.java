package no.nav.pam.cloud.storage;

import com.google.common.net.MediaType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
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
        assertNull(gateway.getMediaLink("This is a name we're sure doesn't exist in out bucket"));

    }

    @Test
    public void getExistingMediaLink() {

        assertEquals("https://www.googleapis.com/download/storage/v1/b/nav-pam-logo/o/nav-logo.png?generation=1508495097202081&alt=media", gateway.getMediaLink("nav-logo.png"));

    }

    @Test
    public void deleteNonExistingObject() {

        assertFalse(gateway.delete(null));
        assertFalse(gateway.delete(""));
        assertFalse(gateway.delete("This is a name we're sure doesn't exist in out bucket"));

    }

    /**
     * Note - if this test fails, you need to do some manual cleanup in order to remove any created object (with a
     * generated UUID as its name).
     *
     * @throws Exception Hopefully not.
     */
    @Test
    public void storeObjectGetItsMediaLinkAndThenDeleteIt()
            throws Exception {

        URL url = this.getClass().getResource("/nav-logo.png");
        assertNotNull(url);
        File file = new File(url.getFile());
        assertTrue(file.exists());
        InputStream content = new FileInputStream(file);
        String name = UUID.randomUUID().toString() + ".png";

        assertEquals(name, gateway.store(name, content, "image/png"));

        String mediaLink = gateway.getMediaLink(name);
        assertTrue(mediaLink.contains(name));

        assertTrue(gateway.delete(name));

    }

}
