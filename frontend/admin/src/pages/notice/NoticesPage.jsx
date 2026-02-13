import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card";
import { Input } from "../../components/ui/input";
import { Button } from "../../components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../../components/ui/table";
import { Badge } from "../../components/ui/badge";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../../components/ui/select";
import { Search, Pin, ChevronLeft, ChevronRight, Plus } from "lucide-react";

import "../../App.css";

export function NoticesPage({ onNavigateToDetail, noticesData, onNavigateToCreate }) {
  const [searchType, setSearchType] = useState("all");
  const [searchInput, setSearchInput] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [searchCategory, setSearchCategory] = useState("all");
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  // 공지사항 전체 리스트 호출

  const handleSearch = () => {
    setSearchTerm(searchInput);
    setSearchCategory(searchType);
    setCurrentPage(1);
  };

  const handleKeyPress = (e) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  // 검색 필터링
  const filteredNotices = noticesData.filter((notice) => {
    if (!searchTerm) return true;

    switch (searchCategory) {
      case "title":
        return notice.title.toLowerCase().includes(searchTerm.toLowerCase());
      case "author":
        return notice.author.toLowerCase().includes(searchTerm.toLowerCase());
      case "all":
      default:
        return (
          notice.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
          notice.author.toLowerCase().includes(searchTerm.toLowerCase())
        );
    }
  });

  // 고정 공지와 일반 공지 분리
  const pinnedNotices = filteredNotices.filter((n) => n.pinned);
  const regularNotices = filteredNotices.filter((n) => !n.pinned);

  // 페이지네이션 (일반 공지만)
  const totalPages = Math.ceil(regularNotices.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentRegularNotices = regularNotices.slice(startIndex, endIndex);

  // 고정 공지 + 현재 페이지의 일반 공지
  const displayNotices = [...pinnedNotices, ...currentRegularNotices];

  const handleRowClick = (notice) => {
    onNavigateToDetail(notice);
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  // 페이지 번호 생성
  const getPageNumbers = () => {
    const pages = [];
    const maxVisiblePages = 5;

    if (totalPages <= maxVisiblePages) {
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      if (currentPage <= 3) {
        for (let i = 1; i <= 5; i++) {
          pages.push(i);
        }
      } else if (currentPage >= totalPages - 2) {
        for (let i = totalPages - 4; i <= totalPages; i++) {
          pages.push(i);
        }
      } else {
        for (let i = currentPage - 2; i <= currentPage + 2; i++) {
          pages.push(i);
        }
      }
    }

    return pages;
  };

  return (
    <div className="space-y-6">
      {/* 공지사항 등록 버튼 */}
      <div className="flex justify-end">
        <Button onClick={onNavigateToCreate} className="bg-blue-600 hover:bg-blue-700 text-white">
          <Plus className="size-4 mr-2" />
          공지사항 등록
        </Button>
      </div>

      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <CardHeader>
          <CardTitle className="dark:text-white">공지사항 목록</CardTitle>
        </CardHeader>
        <CardContent>
          {/* 검색 영역 */}
          <div className="mb-4 flex items-center gap-2">
            <Select value={searchType} onValueChange={setSearchType}>
              <SelectTrigger className="w-32 dark:bg-gray-700 dark:border-gray-600 dark:text-white">
                <SelectValue />
              </SelectTrigger>
              <SelectContent className="dark:bg-gray-700 dark:border-gray-600">
                <SelectItem value="all" className="dark:text-white">
                  전체
                </SelectItem>
                <SelectItem value="title" className="dark:text-white">
                  제목
                </SelectItem>
                <SelectItem value="author" className="dark:text-white">
                  작성자
                </SelectItem>
              </SelectContent>
            </Select>
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-gray-400" />
              <Input
                type="text"
                placeholder="검색어를 입력하세요..."
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
                onKeyPress={handleKeyPress}
                className="pl-10 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
              />
            </div>
            <Button onClick={handleSearch} className="bg-blue-600 hover:bg-blue-700">
              <Search className="size-4 mr-2" />
              검색
            </Button>
          </div>

          {/* 통합 테이블 */}
          <div className="border rounded-lg dark:border-gray-700">
            <Table>
              <TableHeader>
                <TableRow className="dark:border-gray-700">
                  <TableHead className="dark:text-gray-300">제목</TableHead>
                  <TableHead className="dark:text-gray-300">작성자</TableHead>
                  <TableHead className="dark:text-gray-300">작성날짜</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {displayNotices.length === 0 ? (
                  <TableRow>
                    <TableCell
                      colSpan={3}
                      className="text-center py-8 text-gray-500 dark:text-gray-400">
                      등록된 공지사항이 없습니다.
                    </TableCell>
                  </TableRow>
                ) : (
                  displayNotices.map((notice) => (
                    <TableRow
                      key={notice.id}
                      className={`dark:border-gray-700 cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-750 ${
                        notice.pinned ? "bg-blue-50/50 dark:bg-blue-900/10" : ""
                      }`}
                      onClick={() => handleRowClick(notice)}>
                      <TableCell className="font-medium dark:text-white">
                        <div className="flex items-center gap-2">
                          {notice.pinned && (
                            <Badge
                              variant="outline"
                              className="border-blue-500 text-blue-600 dark:border-blue-400 dark:text-blue-400">
                              고정
                            </Badge>
                          )}
                          {notice.urgent && (
                            <Badge className="bg-red-500 hover:bg-red-600">긴급</Badge>
                          )}
                          {notice.title}
                        </div>
                      </TableCell>
                      <TableCell className="dark:text-gray-300">{notice.author}</TableCell>
                      <TableCell className="dark:text-gray-300">{notice.date}</TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </div>

          {/* 페이지네이션 */}
          {totalPages > 1 && (
            <div className="flex items-center justify-center gap-2 mt-6">
              <Button
                variant="outline"
                size="sm"
                onClick={() => handlePageChange(currentPage - 1)}
                disabled={currentPage === 1}
                className="dark:bg-gray-700 dark:border-gray-600 dark:text-white disabled:opacity-50">
                <ChevronLeft className="size-4" />
              </Button>

              {getPageNumbers().map((page) => (
                <Button
                  key={page}
                  variant={currentPage === page ? "default" : "outline"}
                  size="sm"
                  onClick={() => handlePageChange(page)}
                  className={
                    currentPage === page
                      ? "bg-blue-600 hover:bg-blue-700 text-white"
                      : "dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                  }>
                  {page}
                </Button>
              ))}

              <Button
                variant="outline"
                size="sm"
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={currentPage === totalPages}
                className="dark:bg-gray-700 dark:border-gray-600 dark:text-white disabled:opacity-50">
                <ChevronRight className="size-4" />
              </Button>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
