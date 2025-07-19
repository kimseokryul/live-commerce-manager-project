package org.kosa.shoppingmaillmanager.host.order;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kosa.shoppingmaillmanager.page.PageResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {
	private final OrderDAO orderDAO;

	/**
	 * 주문 목록 조회 (페이징 + 검색/필터 + 정렬 포함)
	 * 
	 * @param host_id 호스트 ID
	 * @param searchColumn 검색할 컬럼명
	 * @param searchValue 검색어
	 * @param pageNo 현재 페이지 번호
	 * @param size 페이지당 항목 수
	 * @param startDate 시작일 (yyyy-MM-dd)
	 * @param endDate 종료일 (yyyy-MM-dd)
	 * @param order_status 주문 상태 리스트
	 * @param payment_method 결제 수단 리스트
	 * @param recipient_name 수령인 이름
	 * @param recipient_phone 수령인 연락처
	 * @param order_address_detail 배송 상세 주소
	 * @param user_name 주문자 이름
	 * @param user_phone 주문자 연락처
	 * @param user_email 주문자 이메일
	 * @param sortOption 정렬 옵션
	 * @return 페이징된 주문 목록
	 */
	public PageResponseVO<OrderListDTO> list(
			Integer host_id, String searchColumn, String searchValue, int pageNo, int size, String startDate, String endDate, 
			List<String> order_status, List<String> payment_method, String recipient_name, String recipient_phone, 
			String order_address_detail, String user_name, String user_phone, String user_email, String sortOption) {
		
		int start = (pageNo - 1) * size;

		// 조건 맵 구성
		Map<String, Object> map = new HashMap<>();
		map.put("host_id", host_id);
		map.put("start", start);
		map.put("size", size);
		map.put("searchColumn", searchColumn);
		map.put("searchValue", searchValue);
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("order_status", order_status);
		map.put("payment_method", payment_method);
		map.put("recipient_name", recipient_name);
		map.put("recipient_phone", recipient_phone);
		map.put("order_address_detail",order_address_detail);
		map.put("user_name", user_name);
		map.put("user_phone", user_phone);
		map.put("user_email", user_email);
		map.put("sortOption", sortOption);

		// 조건에 맞는 주문 ID 목록 조회
		List<String> orderIdList = orderDAO.getPagedOrderIds(map);

		List<OrderListDTO> orders = Collections.emptyList();

		// 주문 ID가 존재할 경우 상세 주문 목록 조회
		if(!orderIdList.isEmpty()) {
			map.put("orderIdList", orderIdList);
			orders = orderDAO.list(map);
		}

		// 전체 개수 조회
		Integer count = orderDAO.getTotalCount(map);
		int totalCount = (count != null) ? count : 0;

		return new PageResponseVO<>(pageNo, orders, totalCount, size);
	}

	/**
	 * 단일 주문 상세 조회
	 * 
	 * @param order_id 주문 ID
	 * @return 주문 상세 DTO
	 */
	public OrderDetailDTO getOrder(String order_id) {
		return orderDAO.getOrder(order_id);
	}

	/**
	 * 수령인 정보 수정
	 * 
	 * @param dto 주문 상세 DTO (수정 정보 포함)
	 * @return 성공 여부
	 */
	public boolean updateRecipient(OrderDetailDTO dto) {
		return orderDAO.updateRecipient(dto) > 0;
	}

	/**
	 * 주문 단건 취소 (주문 + 주문아이템 상태 모두)
	 * 
	 * @param order_id 주문 ID
	 * @return 성공 여부
	 */
	@Transactional
	public boolean cancelOrder(String order_id) {
		int result1 = orderDAO.updateOrderStatusToCancelled(order_id);
		int result2 = orderDAO.updateOrderItemStatusToCancelled(order_id);
        return result1 > 0 && result2 > 0;
	}

	/**
	 * 주문 다건 취소 (일괄 처리)
	 * 
	 * @param order_ids 주문 ID 목록
	 */
	public void cancelOrders(List<String> order_ids) {
		orderDAO.updateOrderStatusMutilCancelled(order_ids);
	}

	/**
	 * 특정 사용자 기준으로 주문 내역 조회
	 * 
	 * @param user_id 사용자 ID
	 * @return 사용자별 주문 리스트
	 */
	public List<OrderByUserDTO> getOrderByUser(String user_id) {
		return orderDAO.getOrderByUser(user_id);
	}
}
