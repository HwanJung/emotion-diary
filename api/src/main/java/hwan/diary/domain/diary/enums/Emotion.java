package hwan.diary.domain.diary.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Emotion {
    JOY("#FFD700"),
    SADNESS("#4682B4"),
    SURPRISE("#9370DB"),
    ANGER("#DC143C"),
    FEAR("#2F4F4F"),
    DISGUST("#556B2F"),
    NEUTRAL("#D3D3D3");

    private final String colorCode;
}
