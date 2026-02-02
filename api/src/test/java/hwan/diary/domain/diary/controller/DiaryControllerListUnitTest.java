package hwan.diary.domain.diary.controller;

import hwan.diary.domain.diary.dto.DiaryDto;
import hwan.diary.domain.diary.dto.DiaryWithEmotionDto;
import hwan.diary.domain.diary.dto.response.SliceResponse;
import hwan.diary.domain.diary.entity.Diary;
import hwan.diary.domain.diary.enums.AnalysisStatus;
import hwan.diary.domain.diary.enums.Emotion;
import hwan.diary.domain.diary.service.DiaryService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DiaryControllerListUnitTest {

    @Test
    void listDiaries_passesUserIdAndPageable_andReturnsServiceValue() {
        // given
        DiaryService diaryService = mock(DiaryService.class);
        DiaryController controller = new DiaryController(diaryService);

        Principal principal = () -> "42";
        Pageable pageable = PageRequest.of(1, 5, Sort.by(Sort.Order.desc("diaryDate")));

        DiaryWithEmotionDto d1 = new DiaryWithEmotionDto(
            1L,
            "t1",
            "c1",
            "img1",
            LocalDate.parse("2025-09-01"),
            11L,
            AnalysisStatus.DONE,
            Emotion.JOY,
            Emotion.JOY.getColorCode()
            );
        DiaryWithEmotionDto d2 = new DiaryWithEmotionDto(
            2L,
            "t2",
            "c2",
            "img2",
            LocalDate.parse("2025-09-02"),
            22L,
            AnalysisStatus.DONE,
            Emotion.ANGER,
            Emotion.ANGER.getColorCode()
        );

        SliceResponse<DiaryWithEmotionDto> expected = new SliceResponse<>(List.of(d1, d2), 1, 5, false);

        when(diaryService.findDiaries(42L, pageable)).thenReturn(expected);

        // when
        SliceResponse<DiaryWithEmotionDto> actual = controller.listDiaries(principal, pageable);

        // then
        assertThat(actual).isSameAs(expected);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(diaryService).findDiaries(eq(42L), pageableCaptor.capture());
        Pageable passed = pageableCaptor.getValue();

        assertThat(passed.getPageNumber()).isEqualTo(1);
        assertThat(passed.getPageSize()).isEqualTo(5);
        assertThat(passed.getSort().getOrderFor("diaryDate")).isNotNull();
        assertThat(passed.getSort().getOrderFor("diaryDate").getDirection()).isEqualTo(Sort.Direction.DESC);

        verifyNoMoreInteractions(diaryService);
    }
}
