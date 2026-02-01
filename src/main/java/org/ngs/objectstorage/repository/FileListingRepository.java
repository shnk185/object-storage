package org.ngs.objectstorage.repository;

import org.ngs.objectstorage.entity.FileListingEntity;
import org.ngs.objectstorage.entity.FileListingKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
public interface FileListingRepository extends CassandraRepository<FileListingEntity, FileListingKey> {
    Slice<FileListingEntity> findByKeyUserNameAndKeyBucketNameAndKeyDeletedAndKeyFileNameStartingWith(
            String userName, String bucketName, boolean deleted, String prefix, Pageable pageable);
}
