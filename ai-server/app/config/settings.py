from pydantic_settings import BaseSettings
from functools import lru_cache


class Settings(BaseSettings):
    # PostgreSQL
    postgres_host: str = "localhost"
    postgres_port: int = 5432
    postgres_db: str = "emotion_diary"
    postgres_user: str = "postgres"
    postgres_password: str = "postgres"

    # RabbitMQ
    rabbitmq_host: str = "localhost"
    rabbitmq_port: int = 5672
    rabbitmq_username: str = "guest"
    rabbitmq_password: str = "guest"
    rabbitmq_queue: str = "diary.analysis.queue"

    # Model paths
    text_model_path: str = "model_params/text_best_model.pt"
    image_model_path: str = "model_params/image_best_model.pth"
    fusion_model_path: str = "model_params/fusion_head_model_0.pt"

    @property
    def postgres_dsn(self) -> str:
        return f"postgresql://{self.postgres_user}:{self.postgres_password}@{self.postgres_host}:{self.postgres_port}/{self.postgres_db}"

    @property
    def rabbitmq_url(self) -> str:
        return f"amqp://{self.rabbitmq_username}:{self.rabbitmq_password}@{self.rabbitmq_host}:{self.rabbitmq_port}/"

    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"


@lru_cache
def get_settings() -> Settings:
    return Settings()


settings = get_settings()
