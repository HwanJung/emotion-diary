import asyncpg
import logging
from typing import Optional

from app.config import settings

logger = logging.getLogger(__name__)

_pool: Optional[asyncpg.Pool] = None


async def init_db() -> asyncpg.Pool:
    global _pool
    if _pool is None:
        logger.info(f"Connecting to PostgreSQL at {settings.postgres_host}:{settings.postgres_port}")
        _pool = await asyncpg.create_pool(
            host=settings.postgres_host,
            port=settings.postgres_port,
            database=settings.postgres_db,
            user=settings.postgres_user,
            password=settings.postgres_password,
            min_size=2,
            max_size=10,
        )
        logger.info("PostgreSQL connection pool created")
    return _pool


async def close_db() -> None:
    global _pool
    if _pool is not None:
        await _pool.close()
        _pool = None
        logger.info("PostgreSQL connection pool closed")


def get_pool() -> Optional[asyncpg.Pool]:
    return _pool
