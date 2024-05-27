package com.company.stories.controller;

import com.company.stories.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import java.util.Objects;

@RestController
@RequestMapping("/api/images")
@Slf4j
@Tag(name = "Images", description = "Endpoints for managing images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Operation(summary = "Upload image on server disc")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String uploadImage(@RequestParam MultipartFile image, @RequestParam(required = false) String imageName) throws IOException {
        log.info("Saving image");
        String contentType = image.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Must specify MIME type of the file");
        }

        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
            throw new IllegalArgumentException("Only images are allowed");
        }

        if(imageName != null)
            return imageService.replaceImage(image, imageName);
        else
            return imageService.saveImage(image);
    }

    @Operation(summary = "Get image by it's name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Image not found"),
            @ApiResponse(responseCode = "500")
    })
    @GetMapping(value = "/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] downloadImage(@PathVariable String imageName) throws IOException {
        log.info("Getting image: {}", imageName);
        return imageService.findImage(imageName);
    }
}
