package hwan.diary.domain.diary.controller;

import hwan.diary.domain.diary.entity.Diary;
import hwan.diary.domain.diary.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DiaryController {
    private DiaryService diaryService;

    @Autowired
    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @GetMapping("/diary")
    public String diary() {
        return "diaryHome";
    }

    @GetMapping("/post")
    public String createDiary() {
        return "createDiary";
    }

    @PostMapping("/post")
    public String create(@ModelAttribute DiaryForm form) {
        Diary diary = new Diary();
        diary.setText(form.getText());
        //form으로 image받고 url로 변환해 service entity 대입
        diaryService.posting(diary);

        return "createDiary";
    }

    @GetMapping("/mydiary")
    public String myDiary() {
        return "diaryList";
    }


}
