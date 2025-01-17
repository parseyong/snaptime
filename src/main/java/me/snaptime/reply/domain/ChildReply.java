package me.snaptime.reply.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.BaseTimeEntity;
import me.snaptime.user.domain.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChildReply extends BaseTimeEntity {

    @Id
    @Column(name = "child_reply_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long childReplyId;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "writer_id",nullable = false)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "parent_reply_id",nullable = false)
    private ParentReply parentReply;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "reply_tag_user_id", nullable = true)
    private User replyTagUser;

    @Builder
    protected ChildReply(String content, User writer, User tagUser, ParentReply parentReply){
        this.content=content;
        this.writer=writer;
        this.replyTagUser=tagUser;
        this.parentReply=parentReply;
    }

    public void updateReply(String content){
        this.content=content;
    }
}
