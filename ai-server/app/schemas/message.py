from typing import Optional

from pydantic import BaseModel, Field


class AnalysisMessage(BaseModel):
    diary_id: int = Field(..., alias="diaryId")
    analysis_id: int = Field(..., alias="analysisId")
    image_url: Optional[str] = Field(None, alias="imageUrl")

    class Config:
        populate_by_name = True


class DiaryInput(BaseModel):
    text: str
    image_url: Optional[str] = None


class AnalysisOutput(BaseModel):
    emotion: str
    color_code: str
