package org.kosa.shoppingmaillmanager.host.dashboard;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.kosa.shoppingmaillmanager.host.product.dto.MonthlySalesSummaryDto;
import org.kosa.shoppingmaillmanager.host.product.dto.OrderStatusCountDto;
import org.kosa.shoppingmaillmanager.host.product.dto.SalesCategoryDTO;
import org.kosa.shoppingmaillmanager.host.product.dto.SalesHostDTO;
import org.kosa.shoppingmaillmanager.host.product.dto.SalesOrderItemDto;
import org.kosa.shoppingmaillmanager.host.product.dto.SalesSummaryDto;
import org.kosa.shoppingmaillmanager.host.product.dto.TopProductDto;

@Mapper
public interface DashboardDAO {
	OrderStatusCountDto getOrderStatusCounts(@Param("hostId") String hostId);

	// 최근 30일 일별 매출 요약
	List<SalesSummaryDto> getDailySalesSummary(@Param("hostId") String hostId);

	// 최근 12개월 월별 매출 요약
	List<SalesSummaryDto> getMonthlySalesSummary(@Param("hostId") String hostId);
	
	// 해당월 매출 요약
	MonthlySalesSummaryDto getSalesSummaryCard(@Param("hostId") String hostId, @Param("month") String month);

	// 판매 수량 기준 TOP 5
    List<TopProductDto> getTop5ByQuantity(@Param("hostId") String hostId);

    // 매출 금액 기준 TOP 5
    List<TopProductDto> getTop5BySales(@Param("hostId") String hostId);
    
    Map<String, Integer> countPaymentMethods(@Param("userId")String userId);
	
 // ✅ 매출 상세 테이블용 주문 목록 조회
    List<SalesOrderItemDto> findSalesOrderItems(
        @Param("hostId") String hostId,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("productKeyword") String productKeyword,
        @Param("paymentMethodKeyword") String paymentMethodKeyword
    );

    // 최근 30일 전체 일별 매출 요약
	List<SalesSummaryDto> getWholeDailySalesSummary();

	// 최근 30일 전체 월별 매출 요약
	List<SalesSummaryDto> getWholeMonthlySalesSummary();

	List<SalesCategoryDTO> getDashboardCategorySales();

	List<SalesHostDTO> getDashboardHostSales();

	MonthlySalesSummaryDto getWholeSalesSummaryCard(@Param("month") String month);

	List<SalesOrderItemDto> findWholeSalesOrderItems(
	        @Param("startDate") String startDate,
	        @Param("endDate") String endDate,
	        @Param("productKeyword") String productKeyword,
	        @Param("paymentMethodKeyword") String paymentMethodKeyword
	    );

	List<TopProductDto> getWholeTop5ByQuantity();

	List<TopProductDto> getWholeTop5BySales();

	Map<String, Integer> countWholePaymentMethodCounts();
}



