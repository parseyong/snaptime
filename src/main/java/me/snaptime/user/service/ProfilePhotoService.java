package me.snaptime.user.service;

import org.springframework.web.multipart.MultipartFile;

public interface ProfilePhotoService {
    byte[] downloadPhotoFromFileSystem(String reqLoginId);

    void updatePhotoFromFileSystem(String reqLoginId, MultipartFile updateFile) throws Exception;

}
