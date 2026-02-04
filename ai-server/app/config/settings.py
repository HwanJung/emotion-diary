from pydantic_settings import BaseSettings
from functools import lru_cache
from pydantic import Field


class Settings(BaseSettings):
    # PostgreSQL
    postgres_host: str = Field(default="localhost", validation_alias="DB_HOST")
    postgres_port: int = Field(default=5432, validation_alias="DB_PORT")
    postgres_db: str = Field(default="emotion_diary", validation_alias="DB_NAME")
    postgres_user: str = Field(default="postgres", validation_alias="DB_USERNAME")
    postgres_password: str = Field(default="postgres", validation_alias="DB_PASSWORD")

    # RabbitMQ
    rabbitmq_host: str = Field(default="localhost", validation_alias="RABBITMQ_HOST")
    rabbitmq_port: int = Field(default=5672, validation_alias="RABBITMQ_PORT")
    rabbitmq_username: str = Field(default="guest", validation_alias="RABBITMQ_USERNAME")
    rabbitmq_password: str = Field(default="guest", validation_alias="RABBITMQ_PASSWORD")
    rabbitmq_queue: str = Field(default="diary.analysis.queue", validation_alias="RABBITMQ_QUEUE")


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
