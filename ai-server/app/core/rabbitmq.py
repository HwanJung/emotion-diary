import logging
from typing import Callable, Optional

import aio_pika
from aio_pika import IncomingMessage
from aio_pika.abc import AbstractRobustConnection, AbstractChannel

from app.config import settings

logger = logging.getLogger(__name__)


class RabbitMQConsumer:
    def __init__(self):
        self._connection: Optional[AbstractRobustConnection] = None
        self._channel: Optional[AbstractChannel] = None
        self._message_handler: Optional[Callable] = None

    def set_message_handler(self, handler: Callable) -> None:
        self._message_handler = handler

    async def connect(self) -> None:
        logger.info(f"Connecting to RabbitMQ at {settings.rabbitmq_host}:{settings.rabbitmq_port}")
        self._connection = await aio_pika.connect_robust(settings.rabbitmq_url)
        self._channel = await self._connection.channel()
        await self._channel.set_qos(prefetch_count=1)
        logger.info("RabbitMQ connection established")

    async def start_consuming(self) -> None:
        if self._channel is None:
            raise RuntimeError("Not connected to RabbitMQ")

        queue = await self._channel.declare_queue(
            settings.rabbitmq_queue,
            durable=True,
        )
        logger.info(f"Start consuming from queue: {settings.rabbitmq_queue}")

        async with queue.iterator() as queue_iter:
            async for message in queue_iter:
                await self._process_message(message)

    async def _process_message(self, message: IncomingMessage) -> None:
        if self._message_handler is None:
            logger.error("No message handler set")
            await message.nack(requeue=True)
            return

        try:
            body = message.body.decode("utf-8")
            logger.info(f"Received message: {body}")

            should_ack = await self._message_handler(body)

            if should_ack:
                await message.ack()
                logger.info("Message acknowledged")
            else:
                await message.nack(requeue=True)
                logger.warning("Message nacked (will retry)")
        except Exception as e:
            logger.error(f"Error processing message: {e}")
            await message.nack(requeue=True)

    async def close(self) -> None:
        if self._connection is not None:
            await self._connection.close()
            self._connection = None
            self._channel = None
            logger.info("RabbitMQ connection closed")


rabbitmq_consumer = RabbitMQConsumer()
