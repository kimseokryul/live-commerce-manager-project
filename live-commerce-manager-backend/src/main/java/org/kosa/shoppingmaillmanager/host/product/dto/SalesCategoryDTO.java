package org.kosa.shoppingmaillmanager.host.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesCategoryDTO {
	private int category_id;
	private String name;
	private int product_id;
	private int total_price;
}
