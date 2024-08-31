package me.snaptime.user.service.impl;


import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import me.snaptime.component.PhotoComponent;
import me.snaptime.component.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.user.domain.User;
import me.snaptime.user.dto.req.UserDeleteReqDto;
import me.snaptime.user.dto.req.UserUpdatePasswordReqDto;
import me.snaptime.user.dto.req.UserUpdateReqDto;
import me.snaptime.user.dto.res.UserFindMyPageResDto;
import me.snaptime.user.dto.res.UserFindPagingResDto;
import me.snaptime.user.dto.res.UserFindResDto;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.user.service.UserService;
import me.snaptime.util.NextPageChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static me.snaptime.user.domain.QUser.user;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UrlComponent urlComponent;
    private final PhotoComponent photoComponent;

    @Override
    public UserFindMyPageResDto findUserMyPage(String reqLoginId) {

        User reqUser = userRepository.findByLoginId(reqLoginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        String profilePhotoURL = urlComponent.makePhotoURL(reqUser.getProfilePhotoName(), false);
        return UserFindMyPageResDto.toDto(reqUser, profilePhotoURL);
    }

    @Override
    public UserFindPagingResDto searchUserPaging(String searchKeyword, Long pageNum){

        List<Tuple> tuples = userRepository.searchUserPaging(searchKeyword,pageNum);

        // 다음 페이지 유무 체크
        boolean hasNextPage = NextPageChecker.hasNextPage(tuples,20L);

        List<UserFindResDto> userFindResDtos = tuples.stream().map(tuple -> {
            String profilePhotoURL = urlComponent.makePhotoURL(tuple.get(user.profilePhotoName),false);
            return UserFindResDto.toDto(tuple, profilePhotoURL);
        }).collect(Collectors.toList());

        return UserFindPagingResDto.toDto(userFindResDtos, hasNextPage);
    }

    @Override
    @Transactional
    public void updateUser(String reqLoginId, UserUpdateReqDto userUpdateReqDto) {

        User reqUser = userRepository.findByLoginId(reqLoginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        reqUser.updateNickName(userUpdateReqDto.nickName());
        reqUser.updateEmail(userUpdateReqDto.email());
        reqUser.updateBirthDay(userUpdateReqDto.birthDay());

        userRepository.save(reqUser);
    }

    @Override
    @Transactional
    public void updatePassword(String reqLoginId, UserUpdatePasswordReqDto userUpdatePasswordReqDto){
        User reqUser = userRepository.findByLoginId(reqLoginId)
                .orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_EXIST));

        if (passwordEncoder.matches(userUpdatePasswordReqDto.newPassword(), reqUser.getPassword())) {
            throw new CustomException(ExceptionCode.CAN_NOT_UPDATE_SAME_PASSWORD);
        }

        reqUser.updatePassword(passwordEncoder.encode(userUpdatePasswordReqDto.newPassword()));
        userRepository.save(reqUser);
    }

    @Override
    @Transactional
    public void updateProfilePhoto(String reqLoginId, MultipartFile multipartFile){
        User reqUser = userRepository.findByLoginId(reqLoginId)
                .orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_EXIST));

        try {

            String profilePhotoName = photoComponent.addPhoto
                    (multipartFile.getOriginalFilename(), multipartFile.getInputStream().readAllBytes());

            reqUser.updateProfilePhotoName(profilePhotoName);
        } catch (CustomException customException) {
            throw customException;
        } catch (IOException ioException){
            throw new CustomException(ExceptionCode.PHOTO_FIND_FAIL);
        }

        userRepository.save(reqUser);
    }

    @Override
    @Transactional
    public void deleteUser(String reqLoginId, UserDeleteReqDto userDeleteReqDto) {

        User reqUser = userRepository.findByLoginId(reqLoginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        if (!passwordEncoder.matches(userDeleteReqDto.password(), reqUser.getPassword())) {
            throw new CustomException(ExceptionCode.USER_DELETE_FAIL);
        }

        userRepository.delete(reqUser);
    }
}