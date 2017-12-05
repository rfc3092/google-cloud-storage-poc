**Google Cloud Storage PoC**

A simple test of hwo to use Google Cloud Storage to store blobs (here, images).

You will need your own GCS configuration, see `application.yml` and the `CloudStorageConfig` class - as well as a
properly configured bucket, of course.

Note that this
implementation does not rely on the default method of downloading the JSON configuration and pointing
the environment variable `GOOGLE_APPLICATION_CREDENTIALS` to it; rather, you should fill the relevant infomation into
the YML config from the JSON file.

Also, a basic image downscaler (based on a maximum X and Y pixel count) is included. For configuration of this see the
`ImageDownscalerConfig` class. 

The usage of the gateway is shown in `CloudStorageGatewayTest`, which is `@Ignore`d. Run it manually with a properly
placed breakpoint to see content appear in your bucket.