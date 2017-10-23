package no.nav.pam.cloud.storage;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;

import java.io.InputStream;
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

    public String store(String name, InputStream content)
            throws CloudStorageException {

        try {
            return storage
                    .get(bucket)
                    .create(name, content, Bucket.BlobWriteOption.doesNotExist())
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
