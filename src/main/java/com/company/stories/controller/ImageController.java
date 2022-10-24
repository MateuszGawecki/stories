package com.company.stories.controller;

import com.company.stories.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@RestController
@RequestMapping("/api/image")
@Slf4j
@Tag(name = "Images", description = "Endpoints for managing images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Operation(summary = "Upload image on server disc")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String uploadImage(@RequestParam MultipartFile image) throws IOException {
        log.info("Saving image");
        return imageService.saveImage(image);
    }

    @Operation(summary = "Get image by it's name")
    @GetMapping(value = "/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] downloadImage(@PathVariable String imageName) throws IOException {
        log.info("Getting image: {}", imageName);
        return imageService.findImage(imageName);
    }
}
