package com.campusconnect.backend.reply.repository;

import com.campusconnect.backend.reply.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long>, ReplyRepositoryCustom {

}
