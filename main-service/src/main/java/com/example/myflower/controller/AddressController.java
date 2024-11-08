package com.example.myflower.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.address.requests.AddressRequestDTO;
import com.example.myflower.dto.address.responses.AddressResponseDTO;
import com.example.myflower.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@CrossOrigin("**")
public class AddressController {
    @Autowired
    private AddressService addressService;
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<BaseResponseDTO> createAddress(@RequestBody AddressRequestDTO requestDTO) {
        try {
            AddressResponseDTO addressResponseDTO = addressService.createAddress(requestDTO);
            BaseResponseDTO response = BaseResponseDTO.builder()
                    .message("Address created successfully")
                    .success(true)
                    .code(HttpStatus.CREATED.value())
                    .data(addressResponseDTO)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            BaseResponseDTO response = BaseResponseDTO.builder()
                    .message(e.getMessage())
                    .success(false)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .data(null)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO> getAddressById(@PathVariable Integer id) {
        try {
            AddressResponseDTO addressResponseDTO = addressService.getAddressById(id);
            BaseResponseDTO response = BaseResponseDTO.builder()
                    .message("Address found")
                    .success(true)
                    .code(HttpStatus.OK.value())
                    .data(addressResponseDTO)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            BaseResponseDTO response = BaseResponseDTO.builder()
                    .message(e.getMessage())
                    .success(false)
                    .code(HttpStatus.NOT_FOUND.value())
                    .data(null)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/by-account")
    public ResponseEntity<BaseResponseDTO> getAllByAccount() {
        try {
            List<AddressResponseDTO> addressResponseDTOList = addressService.getAllByAccount();
            BaseResponseDTO response = BaseResponseDTO.builder()
                    .message("Addresses found")
                    .success(true)
                    .code(HttpStatus.OK.value())
                    .data(addressResponseDTOList)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            BaseResponseDTO response = BaseResponseDTO.builder()
                    .message(e.getMessage())
                    .success(false)
                    .code(HttpStatus.NOT_FOUND.value())
                    .data(null)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponseDTO> updateAddress(@PathVariable Integer id, @RequestBody AddressRequestDTO requestDTO) {
        try {
            AddressResponseDTO addressResponseDTO = addressService.updateAddress(id, requestDTO);
            BaseResponseDTO response = BaseResponseDTO.builder()
                    .message("Address updated successfully")
                    .success(true)
                    .code(HttpStatus.OK.value())
                    .data(addressResponseDTO)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            BaseResponseDTO response = BaseResponseDTO.builder()
                    .message(e.getMessage())
                    .success(false)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .data(null)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponseDTO> deleteAddress(@PathVariable Integer id) {
        try {
            addressService.deleteAddress(id);
            BaseResponseDTO response = BaseResponseDTO.builder()
                    .message("Address deleted successfully")
                    .success(true)
                    .code(HttpStatus.NO_CONTENT.value())
                    .data(null)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            BaseResponseDTO response = BaseResponseDTO.builder()
                    .message(e.getMessage())
                    .success(false)
                    .code(HttpStatus.NOT_FOUND.value())
                    .data(null)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
