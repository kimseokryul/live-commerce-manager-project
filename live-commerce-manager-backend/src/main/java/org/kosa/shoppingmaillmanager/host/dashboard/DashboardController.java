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

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

	private final DashboardService dashboardService;

	@GetMapping("/order-status")
	public OrderStatusCountDto getOrderStatus(HttpServletRequest request) {
		String userId = (String) request.getAttribute("userId");

		return dashboardService.getOrderStatusCounts(userId);
	}

	// ✅ 최근 30일 일별 매출
	@GetMapping("/sales/daily")
	public ResponseEntity<List<SalesSummaryDto>> getDailySales(HttpServletRequest request) {
		String userId = (String) request.getAttribute("userId");
		List<SalesSummaryDto> result = dashboardService.getDailySalesSummary(userId);
		return ResponseEntity.ok(result);
	}

	// ✅ 최근 12개월 월별 매출
	@GetMapping("/sales/monthly")
	public ResponseEntity<List<SalesSummaryDto>> getMonthlySales(HttpServletRequest request) {
		String userId = (String) request.getAttribute("userId");
		List<SalesSummaryDto> result = dashboardService.getMonthlySalesSummary(userId);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/sales/summary-card")
	public MonthlySalesSummaryDto getSalesSummaryCard(@RequestParam String month, HttpServletRequest request) {
		String userId = (String) request.getAttribute("userId");
		return dashboardService.getSalesSummaryCard(userId, month);
	}

	@GetMapping("/sales/top-products/quantity")
	public ResponseEntity<List<TopProductDto>> getTopProductsByQuantity(HttpServletRequest request) {
		String userId = (String) request.getAttribute("userId");
		List<TopProductDto> topProducts = dashboardService.getTop5ProductsByQuantity(userId);
		return ResponseEntity.ok(topProducts);
	}

	@GetMapping("/sales/top-products/sales")
	public ResponseEntity<List<TopProductDto>> getTopProductsBySales(HttpServletRequest request) {
		String userId = (String) request.getAttribute("userId");
		List<TopProductDto> topProducts = dashboardService.getTop5ProductsBySales(userId);
		return ResponseEntity.ok(topProducts);
	}

	@GetMapping("/payment-method-chart")
	public ResponseEntity<?> getPaymentMethodChart(HttpServletRequest request) {
		String userId = (String) request.getAttribute("userId");

		Map<String, Integer> result = dashboardService.getPaymentMethodCounts(userId);
		return ResponseEntity.ok(result);
	}

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
	@GetMapping("/admin/sales/daily")
	public ResponseEntity<List<SalesSummaryDto>> getWholeDailySales() {
		List<SalesSummaryDto> result = dashboardService.getWholeDailySalesSummary();
		return ResponseEntity.ok(result);
	}

	// ✅ 최근 12개월 전체 월별 매출
	@GetMapping("/admin/sales/monthly")
	public ResponseEntity<List<SalesSummaryDto>> getWholeMonthlySales() {
		List<SalesSummaryDto> result = dashboardService.getWholeMonthlySalesSummary();
		return ResponseEntity.ok(result);
	}
	

    @GetMapping("/admin")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }
    
    @GetMapping("/admin/sales/category")
    public ResponseEntity<List<SalesCategoryDTO>> getDashboardCategorySales() {
    	System.out.println(dashboardService.getDashboardCategorySales());
        return ResponseEntity.ok(dashboardService.getDashboardCategorySales());
    }
    
    @GetMapping("/admin/sales/host")
    public ResponseEntity<List<SalesHostDTO>> getDashboardHostSales() {
        return ResponseEntity.ok(dashboardService.getDashboardHostSales());
    }
   
}
