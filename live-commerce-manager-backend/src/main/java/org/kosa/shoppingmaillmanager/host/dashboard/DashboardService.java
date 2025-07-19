package org.kosa.shoppingmaillmanager.host.dashboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kosa.shoppingmaillmanager.host.order.OrderDAO;
import org.kosa.shoppingmaillmanager.host.product.HostDAO;
import org.kosa.shoppingmaillmanager.host.product.dto.MonthlySalesSummaryDto;
import org.kosa.shoppingmaillmanager.host.product.dto.OrderStatusCountDto;
import org.kosa.shoppingmaillmanager.host.product.dto.SalesCategoryDTO;
import org.kosa.shoppingmaillmanager.host.product.dto.SalesHostDTO;
import org.kosa.shoppingmaillmanager.host.product.dto.SalesOrderItemDto;
import org.kosa.shoppingmaillmanager.host.product.dto.SalesSummaryDto;
import org.kosa.shoppingmaillmanager.host.product.dto.TopProductDto;
import org.kosa.shoppingmaillmanager.user.UserDAO;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

	private final DashboardDAO dashboardDAO;
	private final HostDAO hostDAO;
	private final UserDAO userDAO;
	private final OrderDAO orderDAO;

	public OrderStatusCountDto getOrderStatusCounts(String userId) {
		String hostId = hostDAO.findHostIdByUserId(userId);
		return dashboardDAO.getOrderStatusCounts(hostId);
	}

	// 최근 30일 일별 매출
	public List<SalesSummaryDto> getDailySalesSummary(String userId) {
		String hostId = hostDAO.findHostIdByUserId(userId);
		return dashboardDAO.getDailySalesSummary(hostId);
	}

	// 최근 12개월 월별 매출
	public List<SalesSummaryDto> getMonthlySalesSummary(String userId) {
		String hostId = hostDAO.findHostIdByUserId(userId);
		return dashboardDAO.getMonthlySalesSummary(hostId);
	}

	public MonthlySalesSummaryDto getSalesSummaryCard(String userId, String month) {
		String hostId = hostDAO.findHostIdByUserId(userId);
		return dashboardDAO.getSalesSummaryCard(hostId, month);
	}

	public List<TopProductDto> getTop5ProductsByQuantity(String userId) {
		String hostId = hostDAO.findHostIdByUserId(userId);
		return dashboardDAO.getTop5ByQuantity(hostId);
	}

	public List<TopProductDto> getTop5ProductsBySales(String userId) {
		String hostId = hostDAO.findHostIdByUserId(userId);
		return dashboardDAO.getTop5BySales(hostId);
	}

	public Map<String, Integer> getPaymentMethodCounts(String userId) {
		return dashboardDAO.countPaymentMethods(userId);
	}

	public List<SalesOrderItemDto> getSalesOrderItems(String userId, String startDate, String endDate,
			String productKeyword, String paymentMethodKeyword) {
		String hostId = hostDAO.findHostIdByUserId(userId);
		return dashboardDAO.findSalesOrderItems(hostId, startDate, endDate, productKeyword, paymentMethodKeyword);
	}

	 public Map<String, Object> getDashboardStats() {
	        Map<String, Object> stats = new HashMap<>();

	        stats.put("todaySales", orderDAO.selectTodaySales());
	        stats.put("totalMembers", userDAO.countTotalMembers());
	        stats.put("newMembers", userDAO.countTodayNewMembers());
	        stats.put("totalOrders", orderDAO.countTotalOrders());
	        stats.put("processingOrders", orderDAO.countProcessingOrders());
	        stats.put("totalHosts", userDAO.countTotalHosts());

	        return stats;
	 }

	public List<SalesSummaryDto> getWholeDailySalesSummary() {
		return dashboardDAO.getWholeDailySalesSummary();
	}

	public List<SalesSummaryDto> getWholeMonthlySalesSummary() {
		return dashboardDAO.getWholeMonthlySalesSummary();
	}

	public List<SalesCategoryDTO> getDashboardCategorySales() {
		return dashboardDAO.getDashboardCategorySales();
	}

	public List<SalesHostDTO> getDashboardHostSales() {
		return dashboardDAO.getDashboardHostSales();
	}

	public MonthlySalesSummaryDto getWholeSalesSummaryCard(String month) {
		return dashboardDAO.getWholeSalesSummaryCard(month);
	}

	public List<SalesOrderItemDto> getWholeSalesOrderItems(String startDate, String endDate, String productKeyword,
			String paymentMethodKeyword) {
		return dashboardDAO.findWholeSalesOrderItems(startDate, endDate, productKeyword, paymentMethodKeyword);
	}

	public List<TopProductDto> getWholeTop5ProductsByQuantity() {
		return dashboardDAO.getWholeTop5ByQuantity();
	}

	public List<TopProductDto> getWholeTop5ProductsBySales() {
		return dashboardDAO.getWholeTop5BySales();
	}

	public Map<String, Integer> getWholePaymentMethodCounts() {
		return dashboardDAO.countWholePaymentMethodCounts();
	}
}
