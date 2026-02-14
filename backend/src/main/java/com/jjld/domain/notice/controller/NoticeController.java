package com.jjld.domain.notice.controller;

import com.jjld.domain.notice.dto.NoticeDetailRequest;
import com.jjld.domain.notice.dto.NoticeDetailResponse;
import com.jjld.domain.notice.dto.NoticeListResponse;
import com.jjld.domain.notice.service.NoticeService;
import com.jjld.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notices/api")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    // 백엔드 페이지네이션을 이용한 공지사항 목록조회
    // 기본값으로 1페이지 10개 호출
    @GetMapping("/list")
    @Operation(summary = "공지사항 목록 조회")
    public ResponseEntity<?> noticeList(
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "page", defaultValue = "1") int page
    ){
        Page<NoticeListResponse> noticeList = noticeService.getNoticeList(size, page-1);
        return ResponseEntity.ok(ApiResponse.success(noticeList));
    }

    // 고정 게시글 리스트
    // 고정 상태(fixStatus)값만 호출
    @GetMapping("fixed")
    @Operation( summary = "고정 게시글 리스트 API")
    public ResponseEntity<?> fixedNoticeList(){
        List<NoticeListResponse> fixedList = noticeService.getFixedNoticeList();
        return ResponseEntity.ok(ApiResponse.success(fixedList));
    }

    // 제목 또는 작성자로 공지사항 리스트 조회
    @GetMapping("/search")
    @Operation( summary = "타입별 리스트 조회 API")
    public ResponseEntity<?> searchNoticeList(
            @RequestParam(name = "search_type") String searchType,
            @RequestParam(name = "keyword") String keyword
    ){
        List<NoticeListResponse> findByTypeList = noticeService.findByTypeList(searchType, keyword);
        return ResponseEntity.ok(ApiResponse.success(findByTypeList));
    }

    // 공지사항 등록
    @PostMapping("/write")
    @Operation( summary = "공지사항 등록")
    public ResponseEntity<?> noticeWrite(@RequestBody NoticeDetailRequest writeRequest){
        noticeService.noticeWrite(writeRequest);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // 아이디로 상세내역 조회
    @GetMapping("/detail")
    @Operation( summary = "아이디로 상세내역 조회")
    public ResponseEntity<?> noticeDetail(@RequestParam(name = "notice_id") Long notice_id){
        NoticeDetailResponse findByNoticeId = noticeService.findByNoticeId(notice_id);
        return ResponseEntity.ok(ApiResponse.success(findByNoticeId));
    }

    // 공지사항 수정
    @PutMapping("/update")
    @Operation( summary = "공지사항 수정")
    public ResponseEntity<?> noticeUpdate(@RequestBody NoticeDetailRequest updateRequest){
        noticeService.updateNotice(updateRequest);
        return ResponseEntity.ok(ApiResponse.success("true"));
    }

    // 아이디로 공지사항 삭제
    @DeleteMapping("/delete")
    @Operation( summary = "공지사항 삭제")
    public ResponseEntity<?> noticeDelete(@RequestParam(name = "notice_id") Long notice_id){
        noticeService.deleteNotice(notice_id);
        return ResponseEntity.ok(ApiResponse.success("true"));
    }

    // 게시글 고정으로 바꾸기
    @PutMapping("/fixStatus/change")
    @Operation( summary = "게시글 고정상태 변화")
    public ResponseEntity<?> noticeChangeFixStatus(@RequestParam(name = "notice_id") Long notice_id ){
        noticeService.fixStatusChange(notice_id);

        return ResponseEntity.ok(ApiResponse.success("true"));
    }
}
