package com.example.myflower.controller;

import com.example.myflower.dto.flowercategogy.request.CreateFlowerCategoryRequestDTO;
import com.example.myflower.dto.flowercategogy.request.UpdateFlowerCategoryRequestDTO;
import com.example.myflower.dto.flowercategogy.response.FlowerCategoryResponseDTO;
import com.example.myflower.service.FlowerCategoryService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flower-categories")
@RequiredArgsConstructor
@CrossOrigin("**")
public class FlowerCategoryController {
    @NonNull
    private FlowerCategoryService flowerCategoryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FlowerCategoryResponseDTO> createCategory(@Valid @ModelAttribute CreateFlowerCategoryRequestDTO requestDTO) {
        return ResponseEntity.ok().body(flowerCategoryService.createFlowerCategory(requestDTO));
    }

    @GetMapping
    public ResponseEntity<List<FlowerCategoryResponseDTO>> getAllCategory() {
        return ResponseEntity.ok().body(flowerCategoryService.getAllFlowerCategory());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlowerCategoryResponseDTO> getCategoryById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(flowerCategoryService.getFlowerCategoryById(id));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FlowerCategoryResponseDTO> updateCategoryById(@PathVariable Integer id, @Valid @ModelAttribute UpdateFlowerCategoryRequestDTO requestDTO) {
        return ResponseEntity.ok().body(flowerCategoryService.updateFlowerCategoryById(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FlowerCategoryResponseDTO> deleteCategoryById(@PathVariable Integer id) {
        flowerCategoryService.deleteFlowerCategoryById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<FlowerCategoryResponseDTO> restoreCategoryById(@PathVariable Integer id) {
        flowerCategoryService.restoreFlowerCategoryById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/clear-cache")
    public ResponseEntity<Void> clearCategoryCache() {
        flowerCategoryService.clearCategoryCache();
        return ResponseEntity.noContent().build();
    }
}