import logging
from typing import Optional

import asyncpg

logger = logging.getLogger(__name__)


class DiaryRepository:
    def __init__(self, pool: asyncpg.Pool):
        self._pool = pool

    async def get_diary_content(self, diary_id: int) -> Optional[str]:
        async with self._pool.acquire() as conn:
            row = await conn.fetchrow(
                "SELECT content FROM diaries WHERE id = $1",
                diary_id,
            )
            if row is None:
                logger.warning(f"Diary not found: {diary_id}")
                return None
            return row["content"]
