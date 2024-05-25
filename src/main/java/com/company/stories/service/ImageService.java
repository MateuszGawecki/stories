package com.company.stories.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
@Slf4j
public class ImageService {

    @Value("${IMAGE_FOLDER}")
    String IMAGE_FOLDER_PATH;
    String DEFAULT_IMAGE = "defaultImage.png";

    public String saveImage(MultipartFile image) throws IOException {
        String newName = System.currentTimeMillis() + image.getOriginalFilename();
        Path filePath = Paths.get(IMAGE_FOLDER_PATH + newName);
        image.transferTo(filePath);
        return newName;
    }

    public String replaceImage(MultipartFile image, String imageName) throws IOException {
        String fileName = IMAGE_FOLDER_PATH + imageName;
        log.error(fileName);
        File f = new File(fileName);
        var x = f.delete();
        log.error("" + x);
        return saveImage(image);
    }

    public byte[] findImage(String imageName) {
        try {
            Path filePath = Paths.get(IMAGE_FOLDER_PATH + imageName);
            return IOUtils.toByteArray(filePath.toUri());
        } catch (FileNotFoundException ex) {
            log.error("File Not Found {}", IMAGE_FOLDER_PATH+imageName);
            return findImage(DEFAULT_IMAGE);
        } catch (IOException ex) {
            log.error("IO Exception occurred during reading file {}", IMAGE_FOLDER_PATH+imageName);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "IO Exception occurred during reading file");
        }
    }
}
