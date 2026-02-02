package org.ngs.objectstorage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {
    private String fileName;
    private long fileSize;
    private Instant createdAt;
    private String status;
    private String md5Hash;
    private Boolean deleted;
}
