package org.ngs.objectstorage.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.ngs.objectstorage.dto.FileDto;
import org.ngs.objectstorage.entity.*;
import org.ngs.objectstorage.enums.PreSignedOperation;
import org.ngs.objectstorage.enums.UploadStatus;
import org.ngs.objectstorage.repository.FileListingRepository;
import org.ngs.objectstorage.repository.FileRegistryRepository;
import org.ngs.objectstorage.repository.FileRepository;
import org.ngs.objectstorage.repository.PreSignedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;

@Slf4j
@Service
public class PreSignedService {

    @Autowired
    private PreSignedRepository preSignedRepository;

    @Autowired
    private DiskService diskService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileRegistryRepository fileRegistryRepository;

    @Autowired
    private FileListingRepository fileListingRepository;

    private static void validateRequest(PreSignedEntity preSignedEntity, PreSignedOperation preSignedOperation) {
        if (!preSignedOperation.name().equals(preSignedEntity.getOperation())) {
            throw new RuntimeException("invalid operation on presignedId " +
                    preSignedEntity.getKey().getPreSignedUUID());
        }
        if (preSignedEntity.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("operation has expired already for presigned " +
                    preSignedEntity.getKey().getPreSignedUUID());
        }
    }

    public FileDto persistFile(String presignedId, HttpServletRequest httpServletRequest) throws IOException {
        PreSignedEntity preSignedEntity = preSignedRepository.findByKey(presignedId);
        validateRequest(preSignedEntity, PreSignedOperation.UPLOAD);

        FileKey fileKey = FileKey.builder()
                .fileName(preSignedEntity.getFileName())
                .bucketName(preSignedEntity.getBucketName())
                .userName(preSignedEntity.getUserName())
                .deleted(false)
                .build();
        FileEntity fileEntity = fileRepository.findByKey(fileKey);

        diskService.persistToDisk(fileEntity.getFileUUID().toString(), fileEntity.getMd5Hash(),
                fileEntity.getFileSize(), httpServletRequest.getInputStream());

        fileEntity.setStatus(UploadStatus.UPLOADED.name());
        fileRepository.save(fileEntity);

        FileRegistryEntity fileRegistryEntity = fileRegistryRepository.findByKey(new FileRegistryKey(fileEntity.getFileUUID()));
        fileRegistryEntity.setFileSize(fileEntity.getFileSize());
        fileRegistryEntity.setMd5Hash(fileEntity.getMd5Hash());
        fileRegistryRepository.save(fileRegistryEntity);

        FileListingEntity fileListingEntity = FileListingEntity.builder()
                .key(FileListingKey.builder()
                        .userName(fileKey.getUserName())
                        .fileName(fileKey.getFileName())
                        .bucketName(fileKey.getBucketName())
                        .deleted(fileKey.isDeleted())
                        .build())
                .createdAt(Instant.now())
                .build();
        fileListingRepository.save(fileListingEntity);

        return FileDto.builder()
                .fileName(fileKey.getFileName())
                .fileSize(fileEntity.getFileSize())
                .status(fileEntity.getStatus())
                .createdAt(fileListingEntity.getCreatedAt())
                .md5Hash(fileEntity.getMd5Hash())
                .build();
    }


    public void fetchFile(String presignedId, HttpServletResponse httpServletResponse) throws IOException {
        PreSignedEntity preSignedEntity = preSignedRepository.findByKey(presignedId);
        validateRequest(preSignedEntity, PreSignedOperation.DOWNLOAD);

        FileKey fileKey = FileKey.builder()
                .fileName(preSignedEntity.getFileName())
                .bucketName(preSignedEntity.getBucketName())
                .userName(preSignedEntity.getUserName())
                .deleted(false)
                .build();
        FileEntity fileEntity = fileRepository.findByKey(fileKey);

        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.setContentType("application/octet-stream");
        httpServletResponse.setHeader("Content-Length", String.valueOf(fileEntity.getFileSize()));
        httpServletResponse.setHeader("Md5Hash", fileEntity.getMd5Hash());
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + fileKey.getFileName() + "\"");
        diskService.writeFileToServletStream(fileEntity.getFileUUID().toString(), httpServletResponse);
    }

    public FileDto deleteFile(String presignedId) {
        PreSignedEntity preSignedEntity = preSignedRepository.findByKey(presignedId);
        validateRequest(preSignedEntity, PreSignedOperation.DELETE);

        FileKey fileKey = FileKey.builder()
                .fileName(preSignedEntity.getFileName())
                .bucketName(preSignedEntity.getBucketName())
                .userName(preSignedEntity.getUserName())
                .deleted(false)
                .build();
        FileEntity fileEntity = fileRepository.findByKey(fileKey);

        boolean deleted = diskService.deleteFile(fileEntity.getFileUUID().toString());
        if (!deleted) {
            throw new RuntimeException("unable to delete file {}" + presignedId);
        }

        fileRepository.deleteById(fileKey);
        fileKey.setDeleted(true);
        fileEntity.setKey(fileKey);
        fileRepository.save(fileEntity);

        FileListingKey fileListingKey = FileListingKey.builder()
                .deleted(false)
                .bucketName(fileKey.getBucketName())
                .userName(fileKey.getUserName())
                .fileName(fileKey.getFileName())
                .build();
        FileListingEntity fileListingEntity = fileListingRepository.findByKey(fileListingKey);
        fileListingRepository.deleteById(fileListingKey);
        fileListingKey.setDeleted(true);
        fileListingEntity.setKey(fileListingKey);
        fileListingRepository.save(fileListingEntity);

        FileRegistryKey fileRegistryKey = new FileRegistryKey(fileEntity.getFileUUID());
        FileRegistryEntity fileRegistryEntity = fileRegistryRepository.findByKey(fileRegistryKey);
        fileRegistryEntity.setOrphan(true);
        fileRegistryRepository.save(fileRegistryEntity);

        return FileDto.builder()
                .fileName(fileKey.getFileName())
                .fileSize(fileEntity.getFileSize())
                .status(fileEntity.getStatus())
                .createdAt(fileListingEntity.getCreatedAt())
                .md5Hash(fileEntity.getMd5Hash())
                .deleted(true)
                .build();
    }
}
