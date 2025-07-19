package org.kosa.shoppingmaillmanager.host.dashboard;

import java.util.List;
import java.util.Map;

import org.kosa.shoppingmaillmanager.host.product.dto.MonthlySalesSummaryDto;
import org.kosa.shoppingmaillmanager.host.product.dto.OrderStatusCountDto;
import org.kosa.shoppingmaillmanager.host.product.dto.SalesCategoryDTO;
import org.kosa.shoppingmaillmanager.host.product.dto.SalesHostDTO;
import org.kosa.shoppingmaillmanager.host.product.dto.SalesOrderItemDto;
import org.kosa.shoppingmaillmanager.host.product.dto.SalesSummaryDto;
import org.kosa.shoppingmaillmanager.host.product.dto.TopProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(name = "매출 현황 및 대시보드 API", description = "판매자 대시보드 및 매출 통계 관련 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

	private final DashboardService dashboardService;

	@Operation(summary = "주문 상태별 통계 조회", description = "신규 주문, 취소, 반품 등의 상태별 주문 수를 반환합니다.")
	@GetMapping("/order-status")
	public OrderStatusCountDto getOrderStatus(HttpServletRequest request) {
		String userId = (String) request.getAttribute("userId");

		return dashboardService.getOrderStatusCounts(userId);
	}

	// ✅ 최근 30일 일별 매출
	@Operation(summary = "일별 매출 통계", description = "최근 30일 동안의 일별 매출 요약을 조회합니다.")
	@GetMapping("/sales/daily")
	public ResponseEntity<List<SalesSummaryDto>> getDailySales(HttpServletRequest request) {
		String userId = (String) request.getAttribute("userId");
		List<SalesSummaryDto> result = dashboardService.getDailySalesSummary(userId);
		return ResponseEntity.ok(result);
	}

	// ✅ 최근 12개월 월별 매출
	@Operation(summary = "월별 매출 통계", description = "최근 12개월 동안의 월별 매출 요약을 조회합니다.")
	@GetMapping("/sales/monthly")
	public ResponseEntity<List<SalesSummaryDto>> getMonthlySales(HttpServletRequest request) {
		String userId = (String) request.getAttribute("userId");
		List<SalesSummaryDto> result = dashboardService.getMonthlySalesSummary(userId);
		return ResponseEntity.ok(result);
	}

	@Operation(summary = "매출 요약 카드", description = "특정 월 기준으로 총 주문 수, 총 매출, 평균 결제 금액을 반환합니다.")
	@GetMapping("/sales/summary-card")
	public MonthlySalesSummaryDto getSalesSummaryCard(@RequestParam String month, HttpServletRequest request) {
		String userId = (String) request.getAttribute("userId");
		return dashboardService.getSalesSummaryCard(userId, month);
	}

	@Operation(summary = "TOP5 상품 (판매 수량 기준)", description = "판매 수량 기준 상위 5개 상품을 조회합니다.")
	@GetMapping("/sales/top-products/quantity")
	public ResponseEntity<List<TopProductDto>> getTopProductsByQuantity(HttpServletRequest request) {
		String userId = (String) request.getAttribute("userId");
		List<TopProductDto> topProducts = dashboardService.getTop5ProductsByQuantity(userId);
		return ResponseEntity.ok(topProducts);
	}

	@Operation(summary = "TOP5 상품 (매출 금액 기준)", description = "매출 금액 기준 상위 5개 상품을 조회합니다.")
	@GetMapping("/sales/top-products/sales")
	public ResponseEntity<List<TopProductDto>> getTopProductsBySales(HttpServletRequest request) {
		String userId = (String) request.getAttribute("userId");
		List<TopProductDto> topProducts = dashboardService.getTop5ProductsBySales(userId);
		return ResponseEntity.ok(topProducts);
	}
	
	@Operation(summary = "결제 수단별 매출 비율", description = "카드, 휴대폰, 페이 등 결제 수단별 매출 비율을 반환합니다.")
	@GetMapping("/payment-method-chart")
	public ResponseEntity<?> getPaymentMethodChart(HttpServletRequest request) {
		String userId = (String) request.getAttribute("userId");

		Map<String, Integer> result = dashboardService.getPaymentMethodCounts(userId);
		return ResponseEntity.ok(result);
	}

	@Operation(summary = "매출 상세 주문 항목 조회", description = "날짜, 상품명, 결제수단으로 필터링한 주문 항목 리스트를 조회합니다.")
	@GetMapping("/sales/order-items")
	public ResponseEntity<List<SalesOrderItemDto>> getSalesOrderItems(HttpServletRequest request,
			@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
			@RequestParam(required = false) String productKeyword,
			@RequestParam(required = false) String paymentMethodKeyword) {
		String userId = (String) request.getAttribute("userId");
		List<SalesOrderItemDto> items = dashboardService.getSalesOrderItems(userId, startDate, endDate, productKeyword,
				paymentMethodKeyword);
		return ResponseEntity.ok(items);
	}
	
	
	// ✅ 최근 30일 전체 일별 매출
	@Operation(summary = "최근 30일 전체 일별 매출", description = "최근 30일 동안의 일별 전체 매출 요약을 조회합니다.")
	@GetMapping("/admin/sales/daily")
	public ResponseEntity<List<SalesSummaryDto>> getWholeDailySales() {
		List<SalesSummaryDto> result = dashboardService.getWholeDailySalesSummary();
		return ResponseEntity.ok(result);
	}

	// ✅ 최근 12개월 전체 월별 매출
	@Operation(summary = "최근 12개월 전체 월별 매출", description = "최근 30일 동안의 월별 전체 매출 요약을 조회합니다.")
	@GetMapping("/admin/sales/monthly")
	public ResponseEntity<List<SalesSummaryDto>> getWholeMonthlySales() {
		List<SalesSummaryDto> result = dashboardService.getWholeMonthlySalesSummary();
		return ResponseEntity.ok(result);
	}
	
	@Operation(summary = "KPI 카드 섹션", description = "금일 매출, 총 회원 수, 신규 회원 수, 총 주문 수, 처리 중 주문, 총 호스트 수를 조회합니다.")
    @GetMapping("/admin")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }
    
	@Operation(summary = "카테고리별 매출 분포", description = "카테고리별 총 매출 수를 조회합니다")
    @GetMapping("/admin/sales/category")
    public ResponseEntity<List<SalesCategoryDTO>> getDashboardCategorySales() {
    	System.out.println(dashboardService.getDashboardCategorySales());
        return ResponseEntity.ok(dashboardService.getDashboardCategorySales());
    }
    
	@Operation(summary = "호스트 매출 순위 Top 5", description = "호스트들 중 총 매출이 가장 높은 5명의 순위를 조회합니다.")
    @GetMapping("/admin/sales/host")
    public ResponseEntity<List<SalesHostDTO>> getDashboardHostSales() {
        return ResponseEntity.ok(dashboardService.getDashboardHostSales());
    }
	
	
	@Operation(summary = "전체 매출 요약 카드", description = "특정 월 기준으로 전체 총 주문 수, 총 매출, 평균 결제 금액을 반환합니다.")
	@GetMapping("/admin/sales/summary-card")
	public MonthlySalesSummaryDto getWholeSalesSummaryCard(@RequestParam String month, HttpServletRequest request) {
		return dashboardService.getWholeSalesSummaryCard(month);
	}
	
	
	@Operation(summary = "전체 매출 상세 주문 항목 조회", description = "날짜, 상품명, 결제수단으로 필터링한 전체 주문 항목 리스트를 조회합니다.")
	@GetMapping("/admin/sales/order-items")
	public ResponseEntity<List<SalesOrderItemDto>> getWholeSalesOrderItems(HttpServletRequest request,
			@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
			@RequestParam(required = false) String productKeyword,
			@RequestParam(required = false) String paymentMethodKeyword) {
		List<SalesOrderItemDto> items = dashboardService.getWholeSalesOrderItems(startDate, endDate, productKeyword,
				paymentMethodKeyword);
		return ResponseEntity.ok(items);
	}
	
	@Operation(summary = "TOP5 상품 (전체 판매 수량 기준)", description = "전체 판매 수량 기준 상위 5개 상품을 조회합니다.")
	@GetMapping("/admin/sales/top-products/quantity")
	public ResponseEntity<List<TopProductDto>> getWholeTopProductsByQuantity() {
		List<TopProductDto> topProducts = dashboardService.getWholeTop5ProductsByQuantity();
		return ResponseEntity.ok(topProducts);
	}

	@Operation(summary = "TOP5 상품 (전체 매출 금액 기준)", description = "전체 매출 금액 기준 상위 5개 상품을 조회합니다.")
	@GetMapping("/admin/sales/top-products/sales")
	public ResponseEntity<List<TopProductDto>> getWholeTopProductsBySales() {
		List<TopProductDto> topProducts = dashboardService.getWholeTop5ProductsBySales();
		return ResponseEntity.ok(topProducts);
	}
	
	@Operation(summary = "결제 수단별 전체 매출 비율", description = "카드, 휴대폰, 페이 등 결제 수단별 전체 매출 비율을 반환합니다.")
	@GetMapping("/admin/payment-method-chart")
	public ResponseEntity<?> getPaymentMethodChart() {

		Map<String, Integer> result = dashboardService.getWholePaymentMethodCounts();
		return ResponseEntity.ok(result);
	}
	
   
}
