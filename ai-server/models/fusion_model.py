# model/fusion_model.py (14d 입력을 받는 최종 수정본)

import torch
import torch.nn as nn
from transformers import BertModel
import timm

# 텍스트 모델: 최종 7d 예측 벡터를 출력하도록 classifier가 포함된 완전한 형태.
class EmotionClassifier(nn.Module):
    def __init__(self, num_labels=7):
        super(EmotionClassifier, self).__init__()
        self.bert = BertModel.from_pretrained("bert-base-multilingual-cased")
        self.dropout = nn.Dropout(0.3)
        self.classifier = nn.Linear(self.bert.config.hidden_size, num_labels)

    def forward(self, input_ids, attention_mask):
        outputs = self.bert(input_ids=input_ids, attention_mask=attention_mask)
        x = self.dropout(outputs.pooler_output)
        return self.classifier(x)

# 이미지 모델: 최종 7d 예측 벡터를 출력하는 ViT 모델 생성 함수.
def get_vit_model(num_classes=7, pretrained=True):
    model = timm.create_model('vit_base_patch16_224', pretrained=pretrained, num_classes=num_classes)
    return model

# 퓨전 헤드 모델: 오류 메시지를 통해 확인된 정확한 구조로 복원.
# 입력 14, 은닉 128, BatchNorm 레이어 포함.
class FusionMLP(nn.Module):
    def __init__(self, input_dim=14, hidden_dim=128, num_classes=7, dropout=0.5):
        super().__init__()
        self.bn1 = nn.BatchNorm1d(input_dim)
        self.fc1 = torch.nn.Linear(input_dim, hidden_dim)
        self.bn2 = nn.BatchNorm1d(hidden_dim)
        self.dropout = nn.Dropout(p=dropout)
        self.fc2 = torch.nn.Linear(hidden_dim, num_classes)
    
    def forward(self, combined_vec: torch.Tensor):
        x = self.bn1(combined_vec)
        x = self.fc1(x)
        x = self.bn2(x)
        x = nn.functional.relu(x)
        x = self.dropout(x)
        output = self.fc2(x)
        return output