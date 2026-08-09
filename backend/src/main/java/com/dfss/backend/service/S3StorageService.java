package com.dfss.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final String bucketName;

    public S3StorageService(
            S3Client s3Client,
            @Value("${aws.s3.bucket}") String bucketName
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public String store(
            MultipartFile file,
            String storedFileName
    ) throws IOException {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(storedFileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromInputStream(
                        file.getInputStream(),
                        file.getSize()
                )
        );

        return "s3://" + bucketName + "/" + storedFileName;
    }

    @Override
    public Resource load(
            String storedFileName
    ) throws IOException {

        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storedFileName)
                    .build();

            byte[] data = s3Client
                    .getObjectAsBytes(request)
                    .asByteArray();

            return new ByteArrayResource(data);

        } catch (NoSuchKeyException exception) {
            throw new FileNotFoundException(
                    "S3 file not found"
            );
        } catch (S3Exception exception) {
            throw new IOException(
                    "Failed to download file from S3",
                    exception
            );
        }
    }

    @Override
    public void delete(
            String storedFileName
    ) throws IOException {

        try {
            DeleteObjectRequest request =
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(storedFileName)
                            .build();

            s3Client.deleteObject(request);

        } catch (S3Exception exception) {
            throw new IOException(
                    "Failed to delete file from S3",
                    exception
            );
        }
    }

    @Override
    public boolean exists(
            String storedFileName
    ) {

        try {
            HeadObjectRequest request =
                    HeadObjectRequest.builder()
                            .bucket(bucketName)
                            .key(storedFileName)
                            .build();

            s3Client.headObject(request);

            return true;

        } catch (S3Exception exception) {
            return false;
        }
    }

    @Override
    public String getProviderName() {
        return "S3";
    }
}