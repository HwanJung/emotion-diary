import logging
from io import BytesIO
from typing import Optional, Tuple

import requests
import torch
from PIL import Image
from torchvision import transforms
from transformers import BertTokenizer

from app.config import settings
from app.models import EmotionClassifier, get_vit_model, FusionMLP

logger = logging.getLogger(__name__)

TOKENIZER_NAME = "bert-base-multilingual-cased"
EMOTION_LABELS = ["JOY", "SADNESS", "SURPRISE", "ANGER", "FEAR", "DISGUST", "NEUTRAL"]
EMOTION_COLORS = {
    "JOY": "#FFD700",
    "SADNESS": "#4682B4",
    "SURPRISE": "#9370DB",
    "ANGER": "#DC143C",
    "FEAR": "#2F4F4F",
    "DISGUST": "#556B2F",
    "NEUTRAL": "#D3D3D3",
}


class EmotionAnalyzer:
    def __init__(self):
        self._device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self._tokenizer: Optional[BertTokenizer] = None
        self._text_model: Optional[EmotionClassifier] = None
        self._image_model = None
        self._fusion_model: Optional[FusionMLP] = None
        self._image_transform = transforms.Compose([
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
            transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),
        ])
        self._loaded = False

    def load_models(self) -> bool:
        if self._loaded:
            return True
        try:
            logger.info(f"Using device: {self._device}")
            self._tokenizer = BertTokenizer.from_pretrained(TOKENIZER_NAME)
            logger.info("Tokenizer loaded")

            self._text_model = EmotionClassifier(num_labels=len(EMOTION_LABELS))
            self._text_model.load_state_dict(
                torch.load(settings.text_model_path, map_location=self._device)
            )
            self._text_model.to(self._device)
            self._text_model.eval()
            logger.info("Text model loaded")

            self._image_model = get_vit_model(num_classes=len(EMOTION_LABELS))
            self._image_model.load_state_dict(
                torch.load(settings.image_model_path, map_location=self._device)
            )
            self._image_model.to(self._device)
            self._image_model.eval()
            logger.info("Image model loaded")

            self._fusion_model = FusionMLP(num_classes=len(EMOTION_LABELS))
            fusion_checkpoint = torch.load(settings.fusion_model_path, map_location=self._device)
            self._fusion_model.load_state_dict(fusion_checkpoint["model_state"])
            self._fusion_model.to(self._device)
            self._fusion_model.eval()
            logger.info("Fusion model loaded")

            self._loaded = True
            logger.info("All models loaded successfully")
            return True
        except Exception as e:
            logger.error(f"Failed to load models: {e}")
            return False

    def is_ready(self) -> bool:
        return self._loaded

    def predict(self, text: str, image_url: Optional[str] = None) -> Tuple[str, str]:
        if not self._loaded:
            raise RuntimeError("Models not loaded")

        with torch.no_grad():
            inputs = self._tokenizer(
                text,
                return_tensors="pt",
                max_length=128,
                truncation=True,
                padding="max_length",
            )
            input_ids = inputs["input_ids"].to(self._device)
            attention_mask = inputs["attention_mask"].to(self._device)
            text_output = self._text_model(input_ids=input_ids, attention_mask=attention_mask)

            if image_url:
                try:
                    response = requests.get(image_url, timeout=10)
                    response.raise_for_status()
                    image = Image.open(BytesIO(response.content)).convert("RGB")
                    image_tensor = self._image_transform(image).unsqueeze(0).to(self._device)
                    image_output = self._image_model(image_tensor)
                    combined = torch.cat([image_output, text_output], dim=1)
                    final_output = self._fusion_model(combined)
                except Exception as e:
                    logger.warning(f"Image processing failed: {e}. Using text only.")
                    final_output = text_output
            else:
                final_output = text_output

        pred_index = torch.argmax(final_output, dim=1).item()
        emotion = EMOTION_LABELS[pred_index]
        color = EMOTION_COLORS.get(emotion, "#FFFFFF")
        return emotion, color


emotion_analyzer = EmotionAnalyzer()
