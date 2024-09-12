package me.snaptime.reply.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.BaseTimeEntity;
import me.snaptime.snap.domain.Snap;
import me.snaptime.user.domain.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParentReply extends BaseTimeEntity {

    @Id
    @Column(name = "parent_reply_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parentReplyId;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "snap_id",nullable = false)
    private Snap snap;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "writer_id",nullable = false)
    private User writer;

    @Builder
    protected ParentReply(String content, Snap snap, User writer){
        this.content=content;
        this.snap=snap;
        this.writer=writer;
    }

    public void updateReply(String content){
        this.content=content;
    }

}
