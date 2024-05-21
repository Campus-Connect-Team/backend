package com.campusconnect.backend.comment.domain;

import com.campusconnect.backend.basetime.BaseTimeEntity;
import com.campusconnect.backend.board.domain.Board;
import com.campusconnect.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;

    @Column(name = "commenter_name")
    private String commenterName;

    @Column(name = "commenter_student_number")
    private String commenterStudentNumber;

    @Column(name = "commenter_department")
    private String commenterDepartment;

    @Column(name = "comment_content", length = 100)
    private String commentContent;

    @Builder
    public Comment(Board board,
                   User user,
                   String commenterName,
                   String commenterStudentNumber,
                   String commenterDepartment,
                   String commentContent) {
        this.board = board;
        this.user = user;
        this.commenterName = commenterName;
        this.commenterStudentNumber = user.getStudentNumber();
        this.commenterDepartment = commenterDepartment;
        this.commentContent = commentContent;
    }

    /** 댓글 수정 */
    public void updateComment(String commentUpdateContent) {
        this.commentContent = commentUpdateContent;
    }
}
