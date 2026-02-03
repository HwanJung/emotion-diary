import json
import logging

from pydantic import ValidationError

from app.core.database import get_pool
from app.repositories import DiaryRepository, AnalysisRepository
from app.repositories.analysis_repository import AnalysisStatus
from app.schemas import AnalysisMessage
from app.services.emotion_analyzer import emotion_analyzer

logger = logging.getLogger(__name__)


class MessageHandler:
    async def handle(self, message_body: str) -> bool:
        pool = get_pool()
        if pool is None:
            logger.error("Database pool not available")
            return False

        try:
            data = json.loads(message_body)
            msg = AnalysisMessage(**data)
        except (json.JSONDecodeError, ValidationError) as e:
            logger.error(f"Invalid message format: {e}")
            return True

        diary_repo = DiaryRepository(pool)
        analysis_repo = AnalysisRepository(pool)

        try:
            content = await diary_repo.get_diary_content(msg.diary_id)
            if content is None:
                logger.warning(f"Diary {msg.diary_id} not found, marking analysis as FAILED")
                await analysis_repo.mark_failed(msg.analysis_id)
                return True

            if not emotion_analyzer.is_ready():
                logger.error("Emotion analyzer not ready")
                return False

            try:
                emotion, color_code = emotion_analyzer.predict(content, msg.image_url)
                await analysis_repo.update_analysis_result(
                    analysis_id=msg.analysis_id,
                    emotion=emotion,
                    color_code=color_code,
                    status=AnalysisStatus.DONE,
                )
                logger.info(
                    f"Analysis completed: diary_id={msg.diary_id}, "
                    f"analysis_id={msg.analysis_id}, emotion={emotion}"
                )
                return True
            except Exception as e:
                logger.error(f"Analysis failed for diary {msg.diary_id}: {e}")
                await analysis_repo.mark_failed(msg.analysis_id)
                return True

        except Exception as e:
            logger.error(f"Database error: {e}")
            return False


message_handler = MessageHandler()
