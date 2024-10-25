package com.example.myflower.dto.file;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDTO {
    private Integer id;
    private String url;
}
