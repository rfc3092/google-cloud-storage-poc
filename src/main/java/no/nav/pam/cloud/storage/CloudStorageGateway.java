package no.nav.pam.cloud.storage;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import no.nav.pam.image.ImageDownscaler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CloudStorageGateway {

    private final String bucket;
    private final Storage storage;
    private final ImageDownscaler downscaler;

    public CloudStorageGateway(
            String clientId,
            String clientEmail,
            String privateKeyPkcs8,
            String privateKeyId,
            String project,
            String bucket,
            ImageDownscaler downscaler)
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
        this.downscaler = downscaler;

    }

    public String store(String name, byte[] content)
            throws CloudStorageException {

        try {
            BlobInfo info = BlobInfo
                    .newBuilder(bucket, name)
                    .setCacheControl("max-age=1337")
                    .build();
            return storage
                    .create(info, downscaler.downscale(name, content), Storage.BlobTargetOption.doesNotExist())
                    .getName();
        } catch (Exception e) {
            throw new CloudStorageException(e);
        }

    }

    public boolean delete(String name) {

        return name != null && storage.delete(BlobId.of(bucket, name));

    }

    public String getMediaLink(String name) {

        if (name == null || name.isEmpty()) {
            return null;
        }
        Blob blob = storage.get(BlobId.of(bucket, name));
        return blob == null ? null : blob.getMediaLink();

    }

    public Set<String> getAllMediaLinks()
            throws CloudStorageException {

        try {

            Set<String> mediaLinks = new HashSet<>();
            getBucket()
                    .list()
                    .iterateAll()
                    .forEach(blob -> mediaLinks.add(blob.getMediaLink()));
            return mediaLinks;

        } catch (StorageException e) {
            throw new CloudStorageException(e);
        }

    }

    public void deleteAllMediaLinks(Set<String> mediaLinks)
            throws CloudStorageException {

        try {

            getBucket()
                    .list()
                    .iterateAll()
                    .forEach(blob -> {
                        if (mediaLinks.contains(blob.getMediaLink())) {
                            blob.delete();
                        }
                    });

        } catch (StorageException e) {
            throw new CloudStorageException(e);
        }

    }

    private Bucket getBucket()
            throws CloudStorageException {

        Bucket b = storage.get(bucket);
        if (b == null) {
            throw new CloudStorageException("Cannot find configured bucket " + bucket);
        }
        return b;

    }

}
