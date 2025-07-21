package hwan.diary.service;

import hwan.diary.domain.diary.controller.DiaryForm;
import hwan.diary.domain.diary.entity.Diary;
import hwan.diary.domain.diary.service.DiaryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Commit
class DiaryServiceTest {

    @Autowired
    DiaryService diaryService;

    private final List<String> testImage = new ArrayList<>();
    @Test
    void 일기_작성() {
        //given
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg",
                "image/jpeg", new byte[10]);
        DiaryForm diaryForm = new DiaryForm();
        diaryForm.setTitle("Title");
        diaryForm.setContent("testing");
        diaryForm.setImage(image);
        diaryForm.setUid(8888);
        //when
        Long saveId = diaryService.posting(diaryForm);

        //then
        Diary findDiary = diaryService.findOne(saveId).get();
        testImage.add(findDiary.getImage_url());

        System.out.println(findDiary.getTitle());
        System.out.println(findDiary.getContent());
        System.out.println(findDiary.getUid());
        System.out.println(findDiary.getImage_url());
        System.out.println(findDiary.getCreated_at());
        System.out.println(findDiary.getModified_at());

        assertThat(diaryForm.getContent()).isEqualTo(findDiary.getContent());
        assertTrue(new File(findDiary.getImage_url()).exists());
    }

    @AfterEach
    void 테스트_이미지_삭제() {
        for (String path : testImage) {
            File file = new File(path);
            if (file.exists()) file.delete();
        }
        testImage.clear();
        System.out.println("clear /uploads");
    }

    @Test
    void 전체_일기_조회() {
    }

    @Test
    void 일기_수정() {
    }
}