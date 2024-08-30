package me.snaptime.photo.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.component.CipherComponent;
import me.snaptime.component.PhotoComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.photo.service.PhotoService;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoServiceImpl implements PhotoService {

    private final UserRepository userRepository;
    private final PhotoComponent photoComponent;
    private final CipherComponent cipherComponent;

    @Override
    public byte[] findPhoto(String reqLoginId, String fileName, boolean isEncrypted) {
        
        // 암호화된 사진인 경우 복호화를 거친 후 반환
        if (isEncrypted) {
            User reqUser = userRepository.findByLoginId(reqLoginId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
            return cipherComponent.decryptData(reqUser.getSecretKey(), photoComponent.findPhoto(fileName));
        }

        return photoComponent.findPhoto(fileName);
    }
}
