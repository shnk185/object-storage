package org.ngs.objectstorage.controller;

import lombok.extern.slf4j.Slf4j;
import org.ngs.objectstorage.dto.FileListingDto;
import org.ngs.objectstorage.service.FileListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/buckets/{bucketName}/list")
public class FileListingController {


    @Autowired
    private FileListingService fileListingService;

    @GetMapping
    public ResponseEntity<FileListingDto> fetchFileList(@PathVariable String bucketName,
                                                        @RequestParam String path,
                                                        @RequestParam String userName,
                                                        @RequestParam Integer pageNumber,
                                                        @RequestParam Integer pageSize) {
        log.info("received file listing request bucketname {} path {} userName {} pageNumber {} pageSize {}", bucketName, path, userName, pageNumber, pageSize);
        FileListingDto fileListingDto = fileListingService.listFiles(bucketName, path, userName, pageNumber, pageSize);
        return ResponseEntity.ok(fileListingDto);
    }
}
