package org.ngs.objectstorage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@Data
@Builder
@PrimaryKeyClass
@AllArgsConstructor
@NoArgsConstructor
public class FileKey {

    @PrimaryKeyColumn(name = "user_name", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String userName;

    @PrimaryKeyColumn(name = "bucket_name", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String bucketName;

    @PrimaryKeyColumn(name = "file_name", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
    private String fileName;

    @PrimaryKeyColumn(name = "deleted", ordinal = 3, type = PrimaryKeyType.PARTITIONED)
    private boolean deleted;


}
