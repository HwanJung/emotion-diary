package hwan.diary.service;

import hwan.diary.domain.diary.entity.Diary;
import hwan.diary.domain.diary.service.DiaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Commit
class DiaryServiceTest {

    @Autowired
    DiaryService diaryService;


    @Test
    void 게시물_text_작성() {
        //given
        Diary diary = new Diary();
        diary.setTitle("Title");
        diary.setText("testing");
        diary.setUid(8888);
        diary.setImage_url("local");
        //when
        Long saveId = diaryService.posting(diary);
        //then
        Diary findDiary = diaryService.findOne(saveId).get();
        System.out.println(findDiary.getTitle());
        System.out.println(findDiary.getText());
        System.out.println(findDiary.getUid());
        System.out.println(findDiary.getImage_url());
        System.out.println(findDiary.getCreated_at());
        System.out.println(findDiary.getModified_at());

        assertThat(diary.getText()).isEqualTo(findDiary.getText());
    }

    @Test
    void findDiary() {
    }

    @Test
    void findOne() {
    }
}