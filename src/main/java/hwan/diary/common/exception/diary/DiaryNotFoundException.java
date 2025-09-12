package hwan.diary.common.exception.diary;

import hwan.diary.common.exception.ApplicationException;
import hwan.diary.common.exception.ErrorCode;
import org.slf4j.Logger;

public class DiaryNotFoundException extends ApplicationException {

    private final Long diaryId;
    private final Long userId;

    public DiaryNotFoundException(ErrorCode errorCode, Long diaryId, Long userId) {
        super(errorCode);
        this.diaryId = diaryId;
        this.userId = userId;
    }

    @Override
    public void log(Logger log) {
        log.warn("Diary not found in DB. diaryId={}, userId={}", diaryId, userId);
    }
}
