import logging
from datetime import datetime
from enum import Enum

import asyncpg

logger = logging.getLogger(__name__)


def _utc_now() -> datetime:
    """UTC 시간을 timezone-naive datetime으로 반환 (PostgreSQL timestamp 호환)"""
    return datetime.utcnow()


class AnalysisStatus(str, Enum):
    PENDING = "PENDING"
    DONE = "DONE"
    FAILED = "FAILED"


class AnalysisRepository:
    def __init__(self, pool: asyncpg.Pool):
        self._pool = pool

    async def update_analysis_result(
        self,
        analysis_id: int,
        emotion: str,
        color_code: str,
        status: AnalysisStatus,
    ) -> bool:
        async with self._pool.acquire() as conn:
            result = await conn.execute(
                """
                UPDATE emotion_analysis
                SET emotion = $1,
                    color_code = $2,
                    status = $3,
                    analyzed_at = $4
                WHERE id = $5
                """,
                emotion,
                color_code,
                status.value,
                _utc_now(),
                analysis_id,
            )
            affected = int(result.split()[-1])
            if affected == 0:
                logger.warning(f"Analysis not found: {analysis_id}")
                return False
            logger.info(f"Updated analysis {analysis_id}: emotion={emotion}, status={status.value}")
            return True

    async def mark_failed(self, analysis_id: int) -> bool:
        async with self._pool.acquire() as conn:
            result = await conn.execute(
                """
                UPDATE emotion_analysis
                SET status = $1,
                    analyzed_at = $2
                WHERE id = $3
                """,
                AnalysisStatus.FAILED.value,
                _utc_now(),
                analysis_id,
            )
            affected = int(result.split()[-1])
            return affected > 0
