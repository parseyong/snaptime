package me.snaptime.profile.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.component.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.friend.service.FriendService;
import me.snaptime.profile.dto.res.UserProfileResDto;
import me.snaptime.profile.service.ProfileService;
import me.snaptime.snap.dto.res.SnapFindResDto;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final UrlComponent urlComponent;
    private final SnapRepository snapRepository;
    private final FriendService friendService;

    @Override
    public UserProfileResDto findUserProfile(String reqEmail, String targetUserEmail) {

        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        User targetUser = userRepository.findByEmail(targetUserEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));


        String profilePhotoURL = urlComponent.makePhotoURL(targetUser.getProfilePhotoName(), false);
        Long snapCnt = snapRepository.countByWriter(targetUser);

        return UserProfileResDto.builder()
                .userId(targetUser.getUserId())
                .email(targetUser.getEmail())
                .nickName(targetUser.getNickname())
                .profilePhotoURL(profilePhotoURL)
                .friendCntResDto( friendService.findFriendCnt(targetUser) )
                .snapCnt(snapCnt)
                .isFollow(friendService.isFollow(reqUser, targetUser))
                .build();
    }

    @Override
    public List<SnapFindResDto> findTagSnap(String targetUserEmail) {

        User targetUser = userRepository.findByEmail(targetUserEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return snapRepository.findTagedSnaps(targetUser).stream().map(snap -> {
            String snapPhotoURL = urlComponent.makePhotoURL(snap.getFileName(),snap.isPrivate());
            return SnapFindResDto.toDto(snap, snapPhotoURL);
        }).collect(Collectors.toList());
    }
}
