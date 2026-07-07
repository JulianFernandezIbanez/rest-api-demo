package com.example.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.text.RandomStringGenerator;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploadUtil {

	public String saveFile(String fileName, MultipartFile multipartFile) throws IOException{

		String FileCode;

		//Definir la ruta donde se va a guardar el archivo recibido, es decir, la imagen
		Path uploadPath = Paths.get("Files-Upload");

		//Comprobacion de la ruta, si no existe se crea
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		//Generacion del codigo alfanumerico de 8 caracteres
		RandomStringGenerator generator = RandomStringGenerator.builder()
			.withinRange('0', 'z')
			.filteredBy(Character::isLetterOrDigit)
		.get();
		FileCode = generator.generate(8);

		try (InputStream fileInputStream = multipartFile.getInputStream()) {

			Path destination = uploadPath.resolve(FileCode + "-" + fileName);
			Files.copy(fileInputStream, destination, StandardCopyOption.REPLACE_EXISTING);
			
		} catch (IOException ioe) {

			throw new IOException("Error a la hora de guardar la imagen", ioe);

		}

		return FileCode;

	}

}
