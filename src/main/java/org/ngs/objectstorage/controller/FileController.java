package org.ngs.objectstorage.controller;

import lombok.extern.slf4j.Slf4j;
import org.ngs.objectstorage.dto.FileCreationDto;
import org.ngs.objectstorage.dto.FileDto;
import org.ngs.objectstorage.dto.PreSignedDto;
import org.ngs.objectstorage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/buckets/{bucketName}/files")
public class FileController {

    @Autowired
    private FileService fileService;


    @PostMapping
    public ResponseEntity<FileCreationDto> createFiles(@PathVariable String bucketName,
                                                       @RequestBody FileCreationDto fileCreationDto) {
        log.info("received file creation request {}", fileCreationDto);
        FileCreationDto response = fileService.createFiles(bucketName, fileCreationDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{fileName}/presigned")
    public ResponseEntity<PreSignedDto> createPreSignedOperation(@PathVariable String bucketName,
                                                                 @PathVariable String fileName,
                                                                 @RequestBody PreSignedDto preSignedDto) {
        log.info("received presigned creation request {} bucket {} fileName {}", preSignedDto, bucketName, fileName);
        PreSignedDto response = fileService.createPreSignedOperation(bucketName, fileName, preSignedDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<FileDto> getFileInfo(@PathVariable String bucketName,
                                               @PathVariable String fileName,
                                               @RequestParam String userName) {
        log.info("received presigned file info request bucket {} fileName {} userName {}", bucketName, fileName, userName);
        FileDto response = fileService.fetchFileInfo(bucketName, fileName, userName);
        return ResponseEntity.ok(response);
    }
}
