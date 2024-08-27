package me.snaptime.snap.service;

import me.snaptime.snap.dto.req.SnapAddReqDto;
import me.snaptime.snap.dto.req.SnapUpdateReqDto;
import me.snaptime.snap.dto.res.SnapFindAllInAlbumResDto;
import me.snaptime.snap.dto.res.SnapFindDetailResDto;

public interface SnapService {

    /*
        스냅을 추가합니다.

        reqLoginId    : 스냅을 저장하는 유저의 loginId
        snapAddReqDto : 스냅에 대한 정보가 담긴 dto
    */
    void addSnap(String reqLoginId, SnapAddReqDto snapAddReqDto);

    /*
        1개 Snap의 상세정보(태그정보,좋아요 수 등등..)를 조회합니다.
        상세정보가 담긴 SnapFindDetailResDto를 반환합니다.

        reqLoginId : 요청자의 loginId
        snapId     : 조회할 snap의 id
    */
    SnapFindDetailResDto findSnapDetail(String reqLoginId, Long snapId);

    /*
        스냅정보를 변경합니다. 자신의 스냅이 아니라면 예외를 반환합니다.
        스냅에 태그된 유저정보, 스냅 한줄일기, 사진을 변경합니다.

        reqLoginId       : 요청자의 loginId
        snapId           : 수정할 스냅의 id
        snapUpdateReqDto : 변경정보가 담긴 dto
    */
    void updateSnap(String reqLoginId, Long snapId, SnapUpdateReqDto snapUpdateReqDto);

    /*
        스냅의 공개여부를 변경합니다. 현재상태값을 다시 보내면 예외를 반환합니다.
        ex. 비공개스냅인데 비공개로 전환할 시

        비공개스냅으로 전환 시 암호화하여 저장합니다.
        공개스냅으로 전환 시 복호화하여 저장합니다.


       reqLoginId : 요청자의 loginId
       snapId     : 변경할 스냅의 id
       isPrivate  : 변경할 스냅의 상태
    */
    void updateSnapVisibility(String reqLoginId, Long snapId, boolean isPrivate);

    /*
        스냅의 앨범위치를 옮깁니다.

        reqLoginId : 요청자의 loginId
        snapId     : 스냅 id
        albumId    : 옮길 앨범의 id
    */
    void updateSnapPosition(String reqLoginId, Long snapId, Long albumId);

    /*
        스냅을 삭제합니다. 자신의 스냅이 아닌경우 예외를 반환합니다.

        reqLoginId : 요청자의 loginId
        snapId     : 삭제할 스냅의 id
    */
    void deleteSnap(String reqLoginId, Long snapId);

    /*
        앨범에 저장된 모든 스냅을 가져옵니다. (공개 비공개 모두)
        이 메소드는 자신의 앨범에 저장된 스냅을 보는 요청입니다.
        다른사람의 앨범을 보려할 시 예외를 반환합니다.

        reqLoginId : 요청자의 loginId
        albumId    : 조회하려는 앨범 id
    */
    SnapFindAllInAlbumResDto findAllSnapInAlbum(String reqLoginId, Long albumId);

}
