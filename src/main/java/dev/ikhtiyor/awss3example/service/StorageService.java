package dev.ikhtiyor.awss3example.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import dev.ikhtiyor.awss3example.payload.Attachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.util.Iterator;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    @Autowired
    public StorageService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }


    public String uploadFile(MultipartHttpServletRequest request) {

        Iterator<String> fileNames = request.getFileNames();

        while (fileNames.hasNext()) {

            MultipartFile file = request.getFile(fileNames.next());

            Assert.notNull(file, "File must be not null");

            uploadFileToS3(file);

        }

        return "File successful uploaded";

    }

    public ResponseEntity<?> downloadFile(String fileName) {

        Attachment attachment = downloadFileFromS3(fileName);

        assert attachment != null;
        byte[] bytes = attachment.getBytes();

        return ResponseEntity
                .ok()
                .contentLength(bytes.length)
                .header("Content-type", attachment.getContentType())
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(bytes);
    }

    public String deleteFile(String fileName) {

        deleteFileFromS3(fileName);
        return "File successful deleted";

    }

    private void uploadFileToS3(MultipartFile file) {

        File convertMultipartFileToFile = convertMultipartFileToFile(file);

        amazonS3.putObject(
                new PutObjectRequest(
                        bucketName,
                        UUID.randomUUID().toString(),
                        convertMultipartFileToFile
                )
        );

        convertMultipartFileToFile.delete();

    }

    private Attachment downloadFileFromS3(String fileName) {

        S3Object s3Object = amazonS3.getObject(bucketName, fileName);

        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        String contentType = s3Object.getObjectMetadata().getContentType();

        try {

            byte[] bytes = IOUtils.toByteArray(inputStream);

            return new Attachment(
                    bytes,
                    contentType
            );

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private void deleteFileFromS3(String fileName) {

        amazonS3.deleteObject(bucketName, fileName);

    }

    private File convertMultipartFileToFile(MultipartFile file) {

        File convertedFile = new File(file.getOriginalFilename());

        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            // error
            e.printStackTrace();
        }

        return convertedFile;
    }
}
