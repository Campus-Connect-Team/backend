package com.campusconnect.backend.favorite.repository;

import com.campusconnect.backend.favorite.domain.Favorite;
import com.campusconnect.backend.favorite.domain.QFavorite;
import com.campusconnect.backend.user.domain.QUser;
import com.campusconnect.backend.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.campusconnect.backend.favorite.domain.QFavorite.*;
import static com.campusconnect.backend.user.domain.QUser.user;

@RequiredArgsConstructor
public class FavoriteRepositoryImpl implements FavoriteRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Boolean validateToDuplicationFavorite(Long boardId, String studentNumber) {
        return queryFactory
                .select(favorite)
                .from(favorite)
                .where(favorite.user.studentNumber.eq(studentNumber)
                        .and(favorite.board.id.eq(boardId)))
                .fetchFirst() != null;
    }

    @Override
    public void deleteToFavoriteBoard(Long boardId, String studentNumber) {
        Long userId = queryFactory
                .select(user.id)
                .from(user)
                .where(user.studentNumber.eq(studentNumber))
                .fetchOne();

        queryFactory
                .delete(favorite)
                .where(favorite.user.id.eq(userId)
                        .and(favorite.board.id.eq(boardId)))
                .execute();
    }

    // 게시글 삭제 시 해당 게시글과 관련된 관심 내역도 모두 삭제
    @Override
    public void deleteAllFavorites(Long boardId) {
        queryFactory.
                delete(favorite)
                .where(favorite.board.id.eq(boardId))
                .execute();
    }

    // 특정 유저의 관심 상품 리스트 조회
    @Override
    public List<Favorite> findUserFavoriteList(String studentNumber) {
        return queryFactory
                .select(favorite)
                .from(favorite)
                .where(favorite.user.studentNumber.eq(studentNumber))
                .fetch();
    }

    // 본인의 관심 게시글 내역 삭제
    @Override
    public List<Long> deleteMyCheckedFavoritesAndGetRelatedToBoardIds(Long userId) {
        // 삭제되기 전에 삭제될 favorite 튜플의 board ID 가져오기
        List<Long> deletedBoardIds = queryFactory
                .select(favorite.board.id)
                .from(favorite)
                .where(favorite.user.id.eq(userId))
                .fetch();

        // 해당 사용자가 관심으로 등록한 favorite 튜플 삭제
        queryFactory
                .delete(favorite)
                .where(favorite.user.id.eq(userId))
                .execute();

        return deletedBoardIds;
    }
}
