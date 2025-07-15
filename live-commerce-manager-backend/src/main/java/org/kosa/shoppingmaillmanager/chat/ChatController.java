package org.kosa.shoppingmaillmanager.chat;

import java.util.List;
import java.util.Map;

import org.kosa.shoppingmaillmanager.chat.util.ChatFilterUtil;
import org.kosa.shoppingmaillmanager.host.product.dto.BroadcastStatusDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "채팅 API", description = "실시간 채팅 관련 기능 제공")
@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatController {
	
	private final SimpMessagingTemplate messagingTemplate;
	private final ChatService chatService;
	private final ChatSessionManager chatSessionManager;
	
	@MessageMapping("/sendMessage")
	public void sendMessage(ChatMessageDTO message) {

	    if (message.getType() == null || message.getType().isBlank()) {
	        message.setType("text");
	    }

	    // 금지된 유저인지 확인
	    boolean banned = chatService.isUserBanned(message.getBroadcastId(), message.getUserId());
	    if (banned) {
	        log.warn("⛔ 금지된 유저의 메시지 차단됨: userId={}, 방송ID={}", message.getUserId(), message.getBroadcastId());
	        return;
	    }

	    // 욕설 필터링
	    message.setText(ChatFilterUtil.filterBadWords(message.getText()));

	    // DB 저장
	    chatService.saveChatMessage(message);

	    // 전체 채팅방에 뿌림
	    messagingTemplate.convertAndSend("/topic/public", message);
	}
	
	@Operation(summary = "방송 채팅 히스토리 조회")
	@GetMapping("/chat/history/{broadcastId}")
	public ResponseEntity<List<ChatMessageDTO>> getChatHistory(@PathVariable Long broadcastId) {
		return ResponseEntity.ok(chatService.getHistoryByBroadcastId(broadcastId));
	}
	
	@Operation(summary = "방송 상태 조회")
	@GetMapping("/broadcasts/{broadcastId}/status")
	public ResponseEntity<BroadcastStatusDTO> getBroadcastStatus(@PathVariable Long broadcastId) {
		BroadcastStatusDTO dto = chatService.getBroadcastStatus(broadcastId);
		return ResponseEntity.ok(dto);
	}
	
	@Operation(summary = "채팅 참여자 수 조회")
	@GetMapping("/chat/participants/{broadcastId}")
    public ResponseEntity<Map<String, Object>> getParticipantCount(@PathVariable Long broadcastId) {
        int count = chatSessionManager.getParticipantCount(broadcastId);
        return ResponseEntity.ok(Map.of("count", count));
    }
	
	@Operation(summary = "특정 세션 수동 제거")
	@PostMapping("/chat/disconnect/{broadcastId}")
	@ResponseBody
	public ResponseEntity<Void> disconnectManually(
	        @PathVariable Long broadcastId,
	        @RequestParam("id") String id) {

	    chatSessionManager.removeSessionManually(broadcastId, id);
	    return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "채팅 유저 강제 금지 (기본 300초)")
	@PostMapping("/chat/ban")
	public ResponseEntity<Void> banUserFromChat(
			@RequestParam Long broadcastId,
			@RequestParam String userId,
			@RequestParam(defaultValue = "300") long durationSeconds) {
		chatService.banUser(broadcastId, userId, durationSeconds);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "유저의 채팅 금지 여부 확인")
	@GetMapping("/chat/ban-status/{broadcastId}/{userId}")
	public ResponseEntity<Map<String, Boolean>> checkUserBanStatus(
	        @PathVariable Long broadcastId,
	        @PathVariable String userId) {

	    boolean banned = chatService.isUserBanned(broadcastId, userId);
	    return ResponseEntity.ok(Map.of("banned", banned));
	}
}




/*
 * @MessageMapping("/sendMessage") 클라이언트가 /app/sendMessage 로 보내면 이 메서드가 받음
 * @SendTo 모든 구독자에게 메세지를 뿌릴 경로
 * return message 다시 브로커에게 전달
 * 
 * 
 * 
 */
