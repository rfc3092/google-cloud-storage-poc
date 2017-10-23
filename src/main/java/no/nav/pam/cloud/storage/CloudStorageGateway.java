package no.nav.pam.cloud.storage;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;

import java.io.IOException;
import java.io.InputStream;


public class CloudStorageGateway {

    private final String bucket;
    private final Storage storage;

    public CloudStorageGateway(String clientId, String clientEmail, String privateKeyPkcs8, String privateKeyId, String project, String bucket)
            throws CloudStorageException {

        try {
            ServiceAccountCredentials credentials = ServiceAccountCredentials.fromPkcs8(clientId, clientEmail, privateKeyPkcs8, privateKeyId, null);
            storage = StorageOptions
                    .newBuilder()
                    .setProjectId(project)
                    .setCredentials(credentials)
                    .build()
                    .getService();
        } catch (IOException e) {
            throw new CloudStorageException(e);
        }
        this.bucket = bucket;

    }

    public String store(String name, InputStream content, String contentType)
            throws CloudStorageException {

        try {
            return storage
                    .get(bucket)
                    .create(name, content, contentType, Bucket.BlobWriteOption.doesNotExist())
                    .getName();
        } catch (StorageException e) {
            throw new CloudStorageException(e);
        }

    }

    public boolean delete(String name) {

        if (name == null) {
            return false;
        }
        return storage.delete(BlobId.of(bucket, name));

    }

    public String getMediaLink(String name) {

        if (name == null || name.isEmpty()) {
            return null;
        }
        Blob blob = storage.get(BlobId.of(bucket, name));
        return blob == null ? null : blob.getMediaLink();

    }

}
