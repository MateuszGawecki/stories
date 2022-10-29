package com.company.stories.service;

import com.company.stories.exception.image.ImageNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
@Slf4j
public class ImageService {
    String IMAGE_FOLDER_PATH = "D:\\upload\\";

    public String saveImage(MultipartFile image) throws IOException {
        String newName = System.currentTimeMillis() + image.getOriginalFilename();
        Path filePath = Paths.get(IMAGE_FOLDER_PATH + newName);
        image.transferTo(filePath);
        return newName;
    }

    public byte[] findImage(String imageName) {
        try {
            Path filePath = Paths.get(IMAGE_FOLDER_PATH + imageName);
            return IOUtils.toByteArray(filePath.toUri());
        } catch (FileNotFoundException ex) {
            log.error("File Not Found {}", IMAGE_FOLDER_PATH+imageName);
            throw new ImageNotFoundException(String.format("File with name %s not found", imageName));
        } catch (IOException ex) {
            log.error("IO Exception occurred during reading file {}", IMAGE_FOLDER_PATH+imageName);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "IO Exception occurred during reading file");
        }
    }
}
