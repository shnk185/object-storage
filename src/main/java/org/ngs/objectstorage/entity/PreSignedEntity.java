package org.ngs.objectstorage.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("pre_signed")
public class PreSignedEntity {

    @PrimaryKey
    private PreSignedKey key;

    private String operation;

    private String userName;

    private String bucketName;

    private String fileName;

    private Instant createdAt;

    private Instant expiresAt;
}
