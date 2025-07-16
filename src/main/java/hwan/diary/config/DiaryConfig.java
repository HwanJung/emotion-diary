package hwan.diary.config;

import hwan.diary.domain.diary.repository.DiaryRepository;
import hwan.diary.domain.diary.service.DiaryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiaryConfig {

    private final DiaryRepository diaryRepository;

    public DiaryConfig(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    @Bean
    public DiaryService diaryService(DiaryRepository diaryRepository) {
        return new DiaryService(diaryRepository);
    }
}
