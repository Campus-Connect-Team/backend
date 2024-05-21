package com.campusconnect.backend.favorite.service;

import com.campusconnect.backend.board.domain.Board;
import com.campusconnect.backend.board.repository.BoardRepository;
import com.campusconnect.backend.favorite.domain.Favorite;
import com.campusconnect.backend.favorite.repository.FavoriteRepository;
import com.campusconnect.backend.user.domain.User;
import com.campusconnect.backend.user.repository.UserRepository;
import com.campusconnect.backend.util.exception.CustomException;
import com.campusconnect.backend.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final FavoriteRepository favoriteRepository;

    /** 관심 상품(게시글)으로 등록 */
    @Transactional
    public Favorite saveFavorite(Long boardId, String studentNumber) {
        // 이미 관심 게시글로 등록했던 회원인지 검증
        if (favoriteRepository.validateToDuplicationFavorite(boardId, studentNumber)) {
            throw new CustomException(ErrorCode.CANNOT_INCREASE_BOARD_FAVORITE_COUNT);
        }

        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        User findUser = userRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Favorite favorite = Favorite.builder()
                .user(findUser)
                .board(findBoard)
                .build();

        findBoard.increaseFavoriteCount();
        return favoriteRepository.save(favorite);
    }

    /** 관심 상품(게시글)으로 등록 취소 */
    @Transactional
    public void cancelFavorite(Long boardId, String studentNumber) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        favoriteRepository.deleteToFavoriteBoard(boardId, studentNumber);

        findBoard.decreaseFavoriteCount();
    }

    /** 게시글 삭제 시 해당 게시글과 관련된 관심 게시글 내역도 모두 삭제 **/
    @Transactional
    public void deleteAllFavorites(Long boardId) {
        favoriteRepository.deleteAllFavorites(boardId);
    }
}
