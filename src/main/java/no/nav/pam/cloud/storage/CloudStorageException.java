package no.nav.pam.cloud.storage;

class CloudStorageException extends Exception {

    CloudStorageException(Throwable cause) {
        super(cause);
    }

    CloudStorageException(String message) {
        super(message);
    }

}
