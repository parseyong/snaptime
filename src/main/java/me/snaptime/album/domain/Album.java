package me.snaptime.album.domain;

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

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Album extends BaseTimeEntity {

    @Id
    @Column(name = "album_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long albumId;

    @Column(nullable = false,name = "album_name")
    private String albumName;

    @OneToMany(mappedBy = "album", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Snap> snaps;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    protected Album(String albumName, User user) {
        this.albumName = albumName;
        this.user = user;
    }

    public void updateAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void resetSnapsForDelete(){
        // null로 설정 시 JPA가 상태추적을 하지 않기때문에 clear()로 해줘야합니다.
        this.snaps.clear();
    }
}
