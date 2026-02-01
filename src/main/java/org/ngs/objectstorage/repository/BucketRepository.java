package org.ngs.objectstorage.repository;

import org.ngs.objectstorage.entity.BucketEntity;
import org.ngs.objectstorage.entity.BucketKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BucketRepository extends CassandraRepository<BucketEntity, BucketKey> {
}
