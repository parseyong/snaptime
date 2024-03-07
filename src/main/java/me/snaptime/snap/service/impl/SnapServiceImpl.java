package me.snaptime.snap.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.domain.Encryption;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.dto.file.WritePhotoToFileSystemResult;
import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.data.dto.req.ModifySnapReqDto;
import me.snaptime.snap.data.dto.res.FindSnapResDto;
import me.snaptime.snap.data.repository.AlbumRepository;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.snap.component.EncryptionComponent;
import me.snaptime.snap.service.PhotoService;
import me.snaptime.snap.service.SnapService;
import me.snaptime.snap.util.EncryptionUtil;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnapServiceImpl implements SnapService {
    private final SnapRepository snapRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final PhotoService photoService;
    private final EncryptionComponent encryptionComponent;

    @Override
    public void createSnap(CreateSnapReqDto createSnapReqDto, String userUid, boolean isPrivate) {
        User foundUser = userRepository.findByLoginId(userUid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        WritePhotoToFileSystemResult writePhotoToFileSystemResult = savePhotoToFileSystem(foundUser, createSnapReqDto.multipartFile(), isPrivate);
        snapRepository.save(
                Snap.builder()
                        .oneLineJournal(createSnapReqDto.oneLineJournal())
                        .fileName(writePhotoToFileSystemResult.fileName())
                        .filePath(writePhotoToFileSystemResult.filePath())
                        .fileType(createSnapReqDto.multipartFile().getContentType())
                        .user(foundUser)
                        .album(albumRepository.findByName(createSnapReqDto.album()))
                        .isPrivate(isPrivate)
                        .build()
        );
    }

    @Override
    public FindSnapResDto findSnap(Long id) {
        Snap foundSnap = snapRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        return FindSnapResDto.entityToResDto(foundSnap);
    }

    @Override
    public void modifySnap(ModifySnapReqDto modifySnapReqDto, String userUid, boolean isPrivate) {
    }

    @Override
    public void changeVisibility(Long snapId, String userUid, boolean isPrivate) {
        Snap foundSnap = snapRepository.findById(snapId).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        User foundUser = userRepository.findByLoginId(userUid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        if (foundSnap.isPrivate() == isPrivate) {
            throw new CustomException(ExceptionCode.CHANGE_SNAP_VISIBILITY_ERROR);
        }

        byte[] foundPhotoByte = photoService.getPhotoByte(foundSnap.getFilePath());
        if(isPrivate) {
            // public -> private (암호화)
            Encryption encryption = encryptionComponent.setEncryption(foundUser);
            byte[] encryptedByte = encryptionComponent.encryptData(encryption, foundPhotoByte);
            photoService.updateFileSystemPhoto(foundSnap.getFilePath(), encryptedByte);
        } else {
            // private -> public (복호화)
            Encryption encryption = encryptionComponent.getEncryption(foundUser);
            byte[] decryptedByte = encryptionComponent.decryptData(encryption, foundPhotoByte);
            encryptionComponent.deleteEncryption(encryption);
            photoService.updateFileSystemPhoto(foundSnap.getFilePath(), decryptedByte);
        }
        foundSnap.updateIsPrivate(isPrivate);

        snapRepository.save(foundSnap);

    }

    @Override
    public void deleteSnap(Long id, String Uid) {

    }

    @Override
    public byte[] downloadPhotoFromFileSystem(String fileName, String uId, boolean isEncrypted) {
        byte[] photoData = photoService.downloadPhotoFromFileSystem(fileName);
        if (isEncrypted) {
            try {
                SecretKey secretKey = encryptionComponent.getSecretKey(uId);
                return EncryptionUtil.decryptData(photoData, secretKey);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new CustomException(ExceptionCode.ENCRYPTION_ERROR);
            }
        }
        return photoData;
    }

    private WritePhotoToFileSystemResult savePhotoToFileSystem(User user, MultipartFile multipartFile, boolean isPrivate) {
        try {
            if (isPrivate) {
                Encryption encryption = encryptionComponent.setEncryption(user);
                byte[] encryptedData = encryptionComponent.encryptData(encryption, multipartFile.getInputStream().readAllBytes());
                return photoService.writePhotoToFileSystem(multipartFile.getOriginalFilename(), multipartFile.getContentType(), encryptedData);
            } else {
                return photoService.writePhotoToFileSystem(multipartFile.getOriginalFilename(), multipartFile.getContentType(), multipartFile.getInputStream().readAllBytes());
        }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_READ_ERROR);
        }
    }
}
