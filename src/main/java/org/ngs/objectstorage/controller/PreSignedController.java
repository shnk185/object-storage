package org.ngs.objectstorage.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.ngs.objectstorage.dto.FileDto;
import org.ngs.objectstorage.service.PreSignedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/presigned")
public class PreSignedController {

    @Autowired
    private PreSignedService preSignedService;

    @PostMapping("/{presignedId}")
    public ResponseEntity<FileDto> uploadFile(@PathVariable String presignedId, HttpServletRequest httpServletRequest) throws IOException {
        log.info("received presigned file upload request {}", presignedId);
        FileDto response = preSignedService.persistFile(presignedId, httpServletRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{presignedId}")
    public void downloadFile(@PathVariable String presignedId, HttpServletResponse httpServletResponse) throws IOException {
        log.info("received presigned file download request {}", presignedId);
        preSignedService.fetchFile(presignedId, httpServletResponse);
        log.info("successfully fetched presigned file download {}", presignedId);
    }

    @DeleteMapping("/{presignedId}")
    public ResponseEntity<FileDto> deleteFile(@PathVariable String presignedId) {
        log.info("received presigned delete file request {}", presignedId);
        FileDto fileDto = preSignedService.deleteFile(presignedId);
        return ResponseEntity.ok(fileDto);
    }
}
