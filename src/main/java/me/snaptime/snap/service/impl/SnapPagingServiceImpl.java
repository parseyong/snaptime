package me.snaptime.snap.service.impl;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.component.UrlComponent;
import me.snaptime.common.component.impl.NextPageChecker;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.dto.res.FindSnapPagingResDto;
import me.snaptime.snap.data.dto.res.SnapPagingInfo;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.social.service.SnapLikeService;
import me.snaptime.social.service.SnapTagService;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static me.snaptime.snap.data.domain.QSnap.snap;
import static me.snaptime.user.data.domain.QUser.user;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SnapPagingServiceImpl {

    private final UserRepository userRepository;
    private final SnapRepository snapRepository;
    private final UrlComponent urlComponent;
    private final SnapTagService snapTagService;
    private final SnapLikeService snapLikeService;
    private final NextPageChecker nextPageChecker;

    // snap 페이징 조회
    public FindSnapPagingResDto findSnapPaging(String loginId, Long pageNum){
        User reqUser = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        List<Tuple> result = snapRepository.findSnapPaging(loginId,pageNum,reqUser);
        boolean hasNextPage = nextPageChecker.hasNextPage(result,10L);
        if(hasNextPage)
            result.remove(10);

        List<SnapPagingInfo> snapPagingInfoList = result.stream().map(entity ->
        {
            Long snapId = entity.get(snap.id);
            String profilePhotoURL = urlComponent.makeProfileURL(entity.get(user.profilePhoto.id));
            String snapPhotoURL = urlComponent.makePhotoURL(entity.get(snap.fileName),false);

            return SnapPagingInfo.toDto(entity,profilePhotoURL,snapPhotoURL,
                    snapTagService.findTagUserList(snapId),
                    snapLikeService.findSnapLikeCnt(snapId),
                    snapLikeService.isLikedSnap(snapId, loginId));
        }).collect(Collectors.toList());

        return FindSnapPagingResDto.toDto(snapPagingInfoList, hasNextPage);
    }

}
