package org.kosa.shoppingmaillmanager.host.broadcast;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BroadCastDAO {

	/** 
	 * 방송 기본 정보 등록
	 * @param broadCast 등록할 방송 정보
	 * @return 삽입된 row 수
	 */
	public int insert(BroadCast broadCast);

	/**
	 * 방송 ID에 해당하는 상품 목록 검색 (아마 오타 가능성 있음? → findByBroadcastId가 더 맞는듯?)
	 * @param keyword 키워드 (사실상 broadcast_id로 추정됨)
	 * @return 상품 목록
	 */
	public List<BroadCastProduct> selectByBroadcastId(@Param("keyword") String keyword);

	/**
	 * 방송과 연결된 상품 정보 등록
	 * @param product 등록할 상품 정보
	 * @return 삽입된 row 수
	 */
	public int insertProduct(BroadCastProduct product);

	/**
	 * 키워드 기반 상품 검색 (상품명 LIKE 검색)
	 * @param keyword 검색어
	 * @return 검색된 상품 목록
	 */
	public List<BroadCastProduct> findByKeyword(@Param("keyword") String keyword);

	/**
	 * 방송 ID로 방송 정보 조회 (상세 조회용)
	 * @param broadcast_id 방송 고유 ID
	 */
	public BroadCast findBroadcastById(@Param("broadcast_id") int broadcast_id);

	/**
	 * 방송 ID 기준으로 연결된 상품 목록 조회
	 * @param broadcastId 방송 고유 ID
	 * @return 상품 목록
	 */
	public List<BroadCastProduct> findProductsByBroadcastId(@Param("broadcast_id") int broadcastId);

	/**
	 * 방송 ID 기준으로 시청자 목록 조회
	 * @param broadcastId 방송 고유 ID
	 * @return 시청자 목록
	 */
	public List<BroadCastViewer> findViewersByBroadcastId(@Param("broadcast_id") int broadcastId);

	/**
	 * 방송 ID로 방송 단건 조회 (간단 조회용)
	 * @param broadcast_id 방송 ID
	 * @return 방송 정보
	 */
	public BroadCast findById(int broadcast_id);

	/**
	 * 방송 상태 업데이트 (예: 송출 중, 종료됨 등)
	 * @param broadCast 상태값이 담긴 객체
	 */
	public void updateStatus(BroadCast broadCast);

	/**
	 * 상품 중 가장 상위 카테고리를 기준으로 방송의 카테고리 자동 업데이트
	 * @param broadcastId 방송 ID
	 */
	public void updateBroadcastCategoryByTopProductCategory(int broadcastId);

	/**
	 * 시청자 입장 기록 추가 (tb_broadcast_viewer)
	 * @param viewer 입장한 시청자 정보
	 */
	public void insertViewer(BroadCastViewer viewer);

	/**
	 * 시청자 퇴장 처리 - 퇴장 시간 기록
	 * @param user_id 사용자 ID
	 * @param broadcast_id 방송 ID
	 */
	public void updateLeftTime(@Param("user_id") String user_id, @Param("broadcast_id") int broadcast_id);

	/**
	 * 시청자 수 최종 집계 저장 (Redis → DB)
	 * @param broadcast_id 방송 ID
	 * @param total 총 시청자 수
	 */
	public void updateTotalViewersManual(@Param("broadcast_id") int broadcast_id, @Param("total") long total);

	/**
	 * 방송 목록 조회 (관리자/호스트용, 검색/페이징 포함)
	 * @param map 조건 포함된 검색 파라미터
	 * @return 방송 리스트
	 */
	public List<BroadCastListDTO> findBroadcastList(Map<String, Object> map);

	/**
	 * 방송 목록 전체 개수 조회 (검색/필터 포함)
	 * @param map 조건 포함
	 * @return 방송 개수
	 */
	public int countBroadcastList(Map<String, Object> map);

	/**
	 * 카테고리 ID 기준으로 카테고리 이름 조회
	 * @param category_id 카테고리 ID
	 * @return 카테고리명
	 */
	public String findCategoryName(@Param("category_id") Long category_id);

	/**
	 * 녹화된 영상 URL 저장
	 * @param broadcastId 방송 ID
	 * @param videoUrl 녹화된 영상 스트리밍 URL
	 */
	public void updateVideoUrl(@Param("broadcast_id") int broadcastId, @Param("video_url") String videoUrl);

	/**
	 * 스트림 URL 수정
	 * @param broadCast 스트림 URL이 포함된 방송 객체
	 */
	public void updateStreamUrl(BroadCast broadCast);

	public long countUniqueViewers(int broadcast_id);
}
