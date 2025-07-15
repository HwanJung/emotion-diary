package hwan.diary.service;

import hwan.diary.domain.Diary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DiaryServiceTest {

    @Autowired
    DiaryService diaryService;


    @Test
    void 게시물_text_작성() {
        //given
        Diary diary = new Diary();
        diary.setText("testing");
        //when
        diaryService.posting(diary);
        //then
        Diary findDiary = diaryService.findOne(diary.getId()).get();
        assertEquals("testing", findDiary.getText());
    }

    @Test
    void findDiary() {
    }

    @Test
    void findOne() {
    }
}