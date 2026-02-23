package com.jjld.domain.notice.repository;

import com.jjld.domain.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 페이지&개수만큼의 데이터 호출
    Page<Notice> findAll(Pageable pageable);

    // 전체 내용 검색으로 찾기
    @Query("""
        SELECT n FROM Notice n
        WHERE n.noticeTitle LIKE %:keyword%
           OR n.admin.adminName LIKE %:keyword%
    """)
    Page<Notice> searchAll(@Param("keyword") String keyword, Pageable pageable);

    // 고정 게시글 따로 조회
    List<Notice> findByFixStatus(Boolean fixStatus);

    // 작성자별 게시글 조회
    Page<Notice> findByAdmin_AdminNameContaining(String adminName, Pageable pageable);

    // 제목별 게시글 조회
    Page<Notice> findByNoticeTitleContaining(String noticeTitle, Pageable pageable);

    // 아이디로 상세내역 조회
    Notice findByNoticeId(Long noticeId);

    // 마이페이지에서 관리자가 작성한 공지의 수를 조회
    long countByAdmin_AdminId(Long adminId);
}
