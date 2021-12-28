package dev.ikhtiyor.awss3example.controller;

import dev.ikhtiyor.awss3example.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;


@RestController
@RequestMapping("/api/file")
public class StorageController {

    private final StorageService service;

    @Autowired
    public StorageController(StorageService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public HttpEntity<?> uploadFile(
            MultipartHttpServletRequest request
    ) {
        String message = service.uploadFile(request);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/download")
    public HttpEntity<?> downloadFile(
            @RequestParam(name = "name") String name
    ) {
        return service.downloadFile(name);
    }

    @DeleteMapping("/delete")
    public HttpEntity<?> deleteFile(
            @RequestParam(name = "name") String name
    ) {
        String message = service.deleteFile(name);
        return ResponseEntity.ok(message);
    }

}
