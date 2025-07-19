package org.kosa.shoppingmaillmanager.user;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDAO {
	
	// 회원 기본 정보 INSERT (tb_member)
	public int insertMember(User user);
	
	// 호스트 추가 정보 INSERT (tb_host)
	public int insertHost(User user);
	
	// 회원 ID로 단일 회원 정보 조회
	public User getUser(String user_id);
	
	// 로그인 성공 시, 마지막 로그인 시간 업데이트
	public void setLoginTime(String userId);
	
	// 로그인 실패 횟수 +1 증가
	public void increaseFailCount(String userId);
	
	// 로그인 실패 횟수 조회
	public Integer getFailCount(String userId);
	
	// 로그인 5회 실패 시 계정 잠금 처리
	public void lockUser(String userId);
	
	// 로그인 성공 시 실패 횟수 초기화
	public void resetFailCount(String userId);
	
	// 이름 + 이메일로 회원 정보 조회 (중복 가능)
	public List<User> findByNameAndEmail(@Param("name") String name, @Param("email") String email);
	
	// 아이디 + 이메일로 회원 정보 조회 (중복 불가)
	public User findByUserIdAndEmail(@Param("user_id") String user_id, @Param("email") String email);
	
	// 비밀번호 변경
	public int updatePassword(@Param("user_id") String userId, @Param("password") String encodedPw);
	
	// 회원 목록 조회 (필터 및 페이징 조건 포함)
	public List<UserListDTO> getUserList(Map<String, Object> map);
	
	// 회원 목록 총 개수 조회 (페이징용)
	public int countUserList(Map<String, Object> map);
	
	// 회원 기본 정보 수정
	public int updateUser(User user);
	
	// 회원 탈퇴 처리 (secession_yn 변경)
	public int secessionUser(@Param("user_id") String user_id, @Param("secession_yn") String secession_yn);
	
	// 호스트 정보 수정
	public void updateHost(User user);
	
	// 로그인한 유저가 호스트인지 여부 확인 (호스트 ID 존재 여부 확인)
	public Integer findByHostId(@Param("user_id") String loginUserId);
	
	// 블랙리스트 상태 변경 (복수 회원 대상)
	public int updateBlacklistStatus(@Param("userIds") List<String> userIds,
	                                 @Param("blacklisted") String blacklisted);
	
	// 계정 잠금 해제 상태 변경 (복수 회원 대상)
	public int updateUnlockStatus(@Param("userIds") List<String> userIds, 
	                              @Param("status") String status, 
	                              @Param("login_fail_cnt") int login_fail_cnt);
	
	// 전체 회원 수 조회 (대시보드용)
	public int countTotalMembers();
	
	// 오늘 가입한 회원 수 조회 (대시보드용)
	public int countTodayNewMembers();
	
	// 전체 호스트 수 조회 (대시보드용)
	public int countTotalHosts();
}
