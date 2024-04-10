package com.campusconnect.backend.user.repository;

import com.campusconnect.backend.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.campusconnect.backend.user.domain.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Boolean isEmailPrefixDuplicated(String email) {
        return queryFactory
                .select(user)
                .from(user)
                .where(user.email.substring(0, user.email.indexOf("@")).eq(email))
                .fetchFirst() != null;
    }
}
