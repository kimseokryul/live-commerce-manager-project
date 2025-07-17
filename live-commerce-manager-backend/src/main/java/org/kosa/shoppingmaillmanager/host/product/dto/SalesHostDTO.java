package org.kosa.shoppingmaillmanager.host.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesHostDTO {
	private int host_id;
	private String user_id;
	private String nickname;
	private int total_price;
}
