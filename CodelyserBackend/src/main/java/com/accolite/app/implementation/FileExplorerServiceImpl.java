package com.accolite.app.implementation;


import com.accolite.app.dto.FileNodeDTO;
import com.accolite.app.exception.FileException;
import com.accolite.app.service.FileExplorerService;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class FileExplorerServiceImpl implements FileExplorerService {
    private static final Logger logger = LoggerFactory.getLogger(FileExplorerServiceImpl.class);
    @Override
    public List<FileNodeDTO> getProjectStructure(String projectPath) {
        File srcDirectory = new File(projectPath, "src");
        File readmeFile = new File(projectPath, "README.md");

        List<FileNodeDTO> children = new ArrayList<>();

        if (srcDirectory.exists() && srcDirectory.isDirectory()) {
            FileNodeDTO srcNode = createFileNode(srcDirectory, projectPath);
            srcNode.setChildren(getFiles(srcDirectory, projectPath));
            children.add(srcNode);
        }

        if (readmeFile.exists() && readmeFile.isFile()) {
            FileNodeDTO readmeNode = createFileNode(readmeFile, projectPath);
            children.add(readmeNode);
        }

        return children;
    }

    public List<FileNodeDTO> getFiles(File directory, String projectPath) {
        List<FileNodeDTO> files = new ArrayList<>();
        File[] fileArray = directory.listFiles();

        if (fileArray != null) {
            for (File file : fileArray) {
                FileNodeDTO fileNode = createFileNode(file,projectPath);
                if (file.isDirectory()) {
                    fileNode.setChildren(getFiles(file, projectPath));
                }
                files.add(fileNode);
            }
        }

        return files;
    }

    private FileNodeDTO createFileNode(File file, String projectPath) {
        FileNodeDTO fileNode = new FileNodeDTO();
        fileNode.setName(file.getName());

        // Calculate a relative path by removing the projectPath prefix
        String relativePath = file.getAbsolutePath().replace(projectPath, "");
        fileNode.setRelativePath(relativePath);

        fileNode.setIsFile(file.isFile());
        return fileNode;
    }

    @Override
    public Map<String, String> getAllFileContent(Map<String, String> requestData, String projectPath) {
        String filePath = requestData.get("filepath");
        try {
            Path fullPath = Paths.get(projectPath, filePath);
            File file = fullPath.toFile();

            if (!file.exists() || !file.isFile()) {
                throw new FileNotFoundException();
            }

            String content = new String(Files.readAllBytes(file.toPath()));
            return Collections.singletonMap(file.getName(), content);
        }
        catch(Exception e){
            logger.error(e.getMessage());
            throw new FileException("Runtime Error: "+e.getMessage());
        }
    }
}

