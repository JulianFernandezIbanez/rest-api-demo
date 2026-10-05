package com.example;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.entities.Presentation;
import com.example.entities.Product;
import com.example.services.PresentationService;
import com.example.services.ProductService;
import com.example.spring_security_jwt.model.ERole;
import com.example.spring_security_jwt.model.Role;
import com.example.spring_security_jwt.model.User;
import com.example.spring_security_jwt.repository.RoleRepository;
import com.example.spring_security_jwt.repository.UserRepository;

@Configuration
public class CreatesSamplesData {

    @Bean
	CommandLineRunner sampleData(ProductService productService, 
        PresentationService presentationService, UserRepository userRepository, 
		RoleRepository roleRepository,
        PasswordEncoder passwordEncoder) {

        return args -> {

			//Crear 2 presentaciones por unidad y por decenas para los productos
			presentationService.save(Presentation.builder()
				.name("unidad")
				.description("por unidades")
				.build()
			);

			presentationService.save(Presentation.builder()
				.name("decenas")
				.description("por decenas")
				.build()
			);

			//Crear varios productos para las presentaciones anteriores
			productService.save(Product.builder()
				.name("resma de papel")
				.description("Description")
				.stock(10)
				.price( new BigDecimal(3.75))
				.presentation(presentationService.findById(1))
				.build()
			);

			productService.save(Product.builder()
				.name("Cartas")
				.description("Description")
				.stock(10)
				.price( new BigDecimal(1.00))
				.presentation(presentationService.findById(2))
				.build()
			);

			productService.save(Product.builder()
				.name("Guitarra de Juguete")
				.description("Description")
				.stock(5)
				.price( new BigDecimal(4.5))
				.presentation(presentationService.findById(1))
				.build()
			);

			productService.save(Product.builder()
				.name("Teclado de ordenador")
				.description("Description")
				.stock(5)
				.price( new BigDecimal(15.00))
				.presentation(presentationService.findById(1))
				.build()
			);

			productService.save(Product.builder()
				.name("Teclado para portatil")
				.description("Description")
				.stock(5)
				.price( new BigDecimal(40.00))
				.presentation(presentationService.findById(1))
				.build()
			);

			productService.save(Product.builder()
				.name("Altavoces bluetooth")
				.description("Description")
				.stock(5)
				.price( new BigDecimal(15.00))
				.presentation(presentationService.findById(1))
				.build()
			);

			productService.save(Product.builder()
				.name("Lapices 2b")
				.description("Description")
				.stock(4)
				.price( new BigDecimal(1.50))
				.presentation(presentationService.findById(2))
				.build()
			);

			productService.save(Product.builder()
				.name("Boligrafos ")
				.description("De color azul")
				.stock(10)
				.price( new BigDecimal(2.00))
				.presentation(presentationService.findById(1))
				.build()
			);

			productService.save(Product.builder()
				.name("Monitor")
				.description("De 15 pulgadas")
				.stock(5)
				.price( new BigDecimal(40.00))
				.presentation(presentationService.findById(1))
				.build()
			);

			productService.save(Product.builder()
				.name("Cargador movil")
				.description("Para telefonos Samsung")
				.stock(10)
				.price( new BigDecimal(10.00))
				.presentation(presentationService.findById(1))
				.build()
			);

			productService.save(Product.builder()
				.name("Raton de ordenador")
				.description("Para ordenadores apple")
				.stock(100)
				.price( new BigDecimal(40.00))
				.presentation(presentationService.findById(1))
				.build()
			);

			Role userRole = roleRepository.save(Role.builder().name(ERole.ROLE_USER).build());
            Role adminRole = roleRepository.save(Role.builder().name(ERole.ROLE_ADMIN).build());
            
            /**
             * Agregamos usuarios con los roles creados, para realizar test de integracion a los 
             * endpoints
             */
            
            userRepository.save(User.builder()
            		.username("admin1")
            		.email("admin1@gmail.com")
            		.roles(Set.of(adminRole))
            		.password(passwordEncoder.encode("Temp2026$$##"))
            		.build());
            
            userRepository.save(User.builder()
            		.username("user1")
            		.email("user1@gmail.com")
            		.roles(Set.of(userRole))
            		.password(passwordEncoder.encode("Temp2026$$##"))
            		.build());

        };

    }

}
