package com.example.controllers;

import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.ArgumentMatchers.any;

import com.example.entities.Presentation;
import com.example.entities.Product;
import com.example.services.ProductService;
import com.example.utilities.FileDownloadUtil;
import com.example.utilities.FileUploadUtil;
import com.example.utilities.FileUtils;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@AutoConfigureMockMvc 
public class ProductControllerTest {

    @Autowired 
    MockMvc mockMvc;

    @MockitoBean 
    ProductService productService;

    @MockitoBean 
    FileUploadUtil fileUploadUtil;

    @MockitoBean 
    FileDownloadUtil fileDownloadUtil;

    @MockitoBean
    FileUtils fileUtils;

    @Autowired 
    ObjectMapper objectMapper;

    Presentation presentationPerUnits;
    Presentation presentationPerDozens;

    Product product0;
    Product product1;

    @BeforeEach 
    void setUp(){

        presentationPerUnits = Presentation.builder()
                    .name("unit")
                    .description("per Units")
                .build();
        
        presentationPerDozens = Presentation.builder()
                    .name("dozen")
                    .description("per Dozens")
                .build();

        product0 = Product.builder()
                    .name("NameTest0")
                    .description("DescriptionTest0")
                    .price(new BigDecimal(0))
                    .stock(0)
                    .productImage(null)
                    .presentation(presentationPerUnits)
                .build();

        product1 = Product.builder()
                    .name("NameTest1")
                    .description("DescriptionTest1")
                    .price(new BigDecimal(1))
                    .stock(1)
                    .productImage(null)
                    .presentation(presentationPerDozens)
                .build();

    }


    @Test 
    @DisplayName("Controller Test que devuelve todos los productos")
    void findAll() throws Exception{

        //given
        List<Product> productsList = new ArrayList<>();

        productsList.add(product0);
        productsList.add(product1);

        given(productService.findAll(Sort.by("name"))).willReturn(productsList);

        //when -> Realizar peticion HTTP mediante el metodo get al end point de products
        ResultActions response = mockMvc.perform(get("/products")
                .accept(MediaType.APPLICATION_JSON));


        //then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.Productos.size()", is(productsList.size())));

    }

    @Test 
    @DisplayName("Controller Test que persiste un producto")
    void testSaveProduct() throws Exception{

        //given
        given(productService.save(any(Product.class))).willAnswer(invocation -> invocation.getArgument(0));

        //when
        String jsonStringProduct = objectMapper.writeValueAsString(product0);

        MockMultipartFile bytesArrayProduct = new MockMultipartFile("product", 
                            null,
                            "application/json",
                            jsonStringProduct.getBytes());

        ResultActions response = this.mockMvc.perform(multipart("/products")
                        .file("image", null)
                        .file(bytesArrayProduct));

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$['Producto guardado: '].name", is(product0.getName())))
                .andExpect(jsonPath("$['Producto guardado: '].description", is(product0.getDescription())));

    }

    @Test
    @DisplayName("Cotroller test que recupera un producto por su id")
    void testFindById() throws Exception{

        //given
            int ProductId = 1;

            given(productService.findById(ProductId)).willReturn(product0);

        //when

            //Esta forma evita crear una variable response para realizar las acciones como andDo
            mockMvc.perform(get("/products/{id}", ProductId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$['Producto encontrado: '].name", is(product0.getName())));

    }

    @Test
    @DisplayName("Cotroller test para comprobar si no ha encontrado el producto")
    void testProductNotFound() throws Exception{

        //given
            int ProductId = 1;

            given(productService.findById(ProductId)).willReturn(null);

        //when

            mockMvc.perform(get("/products/{id}", ProductId))
                    .andExpect(status().isNotFound());

    }

}
