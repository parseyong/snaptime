package me.snaptime.friend.service.impl;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.service.AlarmAddService;
import me.snaptime.component.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.friend.domain.Friend;
import me.snaptime.friend.dto.res.FriendCntResDto;
import me.snaptime.friend.dto.res.FriendFindPagingResDto;
import me.snaptime.friend.dto.res.FriendInfoResDto;
import me.snaptime.friend.enums.FriendSearchType;
import me.snaptime.friend.repository.FriendRepository;
import me.snaptime.friend.service.FriendService;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.util.NextPageChecker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.snaptime.user.domain.QUser.user;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final UrlComponent urlComponent;
    private final AlarmAddService alarmAddService;

    @Override
    @Transactional
    public void sendFollow(String senderEmail, String receiverEmail){

        User sender = findUserByEmail(senderEmail);
        User receiver = findUserByEmail(receiverEmail);

        Optional<Friend> friendOptional = friendRepository.findBySenderAndReceiver(sender,receiver);
        
        // 만약 이미 팔로우요청을 보낸 유저일 경우
        if(friendOptional.isPresent())
            throw new CustomException(ExceptionCode.ALREADY_FOLLOW);

        // 자기자신에게 팔로우요청을 했다면
        if (receiver.getUserId() == sender.getUserId())
            throw new CustomException(ExceptionCode.CAN_NOT_SELF_FOLLOW);

        friendRepository.save(Friend.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .build());

        alarmAddService.addFollowAlarm(sender,receiver);
    }

    @Override
    @Transactional
    public void unFollow(String reqEmail, String deletedUserEmail){

        User reqUser = findUserByEmail(reqEmail);
        User deletedUser = findUserByEmail(deletedUserEmail);

        Friend friend = friendRepository.findBySenderAndReceiver(reqUser,deletedUser)
                .orElseThrow(() -> new CustomException(ExceptionCode.FRIEND_NOT_EXIST));

        friendRepository.delete(friend);
    }

    @Override
    public FriendFindPagingResDto findFriendPageByUser(String reqEmail, String targetUserEmail, Long pageNum,
                                                       FriendSearchType searchType, String searchKeyword){

        User reqUser = findUserByEmail(reqEmail);
        User targetUser = findUserByEmail(targetUserEmail);

        List<Tuple> tuples = friendRepository.findFriendPage(targetUser,searchType,pageNum,searchKeyword);

        // 다음 페이지 유무 체크
        boolean hasNextPage = NextPageChecker.hasNextPage(tuples,20L);

        List<FriendInfoResDto> friendInfoResDtos = tuples.stream().map(tuple -> {

            boolean isMyFriend = isFollow(reqUser , findUserByEmail(tuple.get(user.email)));
            String profilePhotoURL = urlComponent.makePhotoURL(tuple.get(user.profilePhotoName),false);
            return FriendInfoResDto.toDto(tuple,profilePhotoURL,isMyFriend);
        }).collect(Collectors.toList());

        return FriendFindPagingResDto.toDto(friendInfoResDtos, hasNextPage);
    }

    @Override
    public FriendCntResDto findFriendCnt(User targetUser){

        // target의 팔로잉,팔로워 수 조회
        Long followingCnt = friendRepository.countBySender(targetUser);
        Long followerCnt = friendRepository.countByReceiver(targetUser);

        return FriendCntResDto.toDto(followerCnt,followingCnt);
    }

    @Override
    public boolean isFollow(User reqUser, User targetUser){
        return friendRepository.existsBySenderAndReceiver(reqUser, targetUser);
    }

    private User findUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
    }

}