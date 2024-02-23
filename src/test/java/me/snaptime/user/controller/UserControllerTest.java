package me.snaptime.user.controller;

import com.google.gson.Gson;
import me.snaptime.user.data.controller.UserController;
import me.snaptime.user.data.dto.request.UserRequestDto;
import me.snaptime.user.data.dto.request.UserUpdateDto;
import me.snaptime.user.data.dto.response.UserResponseDto;
import me.snaptime.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;


    @Test
    @DisplayName("유저 정보 조회 컨트롤러 테스트")
    void getUserTest() throws Exception{

        //given
        given(userService.getUser(1L)).willReturn(
                UserResponseDto.builder()
                        .id(1L)
                        .loginId("kang4746")
                        .password("test1234")
                        .email("strong@gmail.com")
                        .birthDay("1999-10-29")
                        .build());

        //when
        Long userId = 1L;
        mockMvc.perform(get("/users/{userId}",1L))
                        .andExpect(status().isOk())
                        //json response 형식을 잘 봅시다.
                        .andExpect(jsonPath("$.msg").exists())
                        .andExpect(jsonPath("$.result.id").exists())
                        .andExpect(jsonPath("$.result.loginId").exists())
                        .andExpect(jsonPath("$.result.password").exists())
                        .andExpect(jsonPath("$.result.email").exists())
                        .andExpect(jsonPath("$.result.birthDay").exists())
                        .andDo(print());

        //then
        verify(userService).getUser(1L);
    }

    @Test
    @DisplayName("유저 회원가입 컨트롤러 테스트")
    void signUpTest() throws Exception{

        //given
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .loginId("kang4746")
                .password("test1234")
                .name("홍길순")
                .email("strong@gmail.com")
                .birthDay("1999-10-29")
                .build();

        given(userService.signUp(any(UserRequestDto.class)))
                .willReturn(UserResponseDto.builder()
                        .id(1L)
                        .loginId("kang4746")
                        .password("test1234")
                        .name("홍길순")
                        .email("strong@gmail.com")
                        .birthDay("1999-10-29")
                        .build());

        Gson gson = new Gson();
        String content = gson.toJson(userRequestDto);

        //when
        mockMvc.perform(post("/users/sign-up").content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.loginId").exists())
                .andExpect(jsonPath("$.result.password").exists())
                .andExpect(jsonPath("$.result.email").exists())
                .andExpect(jsonPath("$.result.birthDay").exists())
                .andDo(print());

        //then
        verify(userService).signUp(any(UserRequestDto.class));
    }

    @Test
    @DisplayName("유저 수정 테스트")
    void updateUserTest() throws Exception{

        //given
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .loginId("jun4746")
                .name("")
                .email("strong@naver.com")
                .birthDay("")
                .build();

        given(userService.updateUser(eq(1L),any(UserUpdateDto.class)))
                .willReturn(UserResponseDto.builder()
                        .id(1L)
                        .loginId("kang4746")
                        .password("test1234")
                        .name("홍길순")
                        .email("strong@gmail.com")
                        .birthDay("1999-10-29")
                        .build());

        Gson gson = new Gson();
        String content = gson.toJson(userUpdateDto);

        //when
        Long userId = 1L;
        mockMvc.perform(put("/users/{userId}",userId)
                .content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.loginId").exists())
                .andExpect(jsonPath("$.result.password").exists())
                .andExpect(jsonPath("$.result.email").exists())
                .andExpect(jsonPath("$.result.birthDay").exists())
                .andDo(print());

        //then
        verify(userService).updateUser(eq(1L),any(UserUpdateDto.class));
    }

    @Test
    @DisplayName("유저 삭제 테스트")
    void deleteUserTest() throws Exception{
        //given
        //when
        Long userId = 1L;
        mockMvc.perform(delete("/users/{userId}",userId))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService).deleteUser(1L);
    }

}
