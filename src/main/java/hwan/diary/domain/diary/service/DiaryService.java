package hwan.diary.domain.diary.service;

import hwan.diary.domain.diary.controller.DiaryForm;
import hwan.diary.domain.diary.entity.Diary;
import hwan.diary.domain.diary.repository.DiaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DiaryService {
    private final DiaryRepository diaryRepository;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads/";

    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    /**
     * 일기작성
     */
    public long posting(DiaryForm form) {
        Diary diary = new Diary();
        diary.setContent(form.getContent());
        diary.setTitle(form.getTitle());
        diary.setUid(form.getUid());
        MultipartFile image = form.getImage();
        if (image != null && !image.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
            } catch (IOException e) {
                throw new RuntimeException("mkdir failed", e);
            }

            try {
                String fileName = UUID.randomUUID() +
                        "_" + image.getOriginalFilename();
                String filePath = UPLOAD_DIR + fileName;
                File saveFile = new File(filePath);
                image.transferTo(saveFile);
                diary.setImage_url(filePath);
            } catch (IOException e) {
                throw new RuntimeException("upload failed", e);
            }
        }
        diaryRepository.save(diary);
        return diary.getId();
    }

    /**
     * 전체 일기 조회
     */

    public List<Diary> findDiary() {
        return diaryRepository.findAll();
    }

    public Optional<Diary> findOne(long id) {
        return diaryRepository.findById(id);
    }
}
