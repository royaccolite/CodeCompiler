package com.accolite.app.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class FileNodeDTO {
    private String name;
    private String relativePath;
    private Boolean isFile;
    private List<FileNodeDTO> children;

}
