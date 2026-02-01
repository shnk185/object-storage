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
public class PreSignedDto {
    private String operation;
    private String fileName;
    private String preSigned;
    private String bucketName;
    private String userName;
    private Instant createdAt;
    private Instant expiresAt;

}
