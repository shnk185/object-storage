package org.ngs.objectstorage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileListingDto {
    private String bucketName;
    private String path;
    private String userName;
    private Integer pageNumber;
    private Integer pageSize;
    private List<FileDto> fileDtos;
}
