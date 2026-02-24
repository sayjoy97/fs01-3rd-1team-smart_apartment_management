import { Button, Modal, ScrollView, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import React, { useEffect, useState } from "react";
import Header from "@/components/Header";
import { Scroll } from "lucide-react";
import { fixedNoticeList, noticeAllList, noticeDetail } from "@/api/notice";
import { Bell, X } from "lucide-react";

interface Notice {
  noticeId: string;
  noticeTitle: string;
  createdAt: string;
  adminName: string;
  fixStatus?: boolean;
  noticeContent?: string;
}

export default function NoticeScreen() {
  const [currentPage, setCurrentPage] = useState(1); // 페이지 상태
  const [noticeList, setNoticeList] = useState<Notice[]>([]); // 전체 공지사항 리스트
  const [fixedList, setFixedNoticeList] = useState<Notice[]>([]); // 고정 공지사항 리스트
  const [totalPages, setTotalPages] = useState(1);
  const [notice, setNotice] = useState<Notice | null>(null);
  const [noticeId, setNoticeId] = useState<string | null>(null);

  const [fixedOpen, setFixedOpen] = useState(true);
  const [modalVisible, setModalVisible] = useState(false);

  const formatDate = (iso: string) => {
    const d = new Date(iso);
    return `${d.getFullYear()}-${(d.getMonth() + 1)
      .toString()
      .padStart(2, "0")}-${d.getDate().toString().padStart(2, "0")} ${d
      .getHours()
      .toString()
      .padStart(2, "0")}:${d.getMinutes().toString().padStart(2, "0")}`;
  };

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
    const size = 5;

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

  // 상세 조회
  useEffect(() => {
    if (!noticeId) return;

    noticeDetail(noticeId)
      .then((res) => {
        setNotice(res.data);
        setModalVisible(true);
      })
      .catch(console.error);
  }, [noticeId]);

  return (
    <View style={{ flex: 1 }}>
      <Header title="아파트 공지조회" />
      <ScrollView
        style={[styles.container, { flex: 1 }]}
        contentContainerStyle={{ paddingBottom: 40 }}
        showsVerticalScrollIndicator={false}
      >
        {/* 고정 공지 */}
        {fixedList.length > 0 && (
          <View style={styles.card}>
            <TouchableOpacity onPress={() => setFixedOpen(!fixedOpen)}>
              <Text style={styles.sectionTitle}>
                {fixedOpen ? "▼" : "▶"} 고정 공지 ({fixedList.length})
              </Text>
            </TouchableOpacity>
            {fixedOpen &&
              fixedList.map((notice) => (
                <TouchableOpacity
                  key={notice.noticeId}
                  style={styles.noticeItem}
                  onPress={() => setNoticeId(notice.noticeId)}
                >
                  <View style={styles.row}>
                    <Text style={styles.noticeTitle}>{notice.noticeTitle}</Text>

                    <Text style={styles.writer}>작성자: {notice.adminName}</Text>
                  </View>
                  <Text style={styles.noticeDate}>{formatDate(notice.createdAt)}</Text>
                </TouchableOpacity>
              ))}
          </View>
        )}
        {noticeList.map((notice, index) => (
          <TouchableOpacity
            key={notice.noticeId}
            style={[
              styles.noticeContent,
              index !== noticeList.length - 1 && { marginBottom: 12 }, // 마지막 요소 제외
            ]}
            onPress={() => setNoticeId(notice.noticeId)}
          >
            <View style={styles.list}>
              <View style={styles.row}>
                <Text style={styles.id}> #{notice.noticeId}</Text>
                <Text style={styles.title}>{notice.noticeTitle}</Text>
              </View>

              <Text style={styles.noticeDate}>{formatDate(notice.createdAt)}</Text>
            </View>
          </TouchableOpacity>
        ))}
        {/* 페이징 버튼 */}
        {totalPages > 1 && (
          <View style={styles.pagination}>
            <Button
              title="이전"
              disabled={currentPage === 1}
              onPress={() => setCurrentPage(Math.max(1, currentPage - 1))}
            />
            <Text style={styles.pageInfo}>
              {currentPage} / {totalPages}
            </Text>
            <Button
              title="다음"
              disabled={currentPage === totalPages}
              onPress={() => setCurrentPage(Math.min(totalPages, currentPage + 1))}
            />
          </View>
        )}
      </ScrollView>

      {/* 상세 조회 */}
      <Modal
        visible={modalVisible}
        animationType="slide"
        transparent={true}
        onRequestClose={() => setModalVisible(false)}
      >
        <View style={styles.modalBackground}>
          <View style={styles.modalContainer}>
            {notice ? ( // notice가 존재할 때만 렌더링
              <>
                {notice.fixStatus && (
                  <View style={styles.importantBanner}>
                    <Text style={styles.importantText}>
                      <Bell style={{ color: "#733e0a", width: "15px", height: "15px" }} /> 중요 공지
                    </Text>
                  </View>
                )}

                <Text style={styles.modalTitle}>제목: {notice.noticeTitle}</Text>
                <Text style={styles.modalDate}>작성일: {formatDate(notice.createdAt)}</Text>

                <ScrollView style={{ marginTop: 8 }}>
                  <Text style={styles.modalContent}>{notice.noticeContent}</Text>
                </ScrollView>

                <TouchableOpacity style={styles.closeButton} onPress={() => setModalVisible(false)}>
                  <Text style={styles.closeButtonText}>닫기</Text>
                </TouchableOpacity>
              </>
            ) : (
              <Text>공지사항 정보를 불러오는 중...</Text> // 데이터 없을 때
            )}
          </View>
        </View>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    marginTop: 20,
    padding: 10,
  },
  card: {
    backgroundColor: "#fefce8",
    padding: 10,
    borderRadius: 12,
    marginBottom: 16,
    elevation: 2,
    shadowColor: "#000",
    shadowOpacity: 0.05,
    shadowRadius: 5,
    shadowOffset: { width: 0, height: 2 },
  },
  noticeItem: {
    backgroundColor: "#fffff",
    padding: 8,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#cab299",
    marginBottom: 8,
  },
  row: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 5,
  },
  sectionTitle: {
    marginBottom: 10,
  },
  noticeTitle: {
    fontSize: 18,
    fontWeight: "500",
    color: "#733e0a",
  },
  writer: {
    fontSize: 12,
  },
  noticeDate: {
    fontSize: 12,
    color: "#6B7280",
  },
  emptyContainer: {},
  emptyText: {},
  content: {
    gap: 10,
  },
  list: {
    backgroundColor: "#f9fafb",
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    padding: 16,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#d1d5db",
  },
  id: {
    marginRight: 8,
  },
  title: {
    color: "black",
    fontSize: 16,
  },
  noticeHeader: {},
  noticeText: {},
  importantTitle: {},
  noticeContent: {},
  pagination: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginVertical: 12,
  },
  pageBtn: {
    padding: 10,
    backgroundColor: "#3B82F6",
    borderRadius: 6,
  },
  disableButton: {
    opacity: 0.5,
  },
  activePageButton: {
    backgroundColor: "#3B82F6",
  },
  activePageText: {
    color: "#ffffff",
    fontWeight: "500",
  },
  modalBackground: {
    flex: 1,
    backgroundColor: "rgba(0,0,0,0.5)",
    justifyContent: "center",
    alignItems: "center",
    padding: 16,
  },
  modalContainer: {
    width: "100%",
    maxHeight: "80%",
    backgroundColor: "#fff",
    borderRadius: 12,
    padding: 16,
    elevation: 5,
  },
  importantBanner: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#fef9c2",
    paddingVertical: 6,
    paddingHorizontal: 12,
    borderRadius: 8,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: "#c5b9ab",
  },
  pinIcon: {
    marginRight: 6,
    color: "#733e0a",
  },
  importantText: {
    color: "#733e0a",
    fontSize: 14,
  },
  modalTitle: {
    color: "#733e0a",
    fontSize: 18,
    fontWeight: "600",
  },
  modalDate: {
    fontSize: 12,
    color: "#6B7280",
    marginBottom: 12,
  },
  modalContentWrapper: {
    maxHeight: 200,
    marginBottom: 16,
  },
  modalContent: {
    maxHeight: 100,
    height: 100,
    fontSize: 14,
    lineHeight: 20,
    padding: 10,
    color: "#111",
    backgroundColor: "#fefce8",
    borderRadius: 8,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: "#c5b9ab",
  },
  closeButton: {
    alignSelf: "flex-end",
    paddingVertical: 8,
    paddingHorizontal: 16,
    backgroundColor: "#3B82F6",
    borderRadius: 8,
  },
  closeButtonText: {
    color: "#fff",
    fontWeight: "500",
    fontSize: 14,
  },
});
