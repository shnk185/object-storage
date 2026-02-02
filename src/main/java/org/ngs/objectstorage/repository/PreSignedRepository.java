package org.ngs.objectstorage.repository;

import org.ngs.objectstorage.entity.PreSignedEntity;
import org.ngs.objectstorage.entity.PreSignedKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreSignedRepository extends CassandraRepository<PreSignedEntity, PreSignedKey> {
}
