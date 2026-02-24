import { ScrollView, StyleSheet, Text, View } from "react-native";
import React, { useEffect, useState } from "react";
import Header from "@/components/Header";
import { Scroll } from "lucide-react";
import { fixedNoticeList, noticeAllList } from "@/api/notice";

export default function NoticeScreen() {
  const [currentPage, setCurrentPage] = useState(1); // 페이지 상태
  const [noticeList, setNoticeList] = useState([]); // 전체 공지사항 리스트
  const [fixedList, setFixedNoticeList] = useState([]); // 고정 공지사항 리스트
  const [totalPages, setTotalPages] = useState(1);
  const [notice, setNotice] = useState(null);

  // 페이지 이동
  const handlePageChange = (page: number) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
  };

  // 페이지 번호 리스트 생성
  const getPageNumbers = () => {
    const pages = [];
    for (let i = 1; i <= totalPages; i++) {
      pages.push(i);
    }
    return pages;
  };

  const fetchNoticeList = (page: number) => {
    const size = 10;

    // 검색어 없으면 전체 조회
    noticeAllList({ page, size })
      .then((res) => {
        setNoticeList(res.data.content || []);
        setTotalPages(res.data.totalPages || 1);
      })
      .catch(console.error);
    return;
  };

  useEffect(() => {
    fetchNoticeList(currentPage);

    // 고정 게시글 목록 조회
    fixedNoticeList()
      .then((res) => {
        console.log("고정 게시글 목록조회 성공: ", res.data || []);
        setFixedNoticeList(res.data);
      })
      .catch((err) => console.log("고정 게시글 조회 실패: ", err));
  }, [currentPage]);

  // // 상세 조회
  // useEffect(() => {
  //   noticeDetail(noticeId)
  //     .then((res) => {
  //       setNotice(res.data);
  //     })
  //     .catch(console.error);
  // }, [noticeId]);

  return (
    <View style={{ flex: 1 }}>
      <Header title="아파트 공지조회" />
      <ScrollView>
        <Text>공지사항 화면</Text>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({});
