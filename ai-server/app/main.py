import asyncio
import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI, Request

from app.api import router
from app.core.database import init_db, close_db
from app.core.rabbitmq import rabbitmq_consumer
from app.services.emotion_analyzer import emotion_analyzer
from app.services.message_handler import message_handler

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("Starting up...")

    emotion_analyzer.load_models()

    await init_db()

    rabbitmq_consumer.set_message_handler(message_handler.handle)
    await rabbitmq_consumer.connect()

    consumer_task = asyncio.create_task(rabbitmq_consumer.start_consuming())
    logger.info("RabbitMQ consumer started")

    yield

    logger.info("Shutting down...")
    consumer_task.cancel()
    try:
        await consumer_task
    except asyncio.CancelledError:
        pass

    await rabbitmq_consumer.close()
    await close_db()
    logger.info("Shutdown complete")


app = FastAPI(
    title="Emotion Diary AI Server",
    description="Emotion analysis service with RabbitMQ integration",
    lifespan=lifespan,
)


@app.middleware("http")
async def log_raw_body(request: Request, call_next):
    if request.url.path == "/analyze-diary-fusion":
        raw_body = await request.body()
        logger.info("[RAW][%s %s] headers=%s", request.method, request.url.path, dict(request.headers))
        logger.info("[RAW][%s %s] body=%s", request.method, request.url.path, raw_body)
    response = await call_next(request)
    return response


app.include_router(router)
