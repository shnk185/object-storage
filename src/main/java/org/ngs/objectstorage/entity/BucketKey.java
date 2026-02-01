package org.ngs.objectstorage.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@Data
@PrimaryKeyClass
@AllArgsConstructor
@NoArgsConstructor
public class BucketKey {

    @PrimaryKeyColumn(name = "user_name", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String userName;

    @PrimaryKeyColumn(name = "deleted", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private boolean deleted;

    @PrimaryKeyColumn(name = "bucket_name", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private String bucketName;
}
