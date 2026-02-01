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
@Table("file_registry")
public class FileRegistryEntity {

    @PrimaryKey
    private FileRegistryKey key;

    private Instant createdAt;

    private String md5Hash;

    private Long fileSize;

    private boolean orphan;

}
