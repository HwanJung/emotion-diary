package hwan.diary.domain.diary.controller;

import hwan.diary.domain.diary.dto.DiaryWithEmotionDto;
import hwan.diary.domain.diary.dto.command.CreateDiaryCommand;
import hwan.diary.domain.diary.dto.command.UpdateDiaryCommand;
import hwan.diary.domain.diary.dto.request.CreateDiaryRequest;
import hwan.diary.domain.diary.dto.request.UpdateDiaryRequest;
import hwan.diary.domain.diary.dto.response.SliceResponse;
import hwan.diary.domain.diary.service.DiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @GetMapping
    public SliceResponse<DiaryWithEmotionDto> listDiaries(
        Principal principal,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        Long userId = Long.parseLong(principal.getName());

        return diaryService.findDiaries(userId, pageable);
    }

    @GetMapping("/{id}")
    public DiaryWithEmotionDto getDiary(
        @PathVariable("id") Long id,
        Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());

        return diaryService.findDiaryWithEmotion(id, userId);
    }

    @PostMapping
    public ResponseEntity<DiaryWithEmotionDto> createDiary(
        Principal principal,
        @Valid @RequestBody CreateDiaryRequest request
    ) {
        Long userId = Long.parseLong(principal.getName());

        CreateDiaryCommand cmd = CreateDiaryCommand.from(request);

        DiaryWithEmotionDto created = diaryService.createDiary(cmd, userId);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{diaryId}")
            .buildAndExpand(created.diaryId())
            .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiaryWithEmotionDto> updateDiary(
        Principal principal,
        @PathVariable("id") Long id,
        @Valid @RequestBody UpdateDiaryRequest request
    ) {
        Long userId = Long.parseLong(principal.getName());

        UpdateDiaryCommand cmd = UpdateDiaryCommand.of(request);
        DiaryWithEmotionDto updatedDiary = diaryService.updateDiary(userId, id, cmd);

        return ResponseEntity.ok().body(updatedDiary);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(
        Principal principal,
        @PathVariable("id") Long id
    ) {
        Long userId = Long.parseLong(principal.getName());

        diaryService.deleteDiary(userId, id);

        return ResponseEntity.noContent().build();
    }
}
