package org.kosa.shoppingmaillmanager.user;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kosa.shoppingmaillmanager.page.PageResponseVO;
import org.kosa.shoppingmaillmanager.security.JwtUtil;
import org.kosa.shoppingmaillmanager.security.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
	
	private final UserService userService;
	private final JwtUtil jwtUtil;
	private final BCryptPasswordEncoder passwordEncoder;
	private final RefreshTokenService refreshTokenService;
	
//	@PostMapping("/login")
//	public ResponseEntity<?> login(@RequestBody User user) {
//	    try {
//	        User dbUser = userService.login(user.getUser_id(), user.getPassword());
//	        String token = jwtUtil.generateToken(dbUser.getUser_id());
//
//	        // DTO 변환
//	        LoginUserDTO dto = new LoginUserDTO(dbUser);
//
//	        return ResponseEntity.ok(Map.of(
//	            "token", token,
//	            "user", dto
//	        ));
//
//	    } catch (IllegalArgumentException e) {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//	                .body(Map.of("error", e.getMessage()));
//	    } catch (RuntimeException e) {
//	        return ResponseEntity.status(HttpStatus.FORBIDDEN)
//	                .body(Map.of("error", e.getMessage()));
//	    }
//	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response) {
		System.out.println("✅ 받은 user_id = " + user.getUser_id());
	    System.out.println("✅ 받은 password = " + user.getPassword());
	    try {
	        // 1. DB에서 사용자 인증 (아이디 + 비밀번호 일치 여부 확인)
	        User dbUser = userService.login(user.getUser_id(), user.getPassword());

	        // 2. Access Token 생성 (유효기간: 1시간)
	        // 주로 API 요청 인증에 사용, 클라이언트에서 localStorage 등에 저장
	        String token = jwtUtil.generateToken(dbUser.getUser_id());

	        // 3. Refresh Token 생성 (유효기간: 7일)
	        // Access Token 만료 시 새로운 Access Token을 발급받을 때 사용됨
	        String refreshToken = jwtUtil.generateRefreshToken(dbUser.getUser_id());

	        // 4. 서버 저장소(메모리, Redis 등)에 Refresh Token 저장
	        // 이후 발급 요청 시 서버가 해당 토큰이 유효한지 검증할 수 있게 하기 위함
	        refreshTokenService.save(user.getUser_id(), refreshToken);

	        // 5. Refresh Token을 HttpOnly + Secure 쿠키로 설정
	        // - httpOnly: 자바스크립트에서 접근 불가 (XSS 방어)
	        // - secure: HTTPS에서만 전송됨 (실제 배포 시 필수)
	        // - path: 쿠키가 어떤 경로에서 유효한지 설정 ("/" → 전체 경로에서 사용 가능)
	        // - maxAge: 쿠키 유효 시간 설정 (7일)
	        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
	            .httpOnly(true)
	            .secure(true)
	            .path("/")
	            .maxAge(7 * 24 * 60 * 60)
	            .build();

	        // 6. 응답 헤더에 쿠키 설정 추가
	        response.setHeader("Set-Cookie", cookie.toString());

	        // 7. 로그인한 사용자 정보를 DTO로 변환해서 클라이언트에 전달
	        LoginUserDTO dto = new LoginUserDTO(dbUser);

	        // 8. 응답 바디에 AccessToken + 사용자 정보 전달
	        return ResponseEntity.ok(Map.of(
	            "token", token,
	            "user", dto
	        ));

	    } catch (IllegalArgumentException e) {
	        // 아이디 또는 비밀번호 불일치 등의 이유로 로그인 실패
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(Map.of("error", e.getMessage()));
	    } catch (RuntimeException e) {
	        // 그 외 예외 처리
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                .body(Map.of("error", e.getMessage()));
	    }
	}
	
	
	// 토큰 값 새로 가져오기
	@PostMapping("/refresh") // [POST] 방식의 /refresh 엔드포인트
	public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
	    
	    // 1. 요청의 쿠키에서 "refreshToken"이라는 이름의 쿠키 값을 찾음
	    String refreshToken = null;
	    for (Cookie cookie : request.getCookies()) {
	        if ("refreshToken".equals(cookie.getName())) {
	            refreshToken = cookie.getValue(); //  쿠키에서 추출한 리프레시 토큰 값
	            break;
	        }
	    }

	    // 2. 쿠키에 refreshToken이 없거나, 토큰 자체가 위조되었거나 만료되었을 경우 → 401 반환
	    if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                             .body("리프레시 토큰이 유효하지 않음");
	    }

	    // 3. 리프레시 토큰에서 사용자 ID(subject)를 꺼냄
	    //     JWT 안에 들어있는 사용자 식별자 (setSubject로 넣은 값)
	    String userId = jwtUtil.validateTokenAndGetUserId(refreshToken);

	    //  4. 서버에 저장된 리프레시 토큰과 비교 (토큰 탈취 방지 목적)
	    //     클라이언트가 보내준 토큰이 서버가 기억하고 있는 값과 다르면 → 위조된 토큰으로 판단
	    if (!refreshTokenService.isValid(userId, refreshToken)) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                             .body("리프레시 토큰이 일치하지 않음");
	    }

	    // 5. 모든 검증을 통과하면 새로운 Access Token을 발급 (유효기간: 1시간)
	    String newAccessToken = jwtUtil.generateToken(userId);
	    Date expireAt = jwtUtil.getTokenExpiration(newAccessToken);

	    // 6. 새로 발급한 Access Token을 응답 바디에 담아서 클라이언트에 전달
	    return ResponseEntity.ok(Map.of(
	    		"accessToken", newAccessToken,
	    		"expireTime", expireAt.getTime() / 1000
	    ));
	}
	
	@PostMapping("/logout")
	public ResponseEntity<String> logout() {
	    return ResponseEntity.ok("로그아웃 되었습니다");
	}
	
	
	@GetMapping("/login/findId")
	public ResponseEntity<?> findId(@RequestParam String name,
		    @RequestParam String email) {

	    List<User> user = userService.findByNameAndEmail(name, email);
	    if (user != null) {
	    	List<String> userIds = user.stream()
	                .map(User::getUser_id)
	                .collect(Collectors.toList());
	        return ResponseEntity.ok(Map.of("userIds", userIds));
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 정보 없음");
	    }
	}
	
	@GetMapping("/login/findPassword")
	public ResponseEntity<?> findPassword(@RequestParam String user_id,
		    @RequestParam String email) {

	    User user = userService.findByUserIdAndEmail(user_id, email);
	    if (user != null) {
	        return ResponseEntity.ok(Map.of("user_id", user.getUser_id()));
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 정보 없음");
	    }
	}
	
	@PutMapping("/login/changePassword")
	public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload) {
	    String userId = payload.get("user_id");
	    String newPassword = payload.get("newPassword");

	    String encodedPw = passwordEncoder.encode(newPassword);
	    userService.updatePassword(userId, encodedPw);
	    return ResponseEntity.ok().build();
	}
	
	@PostMapping("/host/register")
	@ResponseBody
	public Map<String, Object> register(@RequestBody User user){
		Map<String, Object> result = new HashMap<String, Object>();
		if(!userService.isValid(user)) {
			result.put("errorMessage", "입력값 검증 오류 발생");
			result.put("status", "error");
		} else {
			userService.registerHost(user);
			result.put("status", "ok");
		}
		return result;
	}
	
	@PostMapping("/host/isExistUserId")
	public Map<String, Object> isExistUserId(@RequestBody User user){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("existUserId", userService.getUser(user.getUser_id()) != null);
		return map;
	}
	
	@GetMapping("/host/me")
	public ResponseEntity<?> getMyInfo() {
		String user_id = (String) SecurityContextHolder.getContext()
							                .getAuthentication()
							                .getPrincipal();
		return ResponseEntity.ok(Map.of("user_id", user_id));
	}
	
	@GetMapping("/login/me")
	public ResponseEntity<?> getMe(){
		String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (userId == null || "anonymousUser".equals(userId)) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
	    }
		
		System.out.println("🔍 userId = " + userId);
	    User user = userService.getUser(userId);
//	    log.info("grade_id: {}", user.getGrade_id());
	    return ResponseEntity.ok(Map.of(
	        "user_id", user.getUser_id(),
	        "grade_id", user.getGrade_id(),
	        "nickname", user.getNickname()
	    ));
	}
	
	
	@GetMapping("/admin/user-list")
	public ResponseEntity<PageResponseVO<UserListDTO>> userList(@ModelAttribute UserListDTO dto){
		dto.applyFilterType();
		PageResponseVO<UserListDTO> pageResponse = userService.userList(dto);
        return ResponseEntity.ok(pageResponse);
	}
	
	@GetMapping("/user-detail/{user_id}")
	public ResponseEntity<User> userDetail(@PathVariable String user_id){
		User user = userService.getUser(user_id);
		
		if (user == null) {
	        return ResponseEntity.notFound().build(); // 404Add commentMore actions
	    }

	    return ResponseEntity.ok(user); // 200 + JSON 바디
	}
	
	@PutMapping("/user-detail")
	public ResponseEntity<String> updateUserDetail(@RequestBody User user) {
	    boolean success = userService.updateUser(user);

	    if (success) {
	        return ResponseEntity.ok("수정 완료"); // 200 OK
	    } else {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패"); // 500
	    }
	}
	
	@PutMapping("/admin/user/secession/{user_id}")
	public ResponseEntity<String> secessionUser(
			@PathVariable String user_id, 
			@RequestParam String secession_yn) {
	    boolean success = userService.secessionUser(user_id ,secession_yn);

	    if (success) {
	        return ResponseEntity.ok("수정 완료"); // 200 OK
	    } else {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패"); // 500
	    }
	}
	
	@PutMapping("/admin/users/blacklist")
	public ResponseEntity<?> setBlacklistStatus(@RequestBody Map<String, Object> body) {
	    List<String> userIds = (List<String>) body.get("userIds");
	    String blacklisted = (String) body.get("blacklisted");

	    if (userIds == null || userIds.isEmpty() || blacklisted == null) {
	        return ResponseEntity.badRequest().body("필수 정보 누락");
	    }

	    int updated = userService.setBlacklistStatus(userIds, blacklisted);
	    return ResponseEntity.ok(Map.of("message", updated + "명 처리 완료", "status", blacklisted));
	}
	
	@PutMapping("/admin/users/unlock")
	public ResponseEntity<?> setUnlockStatus(@RequestBody Map<String, Object> body) {
	    List<String> userIds = (List<String>) body.get("userIds");
	    String status = (String) body.get("status");

	    if (userIds == null || userIds.isEmpty() || status == null) {
	        return ResponseEntity.badRequest().body("필수 정보 누락");
	    }

	    int updated = userService.setUnlockStatus(userIds, status);
	    return ResponseEntity.ok(Map.of("message", updated + "명 처리 완료", "status", status));
	}
}
