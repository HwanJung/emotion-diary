package hwan.diary.domain.diary.service;

import hwan.diary.common.exception.diary.DiaryNotFoundException;
import hwan.diary.domain.diary.dto.DiaryDto;
import hwan.diary.domain.diary.dto.command.CreateDiaryCommand;
import hwan.diary.domain.diary.dto.command.UpdateDiaryCommand;
import hwan.diary.domain.diary.dto.response.SliceResponse;
import hwan.diary.domain.diary.entity.Diary;
import hwan.diary.domain.diary.repository.DiaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiaryServiceTest {

    @Mock
    private DiaryRepository diaryRepository;

    @InjectMocks
    private DiaryService diaryService;

    private static final Long USER_ID = 1L;
    private static final Long DIARY_ID = 2L;
    private static final String TITLE = "titleEx";
    private static final String CONTENT = "contentEx";
    private static final String IMAGE_KEY = "imageKeyEx";
    private static final LocalDate DIARY_DATE = LocalDate.of(2025, 9, 1);
    private static final LocalDate DIARY_DATE2 = LocalDate.of(2025, 9, 2);

    private static final String OLD_TITLE = "old title";
    private static final String OLD_CONTENT = "old content";
    private static final String OLD_IMAGE = "img/old.png";
    private static final LocalDate OLD_DATE = LocalDate.of(2025, 9, 1);

    private Diary existingDiary;

    // for testing updateDiary
    @BeforeEach
    void setUp() {
        existingDiary = Diary.create(USER_ID, OLD_TITLE, OLD_CONTENT, OLD_IMAGE, OLD_DATE);
        ReflectionTestUtils.setField(existingDiary, "id", DIARY_ID);
    }

    @Test
    void createDiary_whenValidInput_thenReturnDto(){
        // given
        CreateDiaryCommand createDiaryCommand = new CreateDiaryCommand(TITLE, CONTENT, IMAGE_KEY, DIARY_DATE);

        given(diaryRepository.save(any(Diary.class))).willAnswer(invocation -> null);

        // when
        DiaryDto diaryDto = diaryService.createDiary(createDiaryCommand, USER_ID);

        // then
        assertEquals(TITLE, diaryDto.title());
        assertEquals(CONTENT, diaryDto.content());
        assertEquals(IMAGE_KEY, diaryDto.imageKey());
        assertEquals(DIARY_DATE, diaryDto.diaryDate());

        verify(diaryRepository, times(1)).save(any(Diary.class));
    }

    @Test
    void findDiary_whenExists_thenReturnDto(){
        // given
        Diary existingDiary = Diary.create(USER_ID, TITLE, CONTENT, IMAGE_KEY, DIARY_DATE);

        given(diaryRepository.findByIdAndUserId(DIARY_ID, USER_ID)).willReturn(Optional.of(existingDiary));

        // when
        DiaryDto diaryDto = diaryService.findDiary(DIARY_ID, USER_ID);

        // then
        assertEquals(TITLE, diaryDto.title());
        assertEquals(CONTENT, diaryDto.content());
        assertEquals(IMAGE_KEY, diaryDto.imageKey());
        assertEquals(DIARY_DATE, diaryDto.diaryDate());
        verify(diaryRepository, times(1)).findByIdAndUserId(DIARY_ID, USER_ID);
    }

    @Test
    void findDiary_whenNotExists_thenThrowsException(){
        // given
        given(diaryRepository.findByIdAndUserId(DIARY_ID, USER_ID)).willReturn(Optional.empty());

        // when & then
        assertThrows(DiaryNotFoundException.class, () -> diaryService.findDiary(DIARY_ID, USER_ID));
        verify(diaryRepository, times(1)).findByIdAndUserId(DIARY_ID, USER_ID);
    }

    @Test
    void findDiaries_whenDataExists_thenMapToDtoAndReturnSliceResponse() {
        // given
        Diary d1 = Diary.create(USER_ID, "t1", "c1", "img1", DIARY_DATE);
        Diary d2 = Diary.create(USER_ID, "t2", "c2", "img2", DIARY_DATE2);

        org.springframework.test.util.ReflectionTestUtils.setField(d1, "id", 11L);
        org.springframework.test.util.ReflectionTestUtils.setField(d2, "id", 12L);

        int requestedPage = 1;
        int requestedSize = 10;

        given(diaryRepository.findSliceByUserId(eq(USER_ID), any(Pageable.class))).willAnswer(inv -> {
            Pageable used = inv.getArgument(1);
            return new SliceImpl<>(List.of(d1, d2), used, false);
        });

        Pageable req = PageRequest.of(requestedPage, requestedSize);

        // when
        SliceResponse<DiaryDto> resp = diaryService.findDiaries(USER_ID, req);

        // then
        assertEquals(requestedPage, resp.page());
        assertEquals(requestedSize, resp.size());
        assertFalse(resp.hasNext());
        assertEquals(2, resp.content().size());

        DiaryDto dto1 = resp.content().get(0);
        DiaryDto dto2 = resp.content().get(1);

        assertEquals(11L, dto1.id());
        assertEquals("t1", dto1.title());
        assertEquals("c1", dto1.content());
        assertEquals("img1", dto1.imageKey());
        assertEquals(DIARY_DATE, dto1.diaryDate());

        assertEquals(12L, dto2.id());
        assertEquals("t2", dto2.title());
        assertEquals("c2", dto2.content());
        assertEquals("img2", dto2.imageKey());
        assertEquals(DIARY_DATE2, dto2.diaryDate());

        verify(diaryRepository, times(1)).findSliceByUserId(eq(USER_ID), any(Pageable.class));
        verifyNoMoreInteractions(diaryRepository);
    }

    @Test
    void updateDiary_whenClearImageTrue_thenImageClearAndUpdateDiary() {
        // given
        given(diaryRepository.findByIdAndUserId(DIARY_ID, USER_ID)).willReturn(Optional.of(existingDiary));

        LocalDate newDate = LocalDate.of(2025, 9, 2);
        UpdateDiaryCommand cmd = new UpdateDiaryCommand("new title", "new content", newDate, null, true);

        // when
        DiaryDto dto = diaryService.updateDiary(USER_ID, DIARY_ID, cmd);

        // then
        assertEquals(DIARY_ID, dto.id());
        assertEquals("new title", dto.title());
        assertEquals("new content", dto.content());
        assertEquals(newDate, dto.diaryDate());
        assertNull(dto.imageKey());                         // 이미지 제거 확인
        assertNull(existingDiary.getImageKey());            // 엔티티 상태도 변경됨

        verify(diaryRepository, times(1)).findByIdAndUserId(DIARY_ID, USER_ID);
        verifyNoMoreInteractions(diaryRepository);
    }

    @Test
    void updateDiary_whenNewImageKeyDifferent_thenChangeImageAndUpdateDiary() {
        // given
        given(diaryRepository.findByIdAndUserId(DIARY_ID, USER_ID)).willReturn(Optional.of(existingDiary));

        LocalDate newDate = LocalDate.of(2025, 9, 3);
        UpdateDiaryCommand cmd = new UpdateDiaryCommand("t2", "c2", newDate, "img/new.png", false);

        // when
        DiaryDto dto = diaryService.updateDiary(USER_ID, DIARY_ID, cmd);

        // then
        assertEquals("img/new.png", dto.imageKey());
        assertEquals("t2", dto.title());
        assertEquals("c2", dto.content());
        assertEquals(newDate, dto.diaryDate());
        assertEquals("img/new.png", existingDiary.getImageKey()); // 엔티티 변경 확인

        verify(diaryRepository).findByIdAndUserId(DIARY_ID, USER_ID);
        verifyNoMoreInteractions(diaryRepository);
    }

    @Test
    void updateDiary_whenNewImageKeyNull_thenUpdateDiary() {
        // given
        given(diaryRepository.findByIdAndUserId(DIARY_ID, USER_ID)).willReturn(Optional.of(existingDiary));

        LocalDate newDate = LocalDate.of(2025, 9, 4);
        UpdateDiaryCommand cmd = new UpdateDiaryCommand("t3", "c3", newDate, null, false);

        // when
        DiaryDto dto = diaryService.updateDiary(USER_ID, DIARY_ID, cmd);

        // then
        assertEquals(OLD_IMAGE, dto.imageKey());            // 유지
        assertEquals("t3", dto.title());
        assertEquals("c3", dto.content());
        assertEquals(newDate, dto.diaryDate());
        assertEquals(OLD_IMAGE, existingDiary.getImageKey());

        verify(diaryRepository).findByIdAndUserId(DIARY_ID, USER_ID);
        verifyNoMoreInteractions(diaryRepository);
    }

    @Test
    void updateDiary_whenNewImageKeySameAsOld_thenUpdateDiary() {
        // given
        given(diaryRepository.findByIdAndUserId(DIARY_ID, USER_ID)).willReturn(Optional.of(existingDiary));

        LocalDate newDate = LocalDate.of(2025, 9, 5);
        UpdateDiaryCommand cmd = new UpdateDiaryCommand("t4", "c4", newDate, OLD_IMAGE, false);

        // when
        DiaryDto dto = diaryService.updateDiary(USER_ID, DIARY_ID, cmd);

        // then
        assertEquals(OLD_IMAGE, dto.imageKey());            // 변경 없음
        assertEquals("t4", dto.title());
        assertEquals("c4", dto.content());
        assertEquals(newDate, dto.diaryDate());
        assertEquals(OLD_IMAGE, existingDiary.getImageKey());

        verify(diaryRepository).findByIdAndUserId(DIARY_ID, USER_ID);
        verifyNoMoreInteractions(diaryRepository);
    }

    @Test
    void updateDiary_whenNotFound_thenThrowException() {
        // given
        given(diaryRepository.findByIdAndUserId(DIARY_ID, USER_ID)).willReturn(Optional.empty());
        UpdateDiaryCommand cmd = new UpdateDiaryCommand("t", "c", LocalDate.now(), null, false);

        // when / then
        assertThrows(DiaryNotFoundException.class,
            () -> diaryService.updateDiary(USER_ID, DIARY_ID, cmd));

        verify(diaryRepository).findByIdAndUserId(DIARY_ID, USER_ID);
        verifyNoMoreInteractions(diaryRepository);
    }

}
