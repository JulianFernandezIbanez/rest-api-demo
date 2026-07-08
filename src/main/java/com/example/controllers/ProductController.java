package com.example.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.entities.Product;
import com.example.models.FileUploadResponse;
import com.example.services.ProductService;
import com.example.utilities.FileDownloadUtil;
import com.example.utilities.FileUploadUtil;
import com.example.utilities.FileUtils;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

/**
* La anotacion @RestController es para que todos los metodos que van a ser
* creados dentro de este
* controlador y reciben peticiones a través del protocolo HTTP, mediante los
* verbos correspondientes
* (GET, POST, PUT, DELETE, PATCH, etc.) devuelvan o reciban datos en formato de
* JSON (JavaScript Object Notation)
*/
@RestController
/**
* Una API REST esta orientada al recurso, es decir, que el controlador necesita
* que se le especifique
* que recurso va a responder, por ejemplo en esto seria /productos, y en
* dependencia del verbo del protocolo
* HTTP se estaria haciendo una peticion (request) contreta. Por ejemplo: Si el
* verbo es GET, significa
* que estamos solicitando todos los productos al recurso /productos. Si el
* verbo es POST significa que queremos
* recibir un producto en formato JSON, en el cuerpo de la peticion (request) y
* persistirlo (guardarlo)
* en las tablas correspondientes
*/
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;
	private final FileUploadUtil fileUploadUtil;
	private final FileDownloadUtil fileDownloadUtil;
	private final FileUtils fileUtils;

	//Metodo que recibe una peticion para devolver un listado de todos los productos
	/*@GetMapping
	public List<Product> getProducts(){

		List<Product> allProducts = productService.findAll();

		return allProducts;

	}*/

	/**
    * 
    * IMPORTANTE!!!
    * 
    * Una API REST tiene que devolver informacion respecto a como ha sido
    * solucionada la peticion (request),
    * por ejemplo: el codigo 200 significa estado OK de la peticion, el codido 201
    * significaria CREATED,
    * el codigo 500 significaria que el servidor no ha podido cumplimentar la
    * peticion, el codigo 401 NO ENCONTRADO,
    * el codigo 403 prohibido, ect. Todos estos codigos se pueden encontrar en el
    * sitio de W3Schools
    * 
    * https://www.w3schools.com/tags/ref_httpmessages.asp
    * 
    */

	/**
     * El metodo siguiente va a responder a una peticion (request) del tipo:
     * 
     * http://localhost:8080/productos?page=0&size=3
     * 
     * Donde los parametros page y size seran utilizados para la paginacion, y no
     * seran requeridos, es decir,
     * que no son obligatorios que se suministren. Y en caso de NO ser suministrados
     * (page y size),
     * los productos se van a devolver ordenados.
     * 
     */

	@GetMapping
	public ResponseEntity<Map<String, Object>> getProducts(
		@RequestParam(name = "page", required = false) Integer page,
		@RequestParam(name = "size", required = false) Integer size
	){

		List<Product> products = null;
		Map<String, Object> responsAsMap = new HashMap<>();
		Sort sort = Sort.by("name");

		//Comprobar si en la peticion request me han suministrado los parametros page y size
		if (page != null && size != null) {
			
			Pageable pageable = PageRequest.of(page, size, sort);

			//Implica devolver los productos paginados, es decir, una pagina de product
			Page<Product> productPage = productService.findAll(pageable);

			products = productPage.getContent();

			responsAsMap.put("Productos", products);

		}else{

			//Devolver los productos ordenados por nombre, por ejemplo
			products = productService.findAll(sort);
			responsAsMap.put("Productos", products);

		}

		return new ResponseEntity<Map<String,Object>>(responsAsMap, HttpStatus.OK);

	}

	/**Metodo para recuperar un producto por el id que se recibe como un avariable en la ruta
	 *mediante un endpoint (URL o URI) que tiene el siguiente formato
	 *http://localhost:8080/products/1
	 *
	 *Donde el valor 1 es el id del producto
	 */

	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getProudctById(
		@PathVariable(name = "id", required = true) int Product_id
	){

		Map<String, Object> responsAsMap = new HashMap<>();
		ResponseEntity<Map<String,Object>> responseEntity = null;

		try {
			
			Product product = productService.findById(Product_id);

			if (product != null) {
				
				String successMessage = "El producto con el id "+ Product_id + " ha sido encontrado";
				responsAsMap.put("successMessage: ", successMessage);
				responsAsMap.put("Producto encontrado: ", product);
				responseEntity = new ResponseEntity<Map<String,Object>>(responsAsMap, HttpStatus.OK);

			} else {

				String failMessage = "Producto con id "+ Product_id +" no encontrado";
				responsAsMap.put("failMessage: ", failMessage);
				responseEntity = new ResponseEntity<Map<String,Object>>(responsAsMap, HttpStatus.NOT_FOUND);
			}

		} catch (DataAccessException e) {
			
			String errorMessage = "Error grave en la busqueda. Causa: "+e.getMostSpecificCause().getMessage();
			responsAsMap.put("errorMessage: ", errorMessage);
			responseEntity = new ResponseEntity<Map<String,Object>>(responsAsMap, HttpStatus.INTERNAL_SERVER_ERROR);	
		
		}

		return responseEntity;

	}

	//Metodo que guarda (persiste) un porducto por POST y que valida el JSON recibido para comprobar si esta bien formado
	//Para persistir imagenes primero hay que cambiar este metodo, ya que el producto ya no ocupa todo el cuerpo de la peticion
	//Sino que se divide en 2: El producto por una parte y por la otra la imagen
	//Muy importante no olvidarse de anotar este metodo y todos los que insertan, crean, eliminan reqistros en la BBDD con la anotacion @Transactional
	//Y tambien especificar el tipo de archivo que va a usar el metodo
	@PostMapping(consumes = "multipart/form-data")
	@Transactional
	public ResponseEntity<Map<String, Object>> saveProduct(
		@Valid
		@RequestPart Product product,
		BindingResult result,
		@RequestPart(name = "image", required = false) MultipartFile productImage
	) throws IOException{

		Map<String, Object> responsAsMap = new HashMap<>();
		ResponseEntity<Map<String,Object>> responseEntity = null;
		List<String> errorMessage = new ArrayList<>();

		//Comprobar si hay errores en el producto recibido
		if (result.hasErrors()) {
			 
			//Recuperar los errores del producto recibido para informar de ellos
			List<ObjectError> errors = result.getAllErrors(); 

			errors.stream().forEach(error -> {
				errorMessage.add(error.getDefaultMessage());
			});

			responsAsMap.put("Error en la inserccion. Causa: ", errorMessage);
			responsAsMap.put("Producto mal formado ", product);
			responseEntity = new ResponseEntity<Map<String,Object>>(responsAsMap, HttpStatus.BAD_REQUEST);

			return responseEntity;

		}

		//Guardamos el producto para persistirlo, puesto que no tiene errores
		//Antes comprobar si se recibe una imagen del producto para guardarla en el sistema de archivos

		if (productImage != null && !productImage.isEmpty()) {
			
			/**
			 * Para guardar la imagen del producto, primero hay que agregar como prefijo un codigo
			 * alfanumerico generado aleatoriamente a partir de un metodo que se encuentra
			 * en la biblioteca Apache Commons text, que hay que descargar la dependencia 
			 * desde el repositorio central de Maven para integrarla en el pom.xml
			 */

			/**
			 * Para la gestion de archivos vamos a crear un componente (@Component) en un paquete
			 * que podria ser com.example.utilities , formado por un metodo para poder guardar
			 * la imagen recibida en una carpeta del sistema de archivos y devolver un codigo alfanumerico
			 * generado aleatoriamente, que kkevara como prefijo el nombre dek fichero de imagen recibido
			 * 
			 * Se hara uso intensivo de NIO 2 y se comprobara si la carpeta existe o no para crearla 
			 */

			String fileCode = fileUploadUtil.saveFile(productImage.getOriginalFilename(), productImage);
			product.setProductImage(fileCode + "-" + productImage.getOriginalFilename());

			/**
			 * Como es una API REST hay qye devolver informacion al que ha realizado la request 
			 * con respecto a la imagen recibida, para lo cual vamos a crear en el paquete models un Record
			 * donde devolveremos la informacion de la imagen subida
			 */

			FileUploadResponse fileUploadResponse = new FileUploadResponse(
				fileCode+ "-" +productImage.getOriginalFilename(), 
				"products/fileDownload", 
				productImage.getSize()
			); 

			responsAsMap.put("Informacion de la imagen del producto", fileUploadResponse);

		}

		try {
			
			Product savedProduct = productService.save(product);
			responsAsMap.put("Mensaje :", "Producto integrado correctamente");
			responsAsMap.put("Producto guardado: ", savedProduct);
			responseEntity = new ResponseEntity<Map<String,Object>>(responsAsMap, HttpStatus.CREATED);

		} catch (DataAccessException e) {
			String ServerErrorMessage = "Error grave en la inserccion. Causa: "+e.getMostSpecificCause().getMessage();
			responsAsMap.put("errorMessage: ", ServerErrorMessage);
			responseEntity = new ResponseEntity<Map<String,Object>>(responsAsMap, HttpStatus.INTERNAL_SERVER_ERROR);	
		}

		return responseEntity;

	}

	/**
	* Metodo que recupera la imagen de un producto, dado el codigo que 
	* tiene como prefijo el nombre de la imagen
	*/

	@GetMapping("/fileDownload/{fileCode}")
	public ResponseEntity<?> downloadFile(@PathVariable String fileCode){

		Resource resource = null;

		try {
			
			resource = fileDownloadUtil.getFileAsResource(fileCode);

		} catch (IOException ioe) {

			return ResponseEntity
				.internalServerError()
				.build();

		}

		if (resource == null) {
			
			return new ResponseEntity<>("Archivo no encontrado", HttpStatus.NOT_FOUND);

		}

		/**
		* Si estamos en este punto quiere decir que el fichero (imagen del producto) ha sido
		* encontrado y podemos enviarlo como respuesta a la peticion, como un fichero adjunto
		* en el cuerpo de la respuesta */

		String contentType = "application/octet-stream";
		String headerValue = "attachment; fileName=\"" + resource.getFilename() + "\"";

		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(contentType))
			.header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
			.body(resource);

	}

	//Metodo que actualiza un producto cuyo id se recibe en la peticion
	//conjuntamente con el JSON del producto y la imagen del producto
	//que no es requerida. El metodo es practicamente igual al que persiste
	//un producto con la imagen recibida
	@PutMapping(value = "/{id}",consumes = "multipart/form-data")
	@Transactional
	public ResponseEntity<Map<String, Object>> updateProduct(
		@Valid
		@RequestPart Product product,
		BindingResult result,
		@RequestPart(name = "image", required = false) MultipartFile productImage,
		@PathVariable(name = "id", required = true) int Product_id
	) throws IOException{

		Map<String, Object> responsAsMap = new HashMap<>();
		ResponseEntity<Map<String,Object>> responseEntity = null;
		List<String> errorMessage = new ArrayList<>();

		//Comprobar si hay errores en el producto recibido
		if (result.hasErrors()) {
			
			//Recuperar los errores del producto recibido para informar de ellos
			List<ObjectError> errors = result.getAllErrors(); 

			errors.stream().forEach(error -> {
				errorMessage.add(error.getDefaultMessage());
			});

			responsAsMap.put("Error en la inserccion. Causa: ", errorMessage);
			responsAsMap.put("Producto mal formado ", product);
			responseEntity = new ResponseEntity<Map<String,Object>>(responsAsMap, HttpStatus.BAD_REQUEST);

			return responseEntity;

		}

		//Actualizamos el producto para persistirlo, puesto que no tiene errores
		//Antes comprobar si se recibe una imagen del producto para actualizarla
		//en cuyo caso debemos eliminar la imagen asociada con el producto guardado,
		//borrandola en el sistema de archivos

		Product oldProduct = productService.findById(Product_id);

		if (oldProduct == null) {

			String failMessage = "Producto con id "+ Product_id +" no encontrado";
			responsAsMap.put("failMessage: ", failMessage);
			return new ResponseEntity<Map<String,Object>>(responsAsMap, HttpStatus.NOT_FOUND);

		}

		if (productImage != null && !productImage.isEmpty()) {

			if (oldProduct.getProductImage() != null) {
				
				// Eliminar la imagen asociada al producto guardado
				// para lo cual vamos a necesitar de un metodo, en un componente,
				// que reciba el nombre del fichero de imagen y lo busque
				// en la carpeta a donde hemos subido las imagenes, y lo elimine
				fileUtils.deleteFile(oldProduct.getProductImage());

			}

			String fileCode = fileUploadUtil.saveFile(productImage.getOriginalFilename(), productImage);
			product.setProductImage(fileCode + "-" + productImage.getOriginalFilename());

			FileUploadResponse fileUploadResponse = new FileUploadResponse(
				fileCode+ "-" +productImage.getOriginalFilename(), 
				"products/fileDownload", 
				productImage.getSize()
			); 

			responsAsMap.put("Informacion de la imagen del producto", fileUploadResponse);

		}

		try {

			product.setId(Product_id);
			Product savedProduct = productService.save(product);
			responsAsMap.put("Mensaje :", "Producto actualizado correctamente");
			responsAsMap.put("Producto guardado: ", savedProduct);
			responseEntity = new ResponseEntity<Map<String,Object>>(responsAsMap, HttpStatus.OK);

		} catch (DataAccessException e) {
			String ServerErrorMessage = "Error grave en la actualizacion. Causa: "+e.getMostSpecificCause().getMessage();
			responsAsMap.put("errorMessage: ", ServerErrorMessage);
			responseEntity = new ResponseEntity<Map<String,Object>>(responsAsMap, HttpStatus.INTERNAL_SERVER_ERROR);	
		}

		return responseEntity;

	}


}
