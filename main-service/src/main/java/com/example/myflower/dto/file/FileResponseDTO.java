package com.example.myflower.dto.file;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDTO implements Serializable {
    private Integer id;
    private String fileName;
    private String url;
}
