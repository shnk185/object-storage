package org.ngs.objectstorage.service;

import org.ngs.objectstorage.dto.FileCreationDto;
import org.ngs.objectstorage.dto.FileDto;
import org.ngs.objectstorage.dto.PreSignedDto;
import org.ngs.objectstorage.entity.*;
import org.ngs.objectstorage.enums.UploadStatus;
import org.ngs.objectstorage.repository.FileListingRepository;
import org.ngs.objectstorage.repository.FileRegistryRepository;
import org.ngs.objectstorage.repository.FileRepository;
import org.ngs.objectstorage.repository.PreSignedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    private FileRegistryRepository fileRegistryRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private PreSignedRepository preSignedRepository;

    @Autowired
    private FileListingRepository fileListingRepository;

    public FileCreationDto createFiles(String bucketName, FileCreationDto fileCreationDto) {
        Instant now = Instant.now();
        List<FileDto> responseFileDtos = new ArrayList<>();
        checkAndInvalidate(bucketName, fileCreationDto);
        for (FileDto fileDto: fileCreationDto.getFileDtos()) {
            FileRegistryEntity fileRegistryEntity = new FileRegistryEntity();
            FileRegistryKey fileRegistryKey = new FileRegistryKey();
            fileRegistryKey.setFileUUID(UUID.randomUUID());
            fileRegistryEntity.setKey(fileRegistryKey);
            fileRegistryEntity.setCreatedAt(now);
            fileRegistryRepository.save(fileRegistryEntity);

            FileEntity fileEntity = new FileEntity();
            FileKey fileKey = new FileKey();
            fileKey.setUserName(fileCreationDto.getUserName());
            fileKey.setBucketName(bucketName);
            fileKey.setFileName(fileDto.getFileName());
            fileKey.setDeleted(false);
            fileEntity.setKey(fileKey);
            fileEntity.setFileSize(fileDto.getFileSize());
            fileEntity.setCreatedAt(now);
            fileEntity.setStatus(UploadStatus.CREATED.name());
            fileEntity.setFileUUID(fileRegistryEntity.getKey().getFileUUID());
            fileEntity.setMd5Hash(fileDto.getMd5Hash());
            fileRepository.save(fileEntity);

            responseFileDtos.add(FileDto.builder()
                    .fileName(fileEntity.getKey().getFileName())
                    .fileSize(fileEntity.getFileSize())
                    .createdAt(fileEntity.getCreatedAt())
                    .status(fileEntity.getStatus())
                    .md5Hash(fileEntity.getMd5Hash())
                    .deleted(false)
                    .build());
        }

        return FileCreationDto.builder()
                .fileDtos(responseFileDtos)
                .userName(fileCreationDto.getUserName())
                .build();
    }

    private void checkAndInvalidate(String bucketName, FileCreationDto fileCreationDto) {
        for (FileDto fileDto: fileCreationDto.getFileDtos()) {
            FileKey fileKey = new FileKey();
            fileKey.setUserName(fileCreationDto.getUserName());
            fileKey.setBucketName(bucketName);
            fileKey.setFileName(fileDto.getFileName());
            Optional<FileEntity> fileEntityOptional = fileRepository.findById(fileKey);
            if (fileEntityOptional.isEmpty()) return;
            FileEntity fileEntity = fileEntityOptional.get();

            FileRegistryKey fileRegistryKey = new FileRegistryKey(fileEntity.getFileUUID());
            FileRegistryEntity fileRegistryEntity = fileRegistryRepository.findById(fileRegistryKey)
                    .orElseThrow(() -> new RuntimeException("invalid fileRegistryEntity"));
            fileRegistryEntity.setOrphan(true);
            fileRegistryRepository.save(fileRegistryEntity);

            FileListingKey fileListingKey = FileListingKey.builder()
                    .deleted(false)
                    .bucketName(fileKey.getBucketName())
                    .userName(fileKey.getUserName())
                    .fileName(fileKey.getFileName())
                    .build();
            FileListingEntity fileListingEntity = fileListingRepository.findById(fileListingKey)
                    .orElseThrow(() -> new RuntimeException("invalid fileListingEntity"));
            fileListingRepository.deleteById(fileListingKey);
            fileListingKey.setDeleted(true);
            fileListingEntity.setKey(fileListingKey);
            fileListingRepository.save(fileListingEntity);
        }
    }

    public PreSignedDto createPreSignedOperation(String bucketName, String fileName, PreSignedDto preSignedDto) {
        Instant now = Instant.now();
        Instant expire = now.plusSeconds(30 * 60);
        PreSignedEntity preSignedEntity = new PreSignedEntity();
        preSignedEntity.setOperation(preSignedDto.getOperation());
        preSignedEntity.setBucketName(bucketName);
        preSignedEntity.setFileName(fileName);
        preSignedEntity.setUserName(preSignedDto.getUserName());
        preSignedEntity.setKey(new PreSignedKey(UUID.randomUUID()));
        preSignedEntity.setCreatedAt(now);
        preSignedEntity.setExpiresAt(expire);
        preSignedRepository.save(preSignedEntity);

        return PreSignedDto.builder()
                .preSigned(preSignedEntity.getKey().getPreSignedUUID().toString())
                .operation(preSignedEntity.getOperation())
                .fileName(preSignedEntity.getFileName())
                .bucketName(preSignedEntity.getBucketName())
                .userName(preSignedEntity.getUserName())
                .createdAt(preSignedEntity.getCreatedAt())
                .expiresAt(preSignedEntity.getExpiresAt())
                .build();
    }

    public FileDto fetchFileInfo(String bucketName, String fileName, String userName, Boolean deleted) {
        FileKey fileKey = new FileKey();
        if (deleted == null) {
            fileKey.setDeleted(false);
        } else {
            fileKey.setDeleted(deleted);
        }
        fileKey.setFileName(fileName);
        fileKey.setUserName(userName);
        fileKey.setBucketName(bucketName);

        FileEntity fileEntity = fileRepository.findById(fileKey)
                .orElseThrow(() -> new RuntimeException("invalid fileEntity"));;

        return FileDto.builder()
                .fileName(fileEntity.getKey().getFileName())
                .fileSize(fileEntity.getFileSize())
                .md5Hash(fileEntity.getMd5Hash())
                .status(fileEntity.getStatus())
                .createdAt(fileEntity.getCreatedAt())
                .build();
    }
}
