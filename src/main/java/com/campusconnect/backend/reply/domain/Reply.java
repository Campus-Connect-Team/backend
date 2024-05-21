package com.campusconnect.backend.reply.domain;

import com.campusconnect.backend.basetime.BaseTimeEntity;
import com.campusconnect.backend.comment.domain.Comment;
import com.campusconnect.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reply")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Reply extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;

    @Column(name = "replier_name")
    private String replierName;

    @Column(name = "replier_student_Number")
    private String replierStudentNumber;

    @Column(name = "replier_department")
    private String replierDepartment;

    @Column(name = "reply_content", length = 100)
    private String replyContent;

    @Builder
    public Reply(Comment comment,
                 User user,
                 String replierName,
                 String replierStudentNumber,
                 String replierDepartment,
                 String replyContent) {
        this.comment = comment;
        this.user = user;
        this.replierName = replierName;
        this.replierStudentNumber = replierStudentNumber;
        this.replierDepartment = replierDepartment;
        this.replyContent = replyContent;
    }

    /** 답글 수정 */
    public void updateReply(String replyUpdateContent) {
        this.replyContent = replyUpdateContent;
    }
}
