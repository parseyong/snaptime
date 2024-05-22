package me.snaptime.social.data.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.domain.BaseTimeEntity;
import me.snaptime.user.data.domain.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChildReply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long childReplyId;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "parent_reply_id",nullable = false)
    private ParentReply parentReply;

    @OneToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "reply_tag_user_id", nullable = true)
    private User replyTagUser;

    @Builder
    protected ChildReply(String content, User user, User tagUser, ParentReply parentReply){
        this.content=content;
        this.user=user;
        this.replyTagUser=tagUser;
        this.parentReply=parentReply;
    }

    public void updateReply(String content){
        this.content=content;
    }
}
