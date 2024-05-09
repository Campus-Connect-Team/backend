package com.campusconnect.backend.favorite.repository;

import com.campusconnect.backend.favorite.domain.Favorite;

import java.util.List;

public interface FavoriteRepositoryCustom {

    // 이미 관심 게시글로 등록했던 회원인지 검증
    Boolean validateToDuplicationFavorite(Long BoardId, String studentNumber);

    // 관심 상품(게시글) 등록을 취소
    void deleteToFavoriteBoard(Long boardId, String studentNumber);

    // 게시글 삭제 시 해당 게시글과 관련된 관심 게시글 내역도 모두 삭제
    void deleteAllFavorites(Long boardId);

    // 특정 유저의 관심 상품 리스트 조회
    List<Favorite> findUserFavoriteList(String studentNumber);

    // 본인의 관심 게시글 내역 삭제
    List<Long> deleteMyCheckedFavoritesAndGetRelatedToBoardIds(Long userId);
}
