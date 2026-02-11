// 나중에 API 붙일 때 바꿀것여
import { useMemo, useState } from "react";
import { toast } from "sonner";
import { generateMockNoiseEvents } from "@/mocks/noiseMocks";

export function useNoiseState() {
  const [events, setEvents] = useState(generateMockNoiseEvents());
  const [currentPage, setCurrentPage] = useState(1);
  const [selectedEvent, setSelectedEvent] = useState(null);

  const [showDetailModal, setShowDetailModal] = useState(false);
  const [showPolicyModal, setShowPolicyModal] = useState(false);
  const [showHabitualAreaPage, setShowHabitualAreaPage] = useState(false);

  const [adminMemo, setAdminMemo] = useState("");
  const [viewMode, setViewMode] = useState("all"); // all | day | night
  const [eventFilter, setEventFilter] = useState("all"); // all | pending | completed

  const itemsPerPage = 10;

  /* ================== 계산 로직 ================== */

  const filteredEvents = useMemo(() => {
    return events.filter((e) => {
      if (eventFilter === "pending") return e.status === "대기";
      if (eventFilter === "completed") return e.status === "승인" || e.status === "보류";
      return true;
    });
  }, [events, eventFilter]);

  const totalPages = Math.ceil(filteredEvents.length / itemsPerPage);

  const paginatedEvents = useMemo(() => {
    const start = (currentPage - 1) * itemsPerPage;
    return filteredEvents.slice(start, start + itemsPerPage);
  }, [filteredEvents, currentPage]);

  const pendingCount = useMemo(() => events.filter((e) => e.status === "대기").length, [events]);

  /* ================== handlers ================== */

  const openDetail = (event) => {
    setSelectedEvent(event);
    setShowDetailModal(true);
    setAdminMemo("");
  };

  const approveEvent = (event) => {
    setEvents((prev) => prev.map((e) => (e.id === event.id ? { ...e, status: "승인" } : e)));
    toast.success("이벤트가 승인되었습니다");
  };

  const rejectEvent = (event) => {
    setEvents((prev) => prev.map((e) => (e.id === event.id ? { ...e, status: "보류" } : e)));
    toast.success("이벤트가 보류되었습니다");
  };

  return {
    /* state */
    events,
    paginatedEvents,
    currentPage,
    totalPages,
    selectedEvent,
    showDetailModal,
    showPolicyModal,
    showHabitualAreaPage,
    adminMemo,
    viewMode,
    eventFilter,
    pendingCount,

    /* setters */
    setCurrentPage,
    setShowDetailModal,
    setShowPolicyModal,
    setShowHabitualAreaPage,
    setAdminMemo,
    setViewMode,
    setEventFilter,

    /* handlers */
    openDetail,
    approveEvent,
    rejectEvent,
  };
}
