package com.example.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;

import com.example.dao.PresentationDao;
import com.example.dao.ProductDao;
import com.example.entities.Presentation;
import com.example.entities.Product;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductDao productDao;

    @Mock 
    private  PresentationDao presentationDao;

    @InjectMocks 
    private ProductServiceImpl productServiceImpl;

    private Presentation presentationPerUnits;
    private Presentation presentationPerDozens;

    private Product product0, product1;

    List<Product> productsList = new ArrayList<>();

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
                    .price(new BigDecimal(0))
                    .stock(0)
                    .productImage(null)
                    .presentation(presentationPerDozens)
                .build();

        productsList.add(product0);
        productsList.add(product1);

    }
    @Test
    void testDelete() {

    }

    @Test
    @DisplayName("Test para recuperar los 2 productos creados")
    void testFindAll() {

        // given
        given(productDao.findAll()).willReturn(productsList);

        // when
        List<Product> testResult = productServiceImpl.findAll();

        //then
        assertEquals(2, testResult.size());

    }

    @Test
    void testFindAll2() {

    }

    @Test
    void testFindAll3() {

    }

    @Test
    void testFindById() {

    }

    @Test
    @DisplayName("Test de servicio para persistir un producto")
    void testSave() {

        //given
        given(productDao.save(product0)).willReturn(product0);

        //when
        Product savedProduct = productServiceImpl.save(product0);

        //then
        assertThat(savedProduct).isNotNull();

    }

    @Test
    @DisplayName("Test que devuelve una lista de productos vacia")
    void testEmptyProductList(){

        //given
        given(productDao.findAll()).willReturn(Collections.emptyList());

        //when
        List<Product> testList = productServiceImpl.findAll();

        //then
        assertThat(testList).isEmpty();

    }
}
