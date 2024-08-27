package me.snaptime.snap.service;

import me.snaptime.snap.dto.req.SnapAddReqDto;
import me.snaptime.snap.dto.req.SnapUpdateReqDto;
import me.snaptime.snap.dto.res.SnapFindAllInAlbumResDto;
import me.snaptime.snap.dto.res.SnapFindDetailResDto;

import java.util.List;

public interface SnapService {

    /*
        스냅을 추가합니다.

        reqLoginId    : 스냅을 저장하는 유저의 loginId
        snapAddReqDto : 스냅에 대한 정보가 담긴 dto
    */
    void addSnap(String reqLoginId, SnapAddReqDto snapAddReqDto);

    SnapFindDetailResDto findSnap(Long id, String uId);
    Long modifySnap(Long snapId, SnapUpdateReqDto snapUpdateReqDto, String userUid, List<String> tagUserLoginIds, boolean isPrivate);
    void changeVisibility(Long snapId, String userUid, boolean isPrivate);
    void deleteSnap(Long id, String Uid);
    byte[] downloadPhotoFromFileSystem(String fileName, String uId, boolean isEncrypted);
    void relocateSnap(Long snapId, Long albumId, String uId);
    SnapFindAllInAlbumResDto findAllSnapInAlbum(String uId, Long album_id);
}
