package hwan.diary.domain.diary.dto.response;

import org.springframework.data.domain.Slice;

import java.util.List;

public record SliceResponse<T>(
    List<T> content,
    int page,
    int size,
    boolean hasNext
) {
    public static <T> SliceResponse<T> of(Slice<T> s) {
        return new SliceResponse<>(
            s.getContent(),
            s.getNumber(),
            s.getSize(),
            s.hasNext()
        );
    }
}