package org.kosa.shoppingmaillmanager.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kosa.shoppingmaillmanager.page.PageResponseVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
	
	private final UserDAO userDAO;
	private final BCryptPasswordEncoder passwordEncoder;

	/**
	 * 로그인 서비스
	 * @param userId 사용자 ID
	 * @param password 평문 비밀번호
	 * @return 로그인 성공 시 User 객체 반환
	 */
	public User login(String userId, String password) {
		
		// 아이디 값이 비었을 때
	    if (userId == null || userId.trim().isEmpty()) {
	        throw new IllegalArgumentException("아이디를 입력해주세요");
	    }

	    // 비밀번호 값이 비었을 때
	    if (password == null || password.trim().isEmpty()) {
	        throw new IllegalArgumentException("비밀번호를 입력해주세요");
	    }

	    // 해당 user_id의 정보를 DB에서 가져온다
	    User dbUser = userDAO.getUser(userId);
	    
	    // 해당 회원이 존재하지 않거나 탈퇴회원일 경우
	    if (dbUser == null || "Y".equals(dbUser.getSecession_yn())) {
	    	throw new RuntimeException("아이디가 존재하지 않습니다.");
	    }
	    
	    // 비밀번호 일치 여부 확인
	    if (!passwordEncoder.matches(password, dbUser.getPassword())) {
	    	// 로그인 실패 횟수 증가
	        userDAO.increaseFailCount(userId);
	        // 로그인 실패 횟수 조회
	        Integer failCount = userDAO.getFailCount(userId);

	        // 로그인 5회 이상 실패 시 계정 잠금
	        if (failCount >= 5) {
	            userDAO.lockUser(userId);
	            throw new RuntimeException("로그인 5회 실패로 계정이 잠겼습니다.");
	        } else {
	            throw new RuntimeException("비밀번호가 잘못되었습니다 (" + failCount + "/5)");
	        }
	    }
	    
	    // 권한 체크: 일반 사용자 로그인 차단
	    if (!"HOST".equals(dbUser.getGrade_id()) &&
	    	!"ADMIN".equals(dbUser.getGrade_id())) {
	        throw new RuntimeException("로그인 권한이 없습니다.");
	    }
	   
	    // 호스트 계정인데 아직 승인되지 않은 경우
	    if ("HOST".equals(dbUser.getGrade_id())) {
	    	if ("N".equals(dbUser.getApproved_yn())) {
	    		throw new RuntimeException("관리자의 승인이 필요합니다. (전화번호 : 02-1234-5678)");
	    	}
	    }
	    
	    // 계정이 잠겨있는 경우
	    if ("N".equals(dbUser.getStatus())) {
	        throw new RuntimeException("계정이 잠겨 있습니다.");
	    }

	    // 블랙리스트 회원일 경우
	    if ("Y".equals(dbUser.getBlacklisted())) {
	        throw new RuntimeException("해당 사이트의 블랙리스트 회원으로, 사이트를 이용할 수 없습니다.");
	    }
	    
	    // 로그인 성공 처리
	    userDAO.resetFailCount(userId);     // 로그인 실패 횟수 초기화
	    userDAO.setLoginTime(userId);       // 로그인 시간 업데이트

	    return dbUser;
	}
	
	
	/**
	 * 호스트 회원가입 서비스
	 * tb_member와 tb_host에 모두 insert 하므로 트랜잭션 처리
	 * @param user 회원 정보
	 */
	@Transactional
	public void registerHost(User user) {
		// 비밀번호 암호화
		String encryptedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encryptedPassword);
		
		// 권한 미지정 시 HOST로 지정
		if(user.getGrade_id() == null || user.getGrade_id().isEmpty()) {
			user.setGrade_id("HOST");
		}
		
	    userDAO.insertMember(user); // tb_member에 저장
	    userDAO.insertHost(user);   // tb_host에 저장
	}

	// 회원가입 유효성 검사 실패 시 메시지 반환, 통과 시 null 반환
	public String validateUser(User user) {
	    if (user.getUser_id() == null || user.getUser_id().isBlank()) {
	        return "아이디를 입력해주세요.";
	    }
	    if (user.getUser_id().length() < 8) {
	        return "아이디는 8자 이상이어야 합니다.";
	    }
	    if (user.getPassword() == null || user.getPassword().isBlank()) {
	        return "비밀번호를 입력해주세요.";
	    }
	    if (!isValidPasswd(user.getPassword())) {
	        return "비밀번호 형식이 올바르지 않습니다. (특수문자, 영문자, 숫자 포함 8자 이상)";
	    }
	    if (user.getName() == null || user.getName().isBlank()) {
	        return "이름을 입력해주세요.";
	    }
	    if (user.getEmail() == null || user.getEmail().isBlank()) {
	        return "이메일을 입력해주세요.";
	    }

	    return null; // 통과
	}

	// 비밀번호 유효성 검사 (정규식)
	private boolean isValidPasswd(String password) {
		// 특수문자, 영문자, 숫자 포함 8자 이상
		String pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";
	    return password != null && password.matches(pattern);
	}

	// 단일 회원 정보 조회
	public User getUser(String user_id) {
		return userDAO.getUser(user_id);
	}

	// 이름 + 이메일로 회원 검색 (중복 허용)
	public List<User> findByNameAndEmail(String name, String email) {
		return userDAO.findByNameAndEmail(name, email);
	}

	// 아이디 + 이메일로 회원 검색 (중복 불가)
	public User findByUserIdAndEmail(String userId, String email) {
		return userDAO.findByUserIdAndEmail(userId, email);
	}

	// 비밀번호 업데이트
	public void updatePassword(String userId, String encodedPw) {
		userDAO.updatePassword(userId, encodedPw);
	}

	/**
	 * 회원 목록 조회 (페이징 + 검색 + 필터링 포함)
	 * @param dto 조회 조건
	 * @return PageResponseVO<UserListDTO>
	 */
	public PageResponseVO<UserListDTO> userList(UserListDTO dto) {
		System.out.println(dto.getFilterType());
		dto.applyFilterType(); // 필터 타입 해석

		int start = (dto.getPageNo() - 1) * dto.getSize(); // 페이징 계산

		Map<String, Object> map = new HashMap<>();
		map.put("start", start);
		map.put("size", dto.getSize());
		map.put("searchColumn" ,dto.getSearchColumn());
		map.put("searchValue", dto.getSearchValue());
		map.put("sortOption", dto.getSortOption());
		map.put("user_id", dto.getUser_id());
		map.put("name", dto.getName());
		map.put("nickname", dto.getNickname());
		map.put("email", dto.getEmail());
		map.put("phone", dto.getPhone());
		map.put("address", dto.getAddress());
		map.put("gender", dto.getGender());
		map.put("created_date", dto.getCreated_date());
		map.put("blacklisted", dto.getBlacklisted());
		map.put("status", dto.getStatus());
		map.put("grade_id", dto.getGrade_id());
		map.put("secession_yn", dto.getSecession_yn());
		map.put("secession_date", dto.getSecession_date());

		// ADMIN, HOST 제외 필터링
	    if (dto.isExcludeAdminAndHost()) {
	        map.put("excludeGrades", List.of("ADMIN", "HOST"));
	    } else if (dto.getGrade_id() != null) {
	        map.put("grade_id", dto.getGrade_id());
	    }

	    map.put("blacklisted", dto.getBlacklisted());
	    map.put("status", dto.getStatus());
	    map.put("filterType", dto.getFilterType());

	    List<UserListDTO> list = userDAO.getUserList(map);
	    int total = userDAO.countUserList(map);

	    System.out.println("🧾 조건 map: " + map);

	    return new PageResponseVO<>(dto.getPageNo(), list, total, dto.getSize());
	}

	/**
	 * 회원 정보 수정 (회원 + 호스트 테이블 모두)
	 * @param user 수정할 회원 정보
	 * @return 성공 여부
	 */
	@Transactional
	public boolean updateUser(User user) {
		try {
			userDAO.updateUser(user);
			userDAO.updateHost(user);
			return true;
		} catch (Exception e) {
			log.info("수정 실패");
			return false;
		}
	}

	// 회원 탈퇴 처리 (secession_yn 업데이트)
	public boolean secessionUser(String user_id, String secession_yn) {
		return userDAO.secessionUser(user_id, secession_yn) > 0;
	}

	// 블랙리스트 상태 변경
	public int setBlacklistStatus(List<String> userIds, String blacklisted) {
	    return userDAO.updateBlacklistStatus(userIds, blacklisted);
	}

	// 계정 잠금 해제 상태 변경
	public int setUnlockStatus(List<String> userIds, String status) {
		return userDAO.updateUnlockStatus(userIds, status);
	}
}
