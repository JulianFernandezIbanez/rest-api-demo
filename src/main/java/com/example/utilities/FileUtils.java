package com.example.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

@Component
public class FileUtils {

    public void deleteFile(String fileName){

        Path uploadPath = Paths.get("Files-Upload");
        Path fileNamePath = uploadPath.resolve(fileName);

        try {
            Files.deleteIfExists(fileNamePath);

        } catch (IOException ioe) {
            
            throw new RuntimeException("Archivo no eliminado. Causa: "+ ioe);

        }

    }

}
