# main.py (image_url 선택 사항으로 최종 수정)

import torch
from fastapi import FastAPI, Request
from pydantic import BaseModel
from transformers import BertTokenizer
from torchvision import transforms
import requests
from PIL import Image
from io import BytesIO
# [수정] Optional 타입을 가져옵니다.
from typing import Optional
import logging

from models.fusion_model import EmotionClassifier, get_vit_model, FusionMLP

# --- 최종 확정된 정보 ---
TOKENIZER_NAME = "bert-base-multilingual-cased"
EMOTION_LABELS = ['Joy', 'Sadness', 'Surprise', 'Anger', 'Fear', 'Disgust', 'Neutral']
TEXT_MODEL_PATH = 'model_params/text_best_model.pt'
IMAGE_MODEL_PATH = 'model_params/image_best_model.pth'
FUSION_MODEL_PATH = 'model_params/fusion_head_model_0.pt'

EMOTION_COLORS = {
    'Joy': '#FFD700', 'Sadness': '#4682B4', 'Surprise': '#9370DB',
    'Anger': '#DC143C', 'Fear': '#2F4F4F', 'Disgust': '#556B2F', 'Neutral': '#D3D3D3'
}

# --- 서버 초기 설정 ---
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
print(f"사용 장치: {device}")

text_model, image_model, fusion_model = None, None, None

try:
    tokenizer = BertTokenizer.from_pretrained(TOKENIZER_NAME)
    print("토크나이저 로딩 성공.")

    print("3개의 모델을 각각 불러옵니다...")
    text_model = EmotionClassifier(num_labels=len(EMOTION_LABELS))
    text_model.load_state_dict(torch.load(TEXT_MODEL_PATH, map_location=device))
    text_model.to(device)
    text_model.eval()
    print("  - 텍스트 모델 로딩 성공.")

    image_model = get_vit_model(num_classes=len(EMOTION_LABELS))
    image_model.load_state_dict(torch.load(IMAGE_MODEL_PATH, map_location=device))
    image_model.to(device)
    image_model.eval()
    print("  - 이미지 모델 로딩 성공.")

    fusion_model = FusionMLP(num_classes=len(EMOTION_LABELS))
    fusion_checkpoint = torch.load(FUSION_MODEL_PATH, map_location=device)
    fusion_model.load_state_dict(fusion_checkpoint['model_state'])
    fusion_model.to(device)
    fusion_model.eval()
    print("  - 퓨전 헤드 모델 로딩 성공.")
    
    print("\n✅ 모든 모델의 가중치를 성공적으로 불러왔습니다!")

except Exception as e:
    print(f"\n❌ 모델 로딩 실패: {e}")

image_transform = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),
])

# --- FastAPI 앱 설정 ---
app = FastAPI()
logger = logging.getLogger("uvicorn.error")

# [수정] DiaryInput 모델에서 image_url을 선택 사항으로 변경합니다.
class DiaryInput(BaseModel):
    text: str
    image_url: Optional[str] = None

class AnalysisOutput(BaseModel):
    emotion: str
    color_code: str

# [수정] 예측 함수 로직을 image_url 유무에 따라 분기 처리합니다.
def predict_emotion(text: str, image_url: Optional[str] = None):
    if not all([text_model, image_model, fusion_model, tokenizer]):
        return "모델 준비 안됨", "#000000"
    
    with torch.no_grad():
        # --- 텍스트 모델 실행 (공통 과정) ---
        inputs = tokenizer(text, return_tensors='pt', max_length=128, truncation=True, padding='max_length')
        input_ids = inputs['input_ids'].to(device)
        attention_mask = inputs['attention_mask'].to(device)
        text_output_7d = text_model(input_ids=input_ids, attention_mask=attention_mask)

        # --- image_url 유무에 따른 분기 ---
        if image_url:
            # [이미지가 있을 경우] -> 퓨전 모델 로직 실행
            try:
                response = requests.get(image_url)
                response.raise_for_status()
                image = Image.open(BytesIO(response.content)).convert("RGB")
                image_tensor = image_transform(image).unsqueeze(0).to(device)
                image_output_7d = image_model(image_tensor)
            except Exception as e:
                print(f"이미지 처리 실패: {e}. 텍스트 모델만 사용하여 분석합니다.")
                # 이미지를 불러오다 실패하면 텍스트 결과만 사용
                final_outputs = text_output_7d
            else:
                combined_vector_14d = torch.cat([image_output_7d, text_output_7d], dim=1)
                final_outputs = fusion_model(combined_vector_14d)
        else:
            # [이미지가 없을 경우] -> 텍스트 모델 결과만 사용
            final_outputs = text_output_7d

    pred_index = torch.argmax(final_outputs, dim=1).item()
    emotion = EMOTION_LABELS[pred_index]
    color = EMOTION_COLORS.get(emotion, "#FFFFFF")
    return emotion, color

@app.middleware("http")
async def log_raw_body(request: Request, call_next):
    # analyze-diary-fusion 요청만 로깅
    if request.url.path == "/analyze-diary-fusion":
        raw_body = await request.body()
        logger.info("[RAW][%s %s] headers=%s", request.method, request.url.path, dict(request.headers))
        logger.info("[RAW][%s %s] body=%s", request.method, request.url.path, raw_body)

    response = await call_next(request)
    return response

@app.post("/analyze-diary-fusion", response_model=AnalysisOutput)
async def analyze_diary_entry(diary_input: DiaryInput):
    emotion, color_code = predict_emotion(diary_input.text, diary_input.image_url)
    return AnalysisOutput(emotion=emotion, color_code=color_code)

@app.get("/health")
def health():
    return {"status": "ok"}

@app.get("/")
def read_root():
    return {"message": "일기 감정 분석 AI 서버 (Fusion Model - Image Optional)"}