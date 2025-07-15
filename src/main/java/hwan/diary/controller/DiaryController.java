package hwan.diary.controller;

import hwan.diary.domain.Diary;
import hwan.diary.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DiaryController {
    private DiaryService diaryService;

    @Autowired
    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @GetMapping("/post")
    public String createDiary() {
        return "diary/createDiaary";
    }

    @PostMapping("/post")
    public String create(DiaryForm form) {
        Diary diary = new Diary();
        diary.setText(form.getText());

        diaryService.posting(diary);

        return "diary/createDiaary";
    }



}
