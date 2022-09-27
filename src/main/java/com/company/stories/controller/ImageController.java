package com.company.stories.controller;

import com.company.stories.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/api/image")
@Slf4j
public class ImageController {

    private ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String uploadImage(@RequestParam MultipartFile image) throws IOException {
        log.info("Saving image");
        return imageService.saveImage(image);
    }

    @GetMapping(value = "/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] downloadImage(@PathVariable String imageName) throws IOException {
        log.info("Getting image: {}", imageName);
        //return Base64.getEncoder().encode(imageService.findImage(imageName));
        return imageService.findImage(imageName);
    }
}
