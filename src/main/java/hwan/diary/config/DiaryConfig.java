package hwan.diary.config;

import hwan.diary.repository.DiaryRepositoryCustom;
import hwan.diary.service.DiaryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiaryConfig {

    private final DiaryRepositoryCustom diaryRepository;

    public DiaryConfig(DiaryRepositoryCustom diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    @Bean
    public DiaryService diaryService(DiaryRepositoryCustom diaryRepository) {
        return new DiaryService(diaryRepository);
    }
}
