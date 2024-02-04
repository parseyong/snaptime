package me.snaptime.Social.data.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.Social.common.FriendStatus;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply {

    @Id
    @Column(name = "reply_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyId;

    @Column(nullable = false)
    private String content;

//    @ManyToOne
//    @JoinColumn(name = "snap_id",nullable = false)
//    private Snap snap;

//    @ManyToOne
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JoinColumn(name = "user_id",nullable = false)
//    private User user;

    @Builder
    public Reply(String content/*, Snap snap, User user*/){
        this.content=content;
        //this.snap=snap;
        //this.user=user;
    }

    public void updateReply(String content){
        this.content=content;
    }

}