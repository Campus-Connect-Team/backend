package com.campusconnect.backend.board.repository;

import com.campusconnect.backend.board.domain.Board;
import com.campusconnect.backend.board.domain.QBoard;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.campusconnect.backend.board.domain.QBoard.*;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Board> searchBoardWithSearchCond(String department, String title, Pageable pageable) {
        return queryFactory
                .select(board)
                .from(board)
                .where(getBoardByDepartment(department), getBoardByTitle(title))
                .orderBy(board.createdDate.desc())
                .fetch();
    }

    private Predicate getBoardByDepartment(String department) {
        return department != null ? (board.user.department.like("%" + department.replaceAll("\\s+", "") + "%")
                .or(board.user.department.like("%" +  department.replaceAll("\\s+", "") + "%" + department.replaceAll("\\s+", "") + "%"))) : null;
    }

    private Predicate getBoardByTitle(String title) {
        return title != null ? (board.title.like("%" + title.replaceAll("\\s+", "") + "%")) : null;
    }
}
