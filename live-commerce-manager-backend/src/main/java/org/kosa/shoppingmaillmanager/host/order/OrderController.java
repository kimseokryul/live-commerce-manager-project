package org.kosa.shoppingmaillmanager.host.order;

import java.util.List;

import org.kosa.shoppingmaillmanager.page.PageResponseVO;
import org.kosa.shoppingmaillmanager.user.User;
import org.kosa.shoppingmaillmanager.user.UserDAO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(
		  name = "호스트 주문 관리 API",
		  description = "호스트(판매자)가 자신의 상품 주문 목록을 조회하고, 상세 조회 및 주문자 정보 수정, 주문 취소 등의 기능을 수행합니다."
		)
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
	private final OrderService orderService;
	private final UserDAO userDAO;
	
	@Operation(summary = "주문 목록 조회", description = "호스트의 전체 주문 목록을 필터 및 정렬 조건에 따라 조회합니다.")
	@GetMapping("/")
	public ResponseEntity<PageResponseVO<OrderListDTO>> orderList(
			@RequestParam(defaultValue = "1") int pageNo, 
    		@RequestParam(defaultValue = "10") int size, 
    		@RequestParam(required = false) String searchColumn, 
    		@RequestParam(required = false) String searchValue, 
    		@RequestParam(required = false) String startDate, 
    		@RequestParam(required = false) String endDate, 
    		@RequestParam(required = false) List<String> order_status, 
    		@RequestParam(required = false) List<String> payment_method, 
    		@RequestParam(required = false) String recipient_name, 
    		@RequestParam(required = false) String recipient_phone, 
    		@RequestParam(required = false) String order_address_detail, 
    		@RequestParam(required = false) String user_name,
    		@RequestParam(required = false) String user_phone,
    		@RequestParam(required = false) String user_email,
    		@RequestParam(required = false) String sortOption){
		
		String loginUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Integer host_id = userDAO.findByHostId(loginUserId);
		
		PageResponseVO<OrderListDTO> pageResponse = orderService.list(
				host_id, searchColumn, searchValue, pageNo, size, startDate, endDate, order_status,
				payment_method, recipient_name, recipient_phone, order_address_detail, 
				user_name, user_phone, user_email, sortOption);
        return ResponseEntity.ok(pageResponse);
	}
	
	@Operation(summary = "주문 상세 조회", description = "주문 ID를 기반으로 해당 주문의 상세 정보를 조회합니다.")
	@GetMapping("/detail")
	public ResponseEntity<OrderDetailDTO> orderDetail(@RequestParam String order_id){
		OrderDetailDTO order = orderService.getOrder(order_id);
		
		if (order == null) {
	        return ResponseEntity.notFound().build(); // 404Add commentMore actions
	    }

	    return ResponseEntity.ok(order); // 200 + JSON 바디
	}
	
	@Operation(summary = "사용자 주문 내역 조회", description = "특정 사용자 ID로 해당 사용자의 주문 내역을 조회합니다.")
	@GetMapping("/user-detail")
	public ResponseEntity<List<OrderByUserDTO>> userDetail(@RequestParam String user_id){
		List<OrderByUserDTO> order = orderService.getOrderByUser(user_id);
	    return ResponseEntity.ok(order); // 200 + JSON 바디
	}
	
	@Operation(summary = "수령인 정보 수정", description = "주문 상세 정보에서 수령인의 이름, 연락처, 주소 등을 수정합니다.")
	@PutMapping("/detail")
	public ResponseEntity<String> updateRecipientInfo(@RequestBody OrderDetailDTO orderDetailDTO) {
	    boolean success = orderService.updateRecipient(orderDetailDTO);

	    if (success) {
	        return ResponseEntity.ok("수정 완료"); // 200 OK
	    } else {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패"); // 500
	    }
	}
	
	@Operation(summary = "주문 취소 (단건)", description = "주문 ID로 특정 주문을 취소합니다.")
	@DeleteMapping("/{order_id}")
	public ResponseEntity<String> cancelOrder(@PathVariable("order_id") String order_id) {
	    boolean isCancelled = orderService.cancelOrder(order_id);

	    if (isCancelled) {
	        return ResponseEntity.ok("주문이 취소되었습니다."); // 200 OK
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("주문 취소 실패"); // 500
	    }
	}
	
	@Operation(summary = "주문 취소 (다건)", description = "선택된 여러 주문을 한 번에 취소합니다.")
	@DeleteMapping
	public ResponseEntity<?> cancelMultipleOrders(@RequestBody List<String> order_ids) {
	    orderService.cancelOrders(order_ids);
	    return ResponseEntity.ok("선택한 주문이 취소되었습니다.");
	}
}
