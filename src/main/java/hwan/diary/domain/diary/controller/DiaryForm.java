package hwan.diary.domain.diary.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class DiaryForm {
    public long uid;
    public String title;
    public String text;
    public MultipartFile image;

}
