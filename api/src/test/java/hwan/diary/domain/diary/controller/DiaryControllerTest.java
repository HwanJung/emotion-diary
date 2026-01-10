package hwan.diary.domain.diary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hwan.diary.common.exception.ErrorCode;
import hwan.diary.common.exception.diary.DiaryNotFoundException;
import hwan.diary.domain.diary.dto.DiaryDto;
import hwan.diary.domain.diary.dto.command.CreateDiaryCommand;
import hwan.diary.domain.diary.dto.command.UpdateDiaryCommand;
import hwan.diary.domain.diary.dto.request.CreateDiaryRequest;
import hwan.diary.domain.diary.dto.request.UpdateDiaryRequest;
import hwan.diary.domain.diary.service.DiaryService;
import hwan.diary.security.jwt.principal.JwtAuthenticationToken;
import hwan.diary.security.jwt.principal.JwtUserPrincipal;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.security.Principal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(DiaryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(DiaryControllerTest.TestMockConfig.class)
public class DiaryControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestMockConfig {
        @Bean
        public DiaryService diaryService() {
            return mock(DiaryService.class);
        }
    }

    private static final Long USER_ID = 1L;
    private static final Long DIARY_ID = 2L;
    private static final String TITLE = "titleEx";
    private static final String CONTENT = "contentEx";
    private static final String IMAGE_KEY = "imageKeyEx";
    private static final LocalDate DIARY_DATE = LocalDate.of(2025, 9, 1);

    private static Principal principal = new JwtAuthenticationToken(new JwtUserPrincipal(USER_ID));

    @Test
    void getDiary_returns200_andBody() throws Exception {
        // given
        DiaryDto diaryDto = new DiaryDto(DIARY_ID, TITLE, CONTENT, IMAGE_KEY, DIARY_DATE);

        given(diaryService.findDiary(DIARY_ID, USER_ID)).willReturn(diaryDto);

        // when & then
        mvc.perform(get("/api/diaries/{id}", DIARY_ID)
                .principal(principal))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(DIARY_ID))
            .andExpect(jsonPath("$.title").value(TITLE))
            .andExpect(jsonPath("$.content").value(CONTENT))
            .andExpect(jsonPath("$.imageKey").value(IMAGE_KEY))
            .andExpect(jsonPath("$.diaryDate").value(DIARY_DATE.toString()));

        verify(diaryService).findDiary(DIARY_ID, USER_ID);
    }

    @Test
    void createDiary_return201_andBody() throws Exception {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(TITLE, CONTENT, IMAGE_KEY, DIARY_DATE);
        DiaryDto createdDiaryDto = new DiaryDto(DIARY_ID, TITLE, CONTENT, IMAGE_KEY, DIARY_DATE);

        given(diaryService.createDiary(any(CreateDiaryCommand.class), eq(USER_ID)))
            .willReturn(createdDiaryDto);

        //when & then
        mvc.perform(post("/api/diaries")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost/api/diaries/" + DIARY_ID))
            .andExpect(jsonPath("$.id").value(DIARY_ID))
            .andExpect(jsonPath("$.title").value(TITLE))
            .andExpect(jsonPath("$.content").value(CONTENT))
            .andExpect(jsonPath("$.imageKey").value(IMAGE_KEY))
            .andExpect(jsonPath("$.diaryDate").value(DIARY_DATE.toString()));

        ArgumentCaptor<CreateDiaryCommand> captor = ArgumentCaptor.forClass(CreateDiaryCommand.class);
        verify(diaryService).createDiary(captor.capture(), eq(USER_ID));
        CreateDiaryCommand cmd = captor.getValue();
        assertEquals(TITLE, cmd.title());
        assertEquals(CONTENT, cmd.content());
        assertEquals(IMAGE_KEY, cmd.imageKey());
        assertEquals(DIARY_DATE, cmd.diaryDate());
    }

    @Test
    void updateDiary_return200_andBody() throws Exception {
        // given
        UpdateDiaryRequest request = new UpdateDiaryRequest(TITLE, CONTENT, IMAGE_KEY, DIARY_DATE, true);
        DiaryDto updated = new DiaryDto(DIARY_ID, TITLE, CONTENT, IMAGE_KEY, DIARY_DATE);

        given(diaryService.updateDiary(eq(USER_ID), eq(DIARY_ID), any(UpdateDiaryCommand.class)))
            .willReturn(updated);

        // when & then
        mvc.perform(put("/api/diaries/{id}", DIARY_ID)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(DIARY_ID))
            .andExpect(jsonPath("$.title").value(TITLE))
            .andExpect(jsonPath("$.content").value(CONTENT))
            .andExpect(jsonPath("$.imageKey").value(IMAGE_KEY))
            .andExpect(jsonPath("$.diaryDate").value(DIARY_DATE.toString()));

        ArgumentCaptor<UpdateDiaryCommand> captor = ArgumentCaptor.forClass(UpdateDiaryCommand.class);
        verify(diaryService).updateDiary(eq(USER_ID), eq(DIARY_ID), captor.capture());
        UpdateDiaryCommand cmd = captor.getValue();
        assertEquals(TITLE, cmd.title());
        assertEquals(CONTENT, cmd.content());
        assertEquals(DIARY_DATE, cmd.diaryDate());
        assertEquals(IMAGE_KEY, cmd.newImageKey());
        assertTrue(cmd.clearImage());
    }

    @Test
    void deleteDiary_return204_andBody() throws Exception {
        // given
        willDoNothing().given(diaryService).deleteDiary(USER_ID, DIARY_ID);

        // when & then
        mvc.perform(delete("/api/diaries/{id}", DIARY_ID)
                .principal(principal))
            .andExpect(status().isNoContent());

        verify(diaryService).deleteDiary(USER_ID, DIARY_ID);
    }

    @Test
    void deleteDiary_returnErrorResponse() throws Exception {
        // given
        doThrow(new DiaryNotFoundException(ErrorCode.DIARY_NOT_FOUND, DIARY_ID, USER_ID))
            .when(diaryService).deleteDiary(USER_ID, DIARY_ID);

        // when & then
        mvc.perform(delete("/api/diaries/{id}", DIARY_ID)
                .principal(principal))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(ErrorCode.DIARY_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(ErrorCode.DIARY_NOT_FOUND.getMessage()));
    }
}
