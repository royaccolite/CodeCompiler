package com.accolite.app.controller;

import com.accolite.app.dto.FileNodeDTO;
import com.accolite.app.service.FileExplorerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidate/project-structure")
@CrossOrigin(origins = "http://localhost:4200")
public class FolderStructureController {
    private final String projectPath = "C:\\Codelyser\\CandidateDummy";
    @Autowired
    FileExplorerService fileExplorerService;


    @GetMapping()
    public ResponseEntity<List<FileNodeDTO>> getProjectStructure() {
        try{
            List<FileNodeDTO> children = fileExplorerService.getProjectStructure(projectPath);
            return new ResponseEntity<>(children, HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/explorer/file-content")
    public ResponseEntity<Map<String, String>> getAllFileContent(@RequestBody Map<String, String> requestData) {
        try {
            Map<String, String> response = fileExplorerService.getAllFileContent(requestData,projectPath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("content", "Error reading file content."+e.getMessage()));
        }
    }
}
