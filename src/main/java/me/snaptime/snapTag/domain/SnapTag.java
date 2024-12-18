package me.snaptime.snapTag.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.snap.domain.Snap;
import me.snaptime.user.domain.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SnapTag {

    @Id
    @Column(name = "snap_tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long snapTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "tag_user_id",nullable = false)
    private User tagUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="snap_id",nullable = false)
    private Snap snap;

    @Builder
    protected SnapTag(User tagUser, Snap snap){
        this.tagUser = tagUser;
        this.snap = snap;
    }
}
