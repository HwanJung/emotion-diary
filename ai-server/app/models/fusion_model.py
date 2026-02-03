import torch
import torch.nn as nn
from transformers import BertModel
import timm


class EmotionClassifier(nn.Module):
    def __init__(self, num_labels: int = 7):
        super(EmotionClassifier, self).__init__()
        self.bert = BertModel.from_pretrained("bert-base-multilingual-cased")
        self.dropout = nn.Dropout(0.3)
        self.classifier = nn.Linear(self.bert.config.hidden_size, num_labels)

    def forward(self, input_ids, attention_mask):
        outputs = self.bert(input_ids=input_ids, attention_mask=attention_mask)
        x = self.dropout(outputs.pooler_output)
        return self.classifier(x)


def get_vit_model(num_classes: int = 7, pretrained: bool = True):
    model = timm.create_model(
        "vit_base_patch16_224",
        pretrained=pretrained,
        num_classes=num_classes,
    )
    return model


class FusionMLP(nn.Module):
    def __init__(
        self,
        input_dim: int = 14,
        hidden_dim: int = 128,
        num_classes: int = 7,
        dropout: float = 0.5,
    ):
        super().__init__()
        self.bn1 = nn.BatchNorm1d(input_dim)
        self.fc1 = nn.Linear(input_dim, hidden_dim)
        self.bn2 = nn.BatchNorm1d(hidden_dim)
        self.dropout = nn.Dropout(p=dropout)
        self.fc2 = nn.Linear(hidden_dim, num_classes)

    def forward(self, combined_vec: torch.Tensor):
        x = self.bn1(combined_vec)
        x = self.fc1(x)
        x = self.bn2(x)
        x = nn.functional.relu(x)
        x = self.dropout(x)
        return self.fc2(x)
