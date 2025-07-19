package org.kosa.shoppingmaillmanager.host.order;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDAO {

    /** 
     * 주문 목록 조회 (조건/페이징 포함)
     * @param map 검색/필터 조건, 페이징 정보 포함
     */
    public List<OrderListDTO> list(Map<String, Object> map);

    /**
     * 주문 전체 개수 조회 (조건 포함)
     * @param map 검색/필터 조건 포함
     */
    public int getTotalCount(Map<String, Object> map);

    /**
     * 단일 주문 상세 조회
     * @param order_id 주문 ID
     */
    public OrderDetailDTO getOrder(String order_id);

    /**
     * 수령인 정보 수정
     * @param dto 수령인 정보가 포함된 DTO
     */
    public int updateRecipient(OrderDetailDTO dto);

    /**
     * 주문 상태 '취소됨'으로 변경 (단일)
     * @param order_id 주문 ID
     */
    public int updateOrderStatusToCancelled(String order_id);

    /**
     * 주문 상태 '취소됨'으로 일괄 변경 (다건)
     * @param order_ids 주문 ID 리스트
     */
    public void updateOrderStatusMutilCancelled(List<String> order_ids);

    /**
     * 조건에 맞는 주문 ID 리스트 조회 (페이징 처리용)
     * @param map 검색 조건 포함
     */
    public List<String> getPagedOrderIds(Map<String, Object> map);

    /**
     * 특정 주문의 주문 상품 목록 조회
     * @param order_id 주문 ID
     */
    public List<OrderItemDTO> getOrderItemsByOrderId(String order_id);

    /**
     * 주문 상품 상태 '취소됨'으로 변경 (단일 주문 내 모든 상품)
     * @param order_id 주문 ID
     */
    public int updateOrderItemStatusToCancelled(String order_id);

    /**
     * 특정 사용자 기준 주문 내역 조회
     * @param user_id 사용자 ID
     */
    public List<OrderByUserDTO> getOrderByUser(String user_id);

    /**
     * 오늘의 총 매출 건수 조회 (당일 결제 기준)
     */
    public int selectTodaySales();

    /**
     * 전체 주문 건수 조회
     */
    public int countTotalOrders();

    /**
     * 현재 처리 중인 주문 건수 조회 (배송대기, 결제대기 등)
     */
    public int countProcessingOrders();
}
