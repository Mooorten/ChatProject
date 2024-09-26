package com.example.chatproject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class FileController {

    @PostMapping("/upload-file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("No file uploaded", HttpStatus.BAD_REQUEST);
        }

        try {
            // Define the directory to which files should be uploaded
            String uploadDir = System.getProperty("user.dir") + "/uploaded_files/";
            File uploadFolder = new File(uploadDir);

            // Create the directory if it does not exist
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            // Define the file destination
            File destinationFile = new File(uploadDir + file.getOriginalFilename());

            // Transfer the file to the destination
            file.transferTo(destinationFile);

            return new ResponseEntity<>("File uploaded successfully: " + file.getOriginalFilename(), HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("File upload failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}