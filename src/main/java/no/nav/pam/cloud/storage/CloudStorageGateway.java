package no.nav.pam.cloud.storage;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.util.ArrayList;
import java.util.List;


public class CloudStorageGateway {

    private final String bucket;
    private final Storage storage;

    public CloudStorageGateway(String bucket) {

        this.bucket = bucket;
        // TODO: Get config (auth) from YML, not environment.
        storage = StorageOptions.getDefaultInstance().getService();

    }

    public BlobId store(Blob blob)
            throws CloudStorageException {
        // TODO: Implement and hide BlobId/Blob.
        return null;
    }

    List<String> list() {

        Page<Blob> blobs = storage.list(bucket);
        List<String> urls = new ArrayList<>();
        blobs.iterateAll().forEach(blob -> {
            urls.add(blob.getMediaLink());
        });
        return urls;

    }

}
