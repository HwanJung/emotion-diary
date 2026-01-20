package hwan.diary.domain.diary.service;

import hwan.diary.common.exception.ErrorCode;
import hwan.diary.common.exception.diary.DiaryNotFoundException;
import hwan.diary.domain.aws.service.S3Service;
import hwan.diary.domain.diary.client.analysis.EmotionAnalysisClient;
import hwan.diary.domain.diary.client.dto.AnalysisRequest;
import hwan.diary.domain.diary.client.dto.AnalysisResponse;
import hwan.diary.domain.diary.dto.DiaryDto;
import hwan.diary.domain.diary.dto.DiaryWithEmotionDto;
import hwan.diary.domain.diary.dto.command.CreateDiaryCommand;
import hwan.diary.domain.diary.dto.command.UpdateDiaryCommand;
import hwan.diary.domain.diary.dto.response.SliceResponse;
import hwan.diary.domain.diary.entity.Diary;
import hwan.diary.domain.diary.repository.DiaryRepository;
import hwan.diary.domain.diary.util.DiaryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final S3Service s3Service;
    private final EmotionAnalysisClient emotionAnalysisClient;
    private final CreateDiaryTxService createDiaryTxService;

    /**
     * Create a new {@link Diary} for the given user and save to DB.
     * Request an emotion analysis to an analysis server.
     * Then, save the result receiving from the server.
     *
     * @param createDiaryCommand command containing title, content, optional imageKey, and date
     * @param userId the id of owner
     * @return a {@link DiaryWithEmotionDto} containing a diary info
     */
    public DiaryWithEmotionDto createDiary(CreateDiaryCommand createDiaryCommand, Long userId) {
        Diary savedDiary = createDiaryTxService.createDiaryTx(createDiaryCommand, userId);

        try {
            log.info("Request to analysis server diaryId={}", savedDiary.getId());
            AnalysisResponse response = callAnalysisApi(savedDiary);
            createDiaryTxService.markDoneTx(savedDiary.getId(), response.emotion(), response.colorCode());
        } catch (ResourceAccessException e) {
            log.error("[ANALYSIS_SERVER][NETWORK] connection failed/timeout diaryId={} msg={}",
                savedDiary.getId(), e.getMessage(), e);

            createDiaryTxService.markFailedTx(savedDiary.getId());
        } catch (RestClientResponseException e) {
            log.error("[ANALYSIS_SERVER][HTTP_ERROR] diaryId={} status={} body={}",
                savedDiary.getId(), e.getRawStatusCode(), e.getResponseBodyAsString(), e);

            createDiaryTxService.markFailedTx(savedDiary.getId());
        }

        return diaryRepository.findDiaryWithEmotionById(userId, savedDiary.getId())
            .orElseThrow(() -> new DiaryNotFoundException(ErrorCode.DIARY_NOT_FOUND, savedDiary.getId(), userId));
    }

    private AnalysisResponse callAnalysisApi(Diary savedDiary) {
        String presignedUrl = null;
        if (savedDiary.getImageKey() != null) {
            presignedUrl = s3Service.createGetPresignedUrl(savedDiary.getImageKey());
        }

        return emotionAnalysisClient.postAnalysisRequest(
            new AnalysisRequest(savedDiary.getContent(), presignedUrl)
        );
    }

    /**
     * Find a diary corresponding the diary id and the userId in DB.
     *
     * @param id the diary id
     * @param userId the id of an owner.
     * @return Diary DTO containing a diary info {@link DiaryDto}
     */
    @Transactional(readOnly = true)
    public DiaryWithEmotionDto findDiaryWithEmotion(Long id, Long userId) {
        return diaryRepository.findDiaryWithEmotionById(userId, id)
            .orElseThrow(() -> new DiaryNotFoundException(ErrorCode.DIARY_NOT_FOUND, id, userId));
    }

    /**
     * Find slice of diaries by userId.
     * A page size is the requested size. A default size is 10.
     *
     * @param userId the id of owner
     * @param pageable paging info
     * @return SliceResponse containing content, page, size, hasNext.
     */
    @Transactional(readOnly = true)
    public SliceResponse<DiaryDto> findDiaries(Long userId, Pageable pageable) {
        int size = Math.min(Math.max(pageable.getPageSize(), 1), 100);
        Pageable p = PageRequest.of(pageable.getPageNumber(), size);

        Slice<DiaryDto> slice = diaryRepository.findSliceByUserId(userId, p)
            .map(DiaryMapper::toDiaryDto);

        return SliceResponse.of(slice);
    }

    /**
     * Update an existing diary
     *
     * @param cmd containing the info of the diary and the id of the diary
     * @param userId the id of the owner.
     * @return {@link DiaryDto} containing a diary info
     */
    @Transactional
    public DiaryWithEmotionDto updateDiary(Long userId, Long id, UpdateDiaryCommand cmd) {
        Diary diary = findDiaryByIdAndUserIdInternal(id, userId);

        if(cmd.clearImage()){
            diary.clearImage();
        } else if(cmd.newImageKey() != null &&
                !cmd.newImageKey().equals(diary.getImageKey())){
            diary.changeImage(cmd.newImageKey());
        }

        diary.update(cmd.title(), cmd.content(), cmd.diaryDate());

        return diaryRepository.findDiaryWithEmotionById(userId, id)
            .orElseThrow(() -> new DiaryNotFoundException(ErrorCode.DIARY_NOT_FOUND, id, userId));
    }

    /**
     * Soft delete the diary by id.
     * Soft deleting is conducted by a SoftDelete annotation of a Diary entity.
     *
     * @param id diary's id
     */
    @Transactional
    public void deleteDiary(Long userId, Long id) {
        Diary diary = findDiaryByIdAndUserIdInternal(id, userId);
        diary.softDelete();
    }

    /* helper */

    /**
     * Internal method of the DiaryService.
     * Find a diary corresponding the id and userId.
     * If there is not a diary corresponding the parameters, throw a custom exception.
     *
     *
     * @param id the id of a diary
     * @param userId the id of an owner
     * @return {@link Diary}
     * @throws {@link DiaryNotFoundException} if not found or not owned by userId
     */
    private Diary findDiaryByIdAndUserIdInternal(Long id, Long userId) {
        return diaryRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
            .orElseThrow(() -> new DiaryNotFoundException(ErrorCode.DIARY_NOT_FOUND, id, userId));
    }
}
