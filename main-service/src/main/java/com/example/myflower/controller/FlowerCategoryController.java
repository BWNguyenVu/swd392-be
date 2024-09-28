package com.example.myflower.controller;

import com.example.myflower.dto.flowercategogy.request.CreateFlowerCategoryRequestDTO;
import com.example.myflower.dto.flowercategogy.request.UpdateFlowerCategoryRequestDTO;
import com.example.myflower.dto.flowercategogy.response.FlowerCategoryResponseDTO;
import com.example.myflower.service.FlowerCategoryService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/flower-categories")
@RequiredArgsConstructor
public class FlowerCategoryController {
    @NonNull
    private FlowerCategoryService flowerCategoryService;

    @PostMapping
    public ResponseEntity<FlowerCategoryResponseDTO> createCategory(@RequestBody CreateFlowerCategoryRequestDTO requestDTO) {
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

    @PutMapping("/{id}")
    public ResponseEntity<FlowerCategoryResponseDTO> updateCategoryById(@PathVariable Integer id, @RequestBody UpdateFlowerCategoryRequestDTO requestDTO) {
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
}