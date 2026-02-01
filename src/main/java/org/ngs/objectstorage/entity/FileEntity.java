package org.ngs.objectstorage.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("files")
public class FileEntity {

    @PrimaryKey
    private FileKey key;

    private UUID fileUUID;

    private Instant createdAt;

    private long fileSize;

    private String md5Hash;

    private String status;
}
