package no.nav.pam.cloud.storage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudStorageGatewayTest {

    @Autowired
    private CloudStorageGateway gateway;

    @Test
    public void getMediaLinks()
            throws Exception {

        for (String url : gateway.list()) {
            System.out.println(url);
        }

    }

}
