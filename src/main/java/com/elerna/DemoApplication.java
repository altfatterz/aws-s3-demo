package com.elerna;

import com.amazonaws.AmazonClientException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.UUID;

import static com.elerna.FixedSizeThumbnail.*;

@SpringBootApplication
public class DemoApplication { // implements CommandLineRunner {

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

//    @Override
//    public void run(String... args) throws Exception {
//
//        // Otherwise: (Service: Amazon S3; Status Code: 301; Error Code: PermanentRedirect)
//        amazonS3.setRegion(Region.getRegion(Regions.EU_WEST_1));
//
//        Resource resource = context.getResource("classpath:mountains.jpg");
//
//        TransferManager transferManager = new TransferManager(amazonS3);
//
//        String id = UUID.randomUUID().toString();
//
//        upload(transferManager, resource, ICON, id);
//        upload(transferManager, resource, SMALL, id);
//        upload(transferManager, resource, BIG, id);
//        upload(transferManager, resource, BIG_RETINA, id);
//        upload(transferManager, resource, ORIGINAL, id);
//    }


    private void upload(TransferManager transferManager, Resource resource, FixedSizeThumbnail fixedSizeThumbnail, String id) throws IOException, InterruptedException {
        try (
                InputStream is = resource.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream()
        ) {

            Thumbnails.Builder<? extends InputStream> builder;
            if (FixedSizeThumbnail.ORIGINAL == fixedSizeThumbnail) {
                builder = Thumbnails.of(is).scale(1.0);
            } else {
                builder = Thumbnails.of(is).size(fixedSizeThumbnail.getWidth(), fixedSizeThumbnail.getHeight());
                if (FixedSizeThumbnail.ICON == fixedSizeThumbnail) {
                    builder.keepAspectRatio(false);
                }
            }

            builder.toOutputStream(os);
            ByteArrayInputStream is2 = new ByteArrayInputStream(os.toByteArray());

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentDisposition("attachment; filename=" + resource.getFilename());

            Upload upload = transferManager.upload("elerna", id + "/" + fixedSizeThumbnail.name().toLowerCase(), is2, objectMetadata);

            try {
                upload.waitForUploadResult();
                System.out.println("Upload complete.");
            } catch (AmazonClientException amazonClientException) {
                System.out.println("Unable to upload file, upload was aborted.");
                amazonClientException.printStackTrace();
            }

        }
    }
}
