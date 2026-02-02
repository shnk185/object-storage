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
public class BucketDto {
    private String bucketName;
    private String userName;
    private Boolean deleted;
    private Instant createdAt;
}
