package com.jptaxi.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import java.util.Locale;

@Service
public class SupabaseStorageService {

    private final S3Client s3Client;
    private final String bucketName;
    private final String publicUrlPrefix;

    public SupabaseStorageService(
            @Value("${supabase.storage.endpoint}") String endpoint,
            @Value("${supabase.storage.region}") String region,
            @Value("${supabase.storage.access-key}") String accessKey,
            @Value("${supabase.storage.secret-key}") String secretKey,
            @Value("${supabase.storage.bucket}") String bucketName,
            @Value("${supabase.storage.public-url}") String publicUrlPrefix
    ) {
        this.bucketName = bucketName;
        this.publicUrlPrefix = publicUrlPrefix.endsWith("/") ? publicUrlPrefix.substring(0, publicUrlPrefix.length() - 1) : publicUrlPrefix;

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

    public String uploadImage(MultipartFile file, String folder) throws IOException {
        String extension = getExtension(file.getOriginalFilename());
        String fileName = folder + "/" + UUID.randomUUID() + extension;

        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return publicUrlPrefix + "/" + bucketName + "/" + fileName;
    }

    public String uploadBase64Image(byte[] imageBytes, String extension, String folder) {
        String fileName = folder + "/" + UUID.randomUUID() + extension;

        String contentType = "image/jpeg";
        if (".png".equals(extension)) contentType = "image/png";
        else if (".webp".equals(extension)) contentType = "image/webp";

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));

        return publicUrlPrefix + "/" + bucketName + "/" + fileName;
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null) {
            return ".jpg";
        }
        String filename = originalFilename.toLowerCase(Locale.ROOT);
        if (filename.endsWith(".jpeg")) return ".jpeg";
        if (filename.endsWith(".png")) return ".png";
        if (filename.endsWith(".webp")) return ".webp";
        return ".jpg";
    }
}
