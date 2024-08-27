package me.snaptime.snap.controller;

import me.snaptime.photo.controller.PhotoController;
import me.snaptime.photo.service.impl.PhotoServiceImpl;
import me.snaptime.user.dto.req.UserReqDto;
import me.snaptime.user.service.UserSignService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = PhotoController.class)
public class PhotoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhotoServiceImpl photoService;

    @MockBean
    private UserSignService userSignService;


    @DisplayName("Photo 조회 테스트")
    @WithMockUser(username = "mockUid",password = "test1234",roles = "USER")
    @Test
    public void findPhotoTest() throws Exception {
        // given
        userSignService.signUp(new UserReqDto(
                "김원정", "mockUid", "test1234", "test@test.com", "990303"
        ));
        byte[] emptyByte = {};

        given(photoService.findPhoto("image.png", "mockUid", true)).willReturn(emptyByte);
        // when
        mockMvc.perform(
                get("/photo?fileName=image.png&isEncrypted=true"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("Photo 조회 실패 테스트 (권한이 없음)")
    @Test
    public void findPhotoFailTest() throws Exception {
        // when
        mockMvc.perform(
                        get("/photo?fileName=image.png&isEncrypted=true"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

}
