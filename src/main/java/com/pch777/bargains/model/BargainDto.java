package com.pch777.bargains.model;

import java.time.LocalDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.pch777.bargains.validation.CompareDate;
import com.pch777.bargains.validation.ComparePrices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ComparePrices(reducePriceField = "reducePrice", normalPriceField = "normalPrice")
@CompareDate(starBargainField = "startBargain", endBargainField  = "endBargain")
public class BargainDto {

	@NotBlank(message = "Title must not be blank")
	@Size(max = 90, message = "Title cannot be longer than 90 characters")
	private String title;

	private String description;

	@Min(value = 0, message = "Price should not be less than 0")
	private Double reducePrice;
	
	@Min(value = 0, message = "Price should not be less than 0")
	private Double normalPrice;
	
	@Min(value = 0, message = "Delivery should not be less than 0")
	private Double delivery;

	private String coupon;
	
	private String link;

	private Boolean closed;

	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private LocalDate startBargain;

	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private LocalDate endBargain;

	@NotNull(message = "Please choose a category of bargain")
	private Category category;

	private Shop shop;
	
}
