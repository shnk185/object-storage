package org.ngs.objectstorage.repository;

import org.ngs.objectstorage.entity.FileEntity;
import org.ngs.objectstorage.entity.FileKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends CassandraRepository<FileEntity, FileKey> {
    FileEntity findByKey(FileKey fileKey);
}
