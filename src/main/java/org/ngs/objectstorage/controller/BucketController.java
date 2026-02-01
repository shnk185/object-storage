package org.ngs.objectstorage.controller;

import lombok.extern.slf4j.Slf4j;
import org.ngs.objectstorage.service.BucketService;
import org.ngs.objectstorage.dto.BucketDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/buckets")
public class BucketController {

    @Autowired
    private BucketService bucketService;

    @PostMapping
    public ResponseEntity<BucketDto> createBucket(@RequestBody BucketDto bucket) {
        log.info("received bucket create request {}", bucket);
        BucketDto response = bucketService.createBucket(bucket);
        return ResponseEntity.ok(response);
    }


}
