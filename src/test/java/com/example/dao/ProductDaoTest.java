package com.example.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;

import com.example.entities.Presentation;
import com.example.entities.Product;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ProductDaoTest {

    @Autowired
    private PresentationDao presentationDao;

    @Autowired 
    private ProductDao productDao;

    private Presentation presentationPerUnits;
    private Presentation presentationPerDozens;

    private Product product0;

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

    }

    @Test
    @DisplayName("Test para probar a persistir un producto")
    void testSaveProduct(){

        //given
        Presentation presentation0 = presentationDao.save(presentationPerUnits);

        product0 = Product.builder()
                    .name("NameTest")
                    .description("DescriptionTest")
                    .price(new BigDecimal(0))
                    .presentation(presentation0)
                .build();

        //when
        Product savedTestProduct = productDao.save(product0);

        //then
        assertThat(savedTestProduct).isNotNull();
        assertThat(savedTestProduct.getId()).isGreaterThan(0);

    }

    @Test
    void testFindAll() {

    }

    @Test
    void testFindAll2() {

    }

    @Test
    void testFindById() {

    }
}
