package com.campusconnect.backend.authentication.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.campusconnect.backend.authentication.domain.QAuthentication.authentication;

@RequiredArgsConstructor
public class AuthenticationRepositoryImpl implements AuthenticationRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Boolean isCorrectAuthenticationNumber(String email, String authenticationNumber) {
        return queryFactory
                .select(authentication)
                .from(authentication)
                .where(authentication.email.substring(0, authentication.email.indexOf("@")).eq(email),
                        authentication.authenticationNumber.eq(authenticationNumber),
                        authentication.createdDate.eq(
                                JPAExpressions
                                        .select(authentication.createdDate.max())
                                        .from(authentication)
                                        .where(authentication.email.substring(0, authentication.email.indexOf("@")).eq(email))
                        )
                )
                .fetchFirst() != null;
    }
}
