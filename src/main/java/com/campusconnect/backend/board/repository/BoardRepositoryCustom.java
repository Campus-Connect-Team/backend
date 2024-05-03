package com.campusconnect.backend.board.repository;

import com.campusconnect.backend.board.domain.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardRepositoryCustom {

    // 게시글 검색(학과, 게시글 제목)
    List<Board> searchBoardWithSearchCond(String department, String title, Pageable pageable);

    // 거래 완료 건에 대한 게시글 조회
    List<Board> findTradeCompletedBoard(Long userId);

    // 회원 탈퇴 - 해당 사용자가 작성한 모든 게시글 조회
    List<Board> findBoards(Long userId);

    // 마이 페이지 - 작성한 판매 게시글 리스트 영역 조회
    List<Board> findBoardsByStudentNumber(String studentNumber);
}
