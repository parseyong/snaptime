package me.snaptime.snap.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.album.domain.Album;
import me.snaptime.common.BaseTimeEntity;
import me.snaptime.user.domain.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Snap extends BaseTimeEntity {

    @Id
    @Column(name = "snap_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long snapId;

    private String oneLineJournal;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Builder
    protected Snap(String oneLineJournal, Album album, User user,
                   String fileName, String filePath, String fileType, boolean isPrivate) {
        this.oneLineJournal = oneLineJournal;
        this.album = album;
        this.user = user;
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.isPrivate = isPrivate;
    }

    public void updateIsPrivate(boolean state) {
        this.isPrivate = state;
    }

    public void associateAlbum(Album album) {
        this.album = album;
    }

    public void updateOneLineJournal(String oneLineJournal) {
        this.oneLineJournal = oneLineJournal;
    }


}
