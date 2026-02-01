package org.ngs.objectstorage.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.util.UUID;

@Data
@PrimaryKeyClass
@AllArgsConstructor
@NoArgsConstructor
public class PreSignedKey {

    @PrimaryKeyColumn(name = "pre_signed_uuid", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID preSignedUUID;
}
