from fastapi import APIRouter

from app.schemas import DiaryInput, AnalysisOutput
from app.services.emotion_analyzer import emotion_analyzer

router = APIRouter()


@router.post("/analyze-diary-fusion", response_model=AnalysisOutput)
async def analyze_diary_entry(diary_input: DiaryInput):
    if not emotion_analyzer.is_ready():
        emotion_analyzer.load_models()
    emotion, color_code = emotion_analyzer.predict(diary_input.text, diary_input.image_url)
    return AnalysisOutput(emotion=emotion, color_code=color_code)


@router.get("/health")
def health():
    return {"status": "ok"}


@router.get("/")
def read_root():
    return {"message": "Emotion Diary AI Server (Fusion Model - RabbitMQ Enabled)"}
