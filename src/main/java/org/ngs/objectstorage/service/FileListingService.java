package org.ngs.objectstorage.service;

import org.ngs.objectstorage.dto.FileDto;
import org.ngs.objectstorage.dto.FileListingDto;
import org.ngs.objectstorage.entity.FileListingEntity;
import org.ngs.objectstorage.repository.FileListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class FileListingService {

    @Autowired
    private FileListingRepository fileListingRepository;

    public FileListingDto listFiles(String bucketName, String path, String userName, Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) pageNumber = 0;
        if (pageSize == null) pageSize = 10;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Slice<FileListingEntity> fileListingEntities = fileListingRepository
                .findByKeyUserNameAndKeyBucketNameAndKeyDeletedAndKeyFileNameStartingWith(userName, bucketName,
                        false, path, pageRequest);

        List<FileDto> responseFileDtos = new ArrayList<>();
        for (FileListingEntity fileListingEntity: fileListingEntities) {
            responseFileDtos.add(FileDto.builder()
                    .fileName(fileListingEntity.getKey().getFileName())
                    .build());
        }
        return FileListingDto.builder()
                .userName(userName)
                .bucketName(bucketName)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .path(path)
                .fileDtos(responseFileDtos)
                .build();
    }
}
