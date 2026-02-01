package org.ngs.objectstorage.repository;

import org.ngs.objectstorage.entity.FileRegistryEntity;
import org.ngs.objectstorage.entity.FileRegistryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRegistryRepository extends CassandraRepository<FileRegistryEntity, FileRegistryKey> {
}
