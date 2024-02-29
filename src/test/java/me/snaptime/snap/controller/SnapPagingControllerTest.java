package me.snaptime.snap.controller;

import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.controller.SnapPagingController;
import me.snaptime.snap.service.impl.SnapPagingServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = SnapPagingController.class)
public class SnapPagingControllerTest {

    @MockBean
    private SnapPagingServiceImpl snapPagingService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("스냅 페이징조회테스트 -> 성공")
    public void findSnapPagingTest1() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(get("/snaps/community/{pageNum}",1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("스냅 페이징조회가 완료되었습니다."))
                .andDo(print());

        verify(snapPagingService,times(1)).findSnapPaging(any(String.class),any(Long.class));
    }

    @Test
    @DisplayName("스냅 페이징조회테스트 -> (실패 : PathVariable 타입예외)")
    public void findSnapPagingTest2() throws Exception{
        //given

        //when, then
        this.mockMvc.perform(get("/snaps/community/{pageNum}","test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("pageNum이 Long타입이여야 합니다."))
                .andDo(print());

        verify(snapPagingService,times(0)).findSnapPaging(any(String.class),any(Long.class));
    }

    @Test
    @DisplayName("스냅 페이징조회테스트 -> (실패 : 존재하지 않는 페이지)")
    public void findSnapPagingTest3() throws Exception{
        //given
        given(snapPagingService.findSnapPaging(any(String.class),any(Long.class)))
                .willThrow(new CustomException(ExceptionCode.PAGE_NOT_FOUND));

        //when, then
        this.mockMvc.perform(get("/snaps/community/{pageNum}",1l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("존재하지 않는 페이지입니다."))
                .andDo(print());

        verify(snapPagingService,times(1)).findSnapPaging(any(String.class),any(Long.class));
    }
}
