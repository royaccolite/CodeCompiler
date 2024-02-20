package com.accolite.app.service;

import com.accolite.app.dto.FileNodeDTO;
import java.io.File;
import java.util.List;
import java.util.Map;

public interface FileExplorerService {
    public List<FileNodeDTO> getProjectStructure(String projectPath);
    public Map<String, String> getAllFileContent(Map<String, String> requestData, String projectPath);
    }
