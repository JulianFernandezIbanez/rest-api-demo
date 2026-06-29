package com.example.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotNull(message = "El producto tiene que tener un nombre")
	@NotEmpty(message = "El nombre del producto no puede estar vacio")
	@Size(min = 4, max = 25, message = "No cumple con los requisitos (minimo 4 y maximo 25 caracteres)")
	private String name;

	@NotNull(message = "El producto tiene que tener una descripcion")
	@NotBlank(message = "La descripcion del producto no puede estar compuesta unicamente de espacios")
	@Size(max = 45, message = "No puede superar el maximo de 45 caracteres")
	private String description;

	@Min(value = 0, message = "Las existencias del producto no pueden ser negativas")
	private int stock;

	@Min(value = 0, message = "El precio del producto no puede ser negativos")
	private BigDecimal price;

	@NotNull(message = "La presentación del producto es requerida")
	@ManyToOne(fetch = FetchType.LAZY)
	private Presentation presentation;

}
