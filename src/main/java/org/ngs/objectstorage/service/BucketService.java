package org.ngs.objectstorage.service;

import org.ngs.objectstorage.dto.BucketDto;
import org.ngs.objectstorage.entity.BucketEntity;
import org.ngs.objectstorage.entity.BucketKey;
import org.ngs.objectstorage.repository.BucketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BucketService {

    @Autowired
    private BucketRepository bucketRepository;

    public BucketDto createBucket(BucketDto bucket) {
        BucketEntity bucketEntity = new BucketEntity();
        BucketKey bucketKey = new BucketKey();
        bucketKey.setUserName(bucket.getUserName());
        bucketKey.setBucketName(bucket.getBucketName());
        bucketKey.setDeleted(false);
        bucketEntity.setCreatedAt(Instant.now());
        bucketEntity.setKey(bucketKey);

        bucketRepository.save(bucketEntity);

        return BucketDto.builder()
                .bucketName(bucketEntity.getKey().getBucketName())
                .userName(bucketEntity.getKey().getUserName())
                .deleted(bucketEntity.getKey().isDeleted())
                .createdAt(bucketEntity.getCreatedAt())
                .build();
    }
}
