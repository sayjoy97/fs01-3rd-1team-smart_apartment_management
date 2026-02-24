import Header from "@/components/Header";
import { useEffect, useState } from "react";
import {
  FlatList,
  Modal,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
  Button,
} from "react-native";

import { Picker } from "@react-native-picker/picker";
import {
  complaintDeleteApi,
  complaintDetailApi,
  complaintListApi,
  complaintUpdateApi,
  complaintWrtieApi,
} from "@/api/UserApi";
import Toast from "react-native-toast-message";
import { toast } from "sonner";

// 카테고리 매핑
const categoryOptions = [
  { label: "엘리베이터", value: "ELEVATOR" },
  { label: "정원", value: "GARDEN" },
  { label: "소음", value: "NOISE" },
  { label: "주차", value: "PARKING" },
  { label: "기타", value: "OTHER" },
];

interface Complaint {
  complaintId: number;
  title: string;
  category: string;
  content: string;
  status: "WAITING" | "ANSWERED";
  createAt: string;
}

interface ComplaintDetail {
  complaintId: number;
  category: string;
  title: string;
  createAt: string;
  replyAt?: string;
  content: string;
  answer?: string;
  adminName?: string;
  canEdit: boolean;
  canDelete: boolean;
  referencedComplaints?: ComplaintDetail[];
}

export default function ComplaintScreen() {
  const [mode, setMode] = useState<"write" | "view">("write");

  // 민원 작성 초기값
  const [title, setTitle] = useState("");
  const [category, setCategory] = useState("");
  const [content, setContent] = useState("");

  // 민원 상세 값
  const [detailTitle, setDetailTitle] = useState("");
  const [detailCategory, setDetailCategory] = useState("");
  const [detailContent, setDetailContent] = useState("");

  // 민원 선택
  const [complaints, setComplaints] = useState([]);
  const [selectedComplaint, setSelectedComplaint] = useState<ComplaintDetail | null>(null);
  const [selectedReferenceComplaint, setSelectedReferenceComplaint] =
    useState<ComplaintDetail | null>(null);

  // 민원 참조
  const [readOnly, setReadOnly] = useState(false);
  const [referenceModal, setShowReferenceModal] = useState(false);
  const [referencedComplaints, setReferencedComplaints] = useState<Partial<Complaint>[]>([]);
  const [referencedIds, setReferencedIds] = useState<number[]>([]);

  const [currentPage, setCurrentPage] = useState(1);

  const formatDate = (dateStr: string) => {
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = ("0" + (date.getMonth() + 1)).slice(-2);
    const day = ("0" + date.getDate()).slice(-2);
    const hours = ("0" + date.getHours()).slice(-2);
    const minutes = ("0" + date.getMinutes()).slice(-2);

    return `${year}년 ${month}월 ${day}일 ${hours}:${minutes}`;
  };

  useEffect(() => {
    const init = async () => {
      try {
        const [complaintRes] = await Promise.all([complaintListApi()]);

        setComplaints(complaintRes.data);
      } catch (error) {
        console.error(error);
      }
    };

    init();
  }, []);

  useEffect(() => {
    if (selectedComplaint) {
      setDetailTitle(selectedComplaint.title);
      setDetailCategory(selectedComplaint.category);
      setDetailContent(selectedComplaint.content);

      setReferencedIds(
        (selectedComplaint.referencedComplaints || []).map((ref) => ref.complaintId),
      );

      setReferencedComplaints(
        (selectedComplaint.referencedComplaints || []).map((ref) => ({
          complaintId: ref.complaintId,
          title: ref.title,
        })),
      );
    } else {
      setReferencedIds([]);
      setReferencedComplaints([]);
    }
  }, [selectedComplaint]);

  const handleReset = () => {
    setTitle("");
    setCategory("");
    setContent("");
    setReferencedComplaints([]);
  };

  const handleSubmit = async () => {
    if (!title || !category || !content) {
      Toast.show({
        type: "error",
        text1: "민원 작성 실패",
        text2: "모든 항목을 입력해주세요",
        position: "top",
      });
      return;
    }

    try {
      const numericIds = referencedIds.map((id) => Number(id));
      await complaintWrtieApi({
        title,
        category,
        content,
        referenceId: numericIds,
      });

      const res = await complaintListApi();
      setComplaints(res.data);

      handleReset();
      setReferencedIds([]);
      setReferencedComplaints([]);
      Toast.show({
        type: "success",
        text1: "민원 작성 완료",
        text2: "민원이 정상적으로 등록되었습니다.",
        position: "top",
      });
    } catch (error: unknown) {
      if (error instanceof Error) {
        alert(error.message);
      } else {
        alert("알 수 없는 오류가 발생했습니다.");
      }
    }
  };

  const handleSelectComplaint = async (complaintId: number) => {
    try {
      const res = await complaintDetailApi(complaintId);
      setSelectedComplaint(res.data);
      console.log(res.data);
    } catch (error) {
      console.error(error);
    }
  };

  // 삭제
  const handleDelete = async (complaintId: number) => {
    try {
      await complaintDeleteApi(complaintId);
      Toast.show({
        type: "success",
        text1: "민원 삭제 완료",
        text2: "민원이 정상적으로 삭제되었습니다.",
        position: "top",
      });

      setSelectedComplaint(null);

      const res = await complaintListApi();
      setComplaints(res.data);
    } catch (err: any) {
      console.error("삭제 실패: ", err.message);
      Toast.show({
        type: "error",
        text1: "민원 삭제 실패",
        text2: "민원 삭제를 실패했습니다.",
        position: "top",
      });
    }
  };

  // 수정
  const handleEdit = async (complaintId: number, updateData: any) => {
    if (!detailTitle || !detailCategory || !detailContent) {
      Toast.show({
        type: "error",
        text1: "민원 수정 실패",
        text2: "모든 항목을 입력해주세요",
        position: "top",
      });
      return;
    }
    try {
      await complaintUpdateApi(complaintId, updateData);
      Toast.show({
        type: "success",
        text1: "민원 수정 완료",
        text2: "민원이 정상적으로 수정되었습니다.",
        position: "top",
      });

      setSelectedComplaint(null);
      const res = await complaintListApi();
      setComplaints(res.data);

      setReferencedIds([]);
      setReferencedComplaints([]);
    } catch (err: any) {
      alert(err.message);
      Toast.show({
        type: "error",
        text1: "민원 수정 실패",
        text2: "민원 수정이 실패했습니다.",
        position: "top",
      });
    }
  };

  // 카테고리 값 변환
  const getCategoryLabel = (value: string) => {
    const option = categoryOptions.find((o) => o.value === value);
    return option ? option.label : value;
  };

  // 민원 참조 버튼
  const handleReferenceSelect = (item: Complaint) => {
    const exists = referencedIds.includes(item.complaintId);

    if (exists) {
      // 이미 참조된 경우 제거 (취소)
      setReferencedIds((prev) => prev.filter((id) => id !== item.complaintId));
      setReferencedComplaints((prev) => prev.filter((c) => c.complaintId !== item.complaintId));
    } else {
      // 참조 추가
      setReferencedIds((prev) => [...prev, item.complaintId]);
      setReferencedComplaints((prev) => [...prev, item]);
    }
  };

  const handleReferenceDetail = async (item: Complaint) => {
    try {
      // 상세보기 API 호출
      const detail = await complaintDetailApi(item.complaintId);
      setSelectedReferenceComplaint(detail.data);
      setReadOnly(true);
    } catch (err) {
      console.error(err);
      toast.error("상세보기 불러오기 실패");
    }
  };

  const itemsPerPage = 4;
  const paginatedComplaints = complaints.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage,
  );

  const totalPages = Math.ceil(complaints.length / itemsPerPage);

  return (
    <View style={{ flex: 1 }}>
      <Header title="민원" />

      <ScrollView
        style={[styles.container, { flex: 1 }]}
        contentContainerStyle={{ paddingBottom: 40 }}
        showsVerticalScrollIndicator={false}
      >
        {/* 토글 버튼 */}
        <View style={styles.toggleContainer}>
          <TouchableOpacity
            style={[
              styles.toggleButton,
              mode === "write" ? styles.toggleActive : styles.toggleInactive,
            ]}
            onPress={() => setMode("write")}
          >
            <Text style={mode === "write" ? styles.toggleActiveText : styles.toggleInactiveText}>
              민원 작성
            </Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[
              styles.toggleButton,
              mode === "view" ? styles.toggleActive : styles.toggleInactive,
            ]}
            onPress={() => setMode("view")}
          >
            <Text style={mode === "view" ? styles.toggleActiveText : styles.toggleInactiveText}>
              민원 조회
            </Text>
          </TouchableOpacity>
        </View>

        {mode === "write" ? (
          <View style={styles.card}>
            <Text style={styles.label}>민원 제목</Text>
            <TextInput
              placeholder="민원 제목을 입력하세요"
              placeholderTextColor="#9CA3AF"
              value={title}
              onChangeText={setTitle}
              style={styles.input}
            />
            <Text style={styles.label}>카테고리</Text>
            <View style={styles.pickerConatiner}>
              <Picker
                style={{
                  width: "100%",
                  height: 50,
                  color: category ? "#111827" : "#9CA3AF",
                }}
                selectedValue={category}
                onValueChange={(itemValue) => setCategory(itemValue)}
              >
                <Picker.Item label="카테고리를 선택하세요" value="" color="#9CA3AF" />
                {categoryOptions.map((o) => (
                  <Picker.Item key={o.value} label={o.label} value={o.value}></Picker.Item>
                ))}
              </Picker>
            </View>
            <Text style={styles.label}>민원 내용</Text>
            <TextInput
              placeholder="민원 내용을 입력하세요"
              placeholderTextColor="#9CA3AF"
              value={content}
              onChangeText={setContent}
              style={[styles.input, styles.textare]}
              multiline
            />
            {referencedComplaints.length > 0 && (
              <View style={styles.referenceContainer}>
                <Text style={styles.referenceTitle}>참조된 민원</Text>

                <ScrollView
                  style={styles.referenceList}
                  nestedScrollEnabled
                  showsVerticalScrollIndicator
                >
                  {referencedComplaints.map((r) => (
                    <Text key={r.complaintId} style={styles.referenceItem}>
                      • {r.title}
                    </Text>
                  ))}
                </ScrollView>
              </View>
            )}

            <View style={{ flexDirection: "row", marginTop: 12, gap: 8 }}>
              <TouchableOpacity style={styles.buttonSecondary} onPress={handleReset}>
                <Text style={styles.buttonSecondaryText}>초기화</Text>
              </TouchableOpacity>
              <TouchableOpacity style={styles.buttonPrimary} onPress={handleSubmit}>
                <Text style={styles.buttonPrimaryText}>민원 접수</Text>
              </TouchableOpacity>
            </View>
            <TouchableOpacity
              style={styles.referenceButton}
              onPress={() => setShowReferenceModal(true)}
            >
              <Text style={styles.buttonReferenceText}>이전 민원 참조</Text>
            </TouchableOpacity>
          </View>
        ) : (
          <View>
            {complaints.length > 0 ? (
              <FlatList<Complaint>
                data={paginatedComplaints}
                keyExtractor={(item) => item.complaintId.toString()}
                contentContainerStyle={{ paddingBottom: 40 }} // 리스트 아래 여백
                showsVerticalScrollIndicator={false}
                renderItem={({ item }) => (
                  <TouchableOpacity
                    style={styles.complaintCard} // 스타일 적용
                    onPress={() => handleSelectComplaint(item.complaintId)}
                  >
                    <View style={styles.complaintHeader}>
                      <Text style={styles.complaintTitle}>{item.title}</Text>
                      <Text style={styles.complaintCategory}>
                        {getCategoryLabel(item.category)}
                      </Text>
                    </View>

                    <View style={styles.complaintInfo}>
                      <Text style={styles.complaintDate}>{formatDate(item.createAt)}</Text>
                      <Text
                        style={[
                          styles.complaintStatus,
                          item.status === "ANSWERED" ? styles.answered : styles.pending,
                        ]}
                      >
                        {item.status === "ANSWERED" ? "답변완료" : "대기중"}
                      </Text>
                    </View>
                  </TouchableOpacity>
                )}
              />
            ) : (
              <View style={styles.emptyContainer}>
                <Text style={styles.emptyText}>현재 작성한 민원이 없습니다</Text>
              </View>
            )}

            {/* 페이징 버튼 */}
            {totalPages > 1 && (
              <View style={styles.pagination}>
                <Button
                  title="이전"
                  disabled={currentPage === 1}
                  onPress={() => setCurrentPage(Math.max(1, currentPage - 1))}
                />
                <Text>
                  {currentPage} / {totalPages}
                </Text>
                <Button
                  title="다음"
                  disabled={currentPage === totalPages}
                  onPress={() => setCurrentPage(Math.min(totalPages, currentPage + 1))}
                />
              </View>
            )}
          </View>
        )}

        {/* 상세보기 모달 */}
        <Modal visible={!!selectedComplaint} transparent>
          <View style={styles.modalBackground}>
            {selectedComplaint && (
              <View style={styles.modalContent}>
                <Text style={styles.label}>제목</Text>
                {selectedComplaint.canEdit ? (
                  <TextInput
                    style={styles.input}
                    value={detailTitle}
                    onChangeText={setDetailTitle}
                  />
                ) : (
                  <Text>{selectedComplaint.title}</Text>
                )}

                <Text style={styles.label}>카테고리</Text>
                {selectedComplaint.canEdit ? (
                  <Picker
                    selectedValue={detailCategory}
                    onValueChange={(itemValue) => setDetailCategory(itemValue)}
                  >
                    <Picker.Item
                      label={getCategoryLabel(selectedComplaint.category)}
                      value={selectedComplaint.category}
                    />
                    {categoryOptions.map((o) => (
                      <Picker.Item key={o.value} label={o.label} value={o.value}></Picker.Item>
                    ))}
                  </Picker>
                ) : (
                  <Text>{getCategoryLabel(selectedComplaint.category)}</Text>
                )}
                <Text style={styles.label}>작성한 민원 내용</Text>
                {selectedComplaint.canEdit ? (
                  <TextInput
                    style={[styles.input, { height: 100 }]}
                    value={detailContent}
                    onChangeText={setDetailContent}
                    multiline
                  />
                ) : (
                  <Text>{selectedComplaint.content}</Text>
                )}

                {/* 참조 민원 표시 */}
                {referencedComplaints.length > 0 && (
                  <View style={styles.referenceContainer}>
                    <Text style={styles.referenceTitle}>참조된 민원</Text>

                    <ScrollView
                      style={styles.referenceList}
                      nestedScrollEnabled
                      showsVerticalScrollIndicator={true}
                    >
                      {referencedComplaints.map((r) => (
                        <Text key={r.complaintId} style={styles.referenceItem}>
                          • {r.title}
                        </Text>
                      ))}
                    </ScrollView>
                  </View>
                )}

                <Text style={styles.referenceAdmin}>
                  답변자: {selectedComplaint.adminName ?? "-"}
                </Text>
                <Text style={{ color: selectedComplaint.answer ? "green" : "#dc362e" }}>
                  {selectedComplaint.answer ?? "아직 답변이 없습니다."}
                </Text>
                {selectedComplaint.answer && (
                  <Text style={{ marginBottom: 12 }}>답변일: {selectedComplaint.replyAt}</Text>
                )}

                {/* 참조 버튼 */}
                <TouchableOpacity
                  style={{
                    flex: 1,
                    backgroundColor: selectedComplaint.canEdit ? "#4f46e5" : "#999",
                    padding: 8,
                    borderRadius: 6,
                    marginTop: 10,
                  }}
                  disabled={!selectedComplaint.canEdit}
                  onPress={() => {
                    setSelectedComplaint(selectedComplaint);
                    setReferencedComplaints(
                      (selectedComplaint.referencedComplaints || []).map((ref) => ({
                        complaintId: ref.complaintId,
                        title: ref.title,
                      })),
                    );
                    setShowReferenceModal(true);
                  }}
                >
                  <Text style={{ color: "#fff", textAlign: "center" }}>참조</Text>
                </TouchableOpacity>
                <View style={{ flexDirection: "row", marginTop: 12, gap: 8 }}>
                  <TouchableOpacity
                    style={{
                      flex: 1,
                      padding: 12,
                      backgroundColor: selectedComplaint.canDelete ? "#f87171" : "#ccc",
                      borderRadius: 8,
                      alignItems: "center",
                    }}
                    disabled={!selectedComplaint.canDelete}
                    onPress={() => {
                      if (selectedComplaint.canDelete) {
                        handleDelete(selectedComplaint.complaintId);
                      } else {
                        toast.error("답변이 달린 민원은 삭제할 수 없습니다");
                      }
                    }}
                  >
                    <Text style={{ color: "#ffffff", fontWeight: "bold" }}>삭제</Text>
                  </TouchableOpacity>
                  <TouchableOpacity
                    style={{
                      flex: 1,
                      padding: 12,
                      backgroundColor: selectedComplaint.canEdit ? "#3b82f6" : "#ccc",
                      borderRadius: 8,
                      alignItems: "center",
                    }}
                    disabled={!selectedComplaint.canEdit}
                    onPress={() => {
                      console.log("보내는 값:", {
                        title: detailTitle,
                        category: detailCategory,
                        content: detailContent,

                        referenceId: referencedIds,
                      });
                      if (selectedComplaint.canEdit) {
                        handleEdit(selectedComplaint.complaintId, {
                          title: detailTitle,
                          category: detailCategory,
                          content: detailContent,
                          referenceId: referencedIds,
                        });
                      } else {
                        toast.error("답변이 달린 민원은 수정할 수 없습니다");
                      }
                    }}
                  >
                    <Text style={styles.buttonPrimaryText}>수정</Text>
                  </TouchableOpacity>
                </View>
                <TouchableOpacity
                  onPress={() => setSelectedComplaint(null)}
                  style={styles.closeBtn}
                >
                  <Text style={{ textAlign: "center" }}>닫기</Text>
                </TouchableOpacity>
              </View>
            )}
          </View>
        </Modal>

        {/* 참조 모달 */}
        <Modal visible={referenceModal} transparent>
          <View style={{ flex: 1, backgroundColor: "#000000aa", justifyContent: "center" }}>
            <View
              style={{
                margin: 16,
                backgroundColor: "#fff",
                borderRadius: 8,
                padding: 16,
                maxHeight: "80%",
              }}
            >
              <Text style={{ fontWeight: "bold", marginBottom: 12 }}>이전 민원 선택</Text>
              <FlatList<Complaint>
                data={complaints}
                keyExtractor={(item) => item.complaintId.toString()}
                renderItem={({ item }) => (
                  <View
                    style={{
                      marginBottom: 8,
                      borderBottomWidth: 1,
                      borderColor: "#ccc",
                      paddingBottom: 8,
                    }}
                  >
                    <Text>{item.title}</Text>
                    <View style={{ flexDirection: "row", marginTop: 4, gap: 8 }}>
                      <TouchableOpacity
                        style={{ flex: 1, backgroundColor: "#ddd", padding: 8, borderRadius: 6 }}
                        onPress={() => {
                          handleReferenceDetail(item);
                        }}
                      >
                        <Text>상세보기</Text>
                      </TouchableOpacity>
                      <TouchableOpacity
                        style={{
                          flex: 1,
                          backgroundColor: (referencedComplaints || []).some(
                            (c) => c.complaintId === item.complaintId,
                          )
                            ? "#999"
                            : "#4f46e5",
                          padding: 8,
                          borderRadius: 6,
                        }}
                        onPress={() => handleReferenceSelect(item)}
                      >
                        <Text style={{ color: "#fff", textAlign: "center" }}>
                          {(referencedComplaints || []).some(
                            (c) => c.complaintId === item.complaintId,
                          )
                            ? "참조됨"
                            : "참조"}
                        </Text>
                      </TouchableOpacity>
                    </View>
                  </View>
                )}
              />
              {/* 민원 참조 상세보기 */}
              <Modal visible={!!selectedReferenceComplaint} transparent animationType="fade">
                <View
                  style={{
                    flex: 1,
                    backgroundColor: "#000000aa",
                    justifyContent: "center",
                    padding: 16,
                  }}
                >
                  <View style={{ backgroundColor: "#fff", borderRadius: 8, padding: 16 }}>
                    {selectedReferenceComplaint && (
                      <>
                        <ScrollView
                          style={{ maxHeight: 300, padding: 12 }} // 높이 제한을 두고 스크롤 가능
                          showsVerticalScrollIndicator={true}
                        >
                          <Text style={styles.referenceTitle}>
                            작성한 민원 제목: {selectedReferenceComplaint.title}
                          </Text>

                          <Text style={styles.referenceContent}>
                            작성한 내용: {selectedReferenceComplaint.content}
                          </Text>

                          <Text style={styles.referenceAdmin}>
                            답변자: {selectedReferenceComplaint.adminName}
                          </Text>

                          <Text
                            style={[
                              styles.referenceAnswer,
                              { color: selectedReferenceComplaint.answer ? "green" : "#dc362e" },
                            ]}
                          >
                            {selectedReferenceComplaint.answer ?? "아직 답변이 없습니다."}
                          </Text>

                          {selectedReferenceComplaint.answer && (
                            <Text style={styles.referenceReplyAt}>
                              답변일: {selectedReferenceComplaint.replyAt}
                            </Text>
                          )}
                        </ScrollView>

                        <TouchableOpacity
                          onPress={() => setSelectedReferenceComplaint(null)}
                          style={{ padding: 12, backgroundColor: "#ccc", borderRadius: 6 }}
                        >
                          <Text style={{ textAlign: "center" }}>닫기</Text>
                        </TouchableOpacity>
                      </>
                    )}
                  </View>
                </View>
              </Modal>

              <TouchableOpacity
                onPress={() => setShowReferenceModal(false)}
                style={{ marginTop: 12, padding: 12, backgroundColor: "#ccc", borderRadius: 6 }}
              >
                <Text style={{ textAlign: "center" }}>닫기</Text>
              </TouchableOpacity>
            </View>
          </View>
        </Modal>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 16,
  },
  toggleContainer: {
    flexDirection: "row",
    borderRadius: 8,
    marginBottom: 16,
    gap: 8,
  },
  toggleButton: {
    flex: 1,
    paddingVertical: 10,
    borderRadius: 8,
    alignItems: "center",
    textAlign: "center",
    padding: 8,
  },
  toggleActive: {
    backgroundColor: "#2563EB",
  },
  toggleInactive: {
    backgroundColor: "#dee0e2",
  },
  toggleActiveText: {
    color: "#fff",
    fontSize: 16,
  },
  toggleInactiveText: {
    color: "#4B5563",
    fontSize: 16,
  },
  card: {
    backgroundColor: "#fff",
    borderRadius: 12,
    padding: 16,
    shadowColor: "#000",
    shadowOpacity: 0.05,
    shadowRadius: 5,
    shadowOffset: { width: 0, height: 2 },
    marginBottom: 12,
  },
  input: {
    borderWidth: 1,
    borderColor: "#E5E7EB",
    borderRadius: 10,
    padding: 12,
    fontSize: 14,
    backgroundColor: "#F9FAFB",
    marginBottom: 16,
  },
  label: {
    fontSize: 16,
    fontWeight: "400",
    marginBottom: 6,
    color: "#374151",
  },
  textare: {
    height: 120,
    textAlignVertical: "top",
  },
  pickerConatiner: {
    borderWidth: 1,
    borderColor: "#E5E7EB",
    borderRadius: 10,
    backgroundColor: "#F9FAFB",
    marginBottom: 16,
    overflow: "hidden",
    width: "100%",
  },
  buttonPrimary: {
    flex: 1,
    paddingVertical: 12,
    backgroundColor: "#2563EB",
    borderRadius: 8,
    alignItems: "center",
  },
  buttonPrimaryText: {
    color: "#fff",
    fontWeight: "500",
  },
  buttonSecondary: {
    flex: 1,
    paddingVertical: 12,
    backgroundColor: "#E5E7EB",
    borderRadius: 8,
    alignItems: "center",
  },
  buttonSecondaryText: {
    color: "#485563",
    fontWeight: "500",
  },
  referenceButton: {
    marginTop: 10,
    flex: 1,
    paddingVertical: 12,
    borderColor: "#2563EB",
    borderWidth: 2,
    borderRadius: 8,
    alignItems: "center",
  },
  buttonReferenceText: {
    color: "#2563EB",
  },
  referenceContainer: {
    marginTop: 10,
    padding: 12,
    backgroundColor: "#EEF2FF",
    borderRadius: 12,
  },
  referenceTitle: {
    fontWeight: "bold",
    fontSize: 16,
    marginBottom: 8,
  },
  referenceContent: {
    fontSize: 14,
    marginBottom: 8,
    lineHeight: 20,
    backgroundColor: "#f3f6ff",
    padding: 6,
  },
  referenceAdmin: {
    fontSize: 14,
    marginBottom: 4,
  },
  referenceAnswer: {
    fontSize: 14,
    marginBottom: 8,
    fontWeight: "500",
    backgroundColor: "#f3f6ff",
    padding: 6,
  },
  referenceReplyAt: {
    fontSize: 12,
    color: "#6B7280",
    marginBottom: 12,
  },

  referenceList: {
    maxHeight: 120,
  },
  referenceItem: {
    fontSize: 13,
    color: "#374151",
    marginBottom: 6,
  },
  pagination: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginVertical: 8,
  },
  modalBackground: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(0, 0, 0, 0.5)",
  },
  modalContent: {
    backgroundColor: "#fff",
    padding: 16,
    borderRadius: 12,
    width: "90%",
  },
  title: {
    fontWeight: "bold",
    fontSize: 16,
  },
  category: {
    fontSize: 12,
    color: "#555",
    marginBottom: 4,
  },
  content: {
    flex: 1,
    padding: 16,
  },
  closeBtn: {
    backgroundColor: "#ccc",
    padding: 8,
    borderRadius: 6,
    marginTop: 12,
  },
  complaintCard: {
    backgroundColor: "#fff",
    borderRadius: 10,
    padding: 16,
    marginBottom: 12,
    shadowColor: "#000",
    shadowOpacity: 0.05,
    shadowRadius: 4,
    shadowOffset: { width: 0, height: 2 },
    elevation: 2,
  },
  complaintHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 8,
  },
  complaintTitle: {
    fontSize: 16,
    fontWeight: "600",
    flex: 1,
  },
  complaintCategory: {
    fontSize: 12,
    color: "#6B7280",
    marginLeft: 8,
  },
  complaintInfo: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  complaintDate: {
    fontSize: 12,
    color: "#6B7280",
  },
  complaintStatus: {
    fontSize: 12,
    fontWeight: "500",
    paddingVertical: 2,
    paddingHorizontal: 6,
    borderRadius: 6,
    overflow: "hidden",
  },
  answered: {
    backgroundColor: "#D1FAE5",
    color: "#065F46",
  },
  pending: {
    backgroundColor: "#FEF3C7",
    color: "#92400E",
  },
  emptyContainer: {
    alignItems: "center",
    marginTop: 40,
  },
  emptyText: {
    color: "#6B7280",
    fontSize: 14,
  },
});
