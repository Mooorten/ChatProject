package com.example.chatproject;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

@Controller
public class FileController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploaded_files/";

    @PostMapping("/upload-file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("No file uploaded", HttpStatus.BAD_REQUEST);
        }

        try {
            File uploadFolder = new File(UPLOAD_DIR);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            File destinationFile = new File(UPLOAD_DIR + file.getOriginalFilename());
            file.transferTo(destinationFile);

            return new ResponseEntity<>("File uploaded successfully: " + file.getOriginalFilename(), HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("File upload failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download-file")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename) {
        File file = new File(UPLOAD_DIR + filename);

        if (file.exists()) {
            Resource resource = new FileSystemResource(file);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}