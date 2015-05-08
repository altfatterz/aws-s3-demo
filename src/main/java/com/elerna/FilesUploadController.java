package com.elerna;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Controller
public class FilesUploadController {

    @Autowired
    private AmazonS3 amazonS3;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {

        if (!file.isEmpty()) {
            try {

                String bucketName = "elerna";

                System.out.println("File name:" + file.getName());
                System.out.println("File content type:" + file.getContentType());
                System.out.println("File original file name:" + file.getOriginalFilename());
                System.out.println("File size:" + file.getSize());

                TransferManager transferManager = new TransferManager(amazonS3);

                // Otherwise: (Service: Amazon S3; Status Code: 301; Error Code: PermanentRedirect)
                amazonS3.setRegion(Region.getRegion(Regions.EU_WEST_1));


                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentDisposition("attachment; filename=" + file.getOriginalFilename());

                String id = UUID.randomUUID().toString();
                Upload upload = transferManager.upload(bucketName, id, file.getInputStream(), objectMetadata);

                upload.waitForUploadResult();

                return "https://s3-" + Regions.EU_WEST_1.getName() + ".amazonaws.com/" + bucketName + "/" + id;
            } catch (Exception e) {
                return "You failed to upload " + e.getMessage();
            }


        }

        return null;
    }


}
