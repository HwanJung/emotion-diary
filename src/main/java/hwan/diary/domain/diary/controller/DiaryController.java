package hwan.diary.domain.diary.controller;

import hwan.diary.domain.diary.dto.DiaryDto;
import hwan.diary.domain.diary.dto.command.CreateDiaryCommand;
import hwan.diary.domain.diary.dto.command.UpdateDiaryCommand;
import hwan.diary.domain.diary.dto.request.CreateDiaryRequest;
import hwan.diary.domain.diary.dto.request.UpdateDiaryRequest;
import hwan.diary.domain.diary.dto.response.SliceResponse;
import hwan.diary.domain.diary.service.DiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @GetMapping
    public SliceResponse<DiaryDto> listDiaries(
        Principal principal,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        Long userId = Long.parseLong(principal.getName());

        return diaryService.findDiaries(userId, pageable);
    }

    @GetMapping("/{id}")
    public DiaryDto getDiary(
        @PathVariable Long id,
        Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());

        return diaryService.findDiary(id, userId);
    }

    @PostMapping
    public ResponseEntity<DiaryDto> createDiary(
        Principal principal,
        @Valid @RequestBody CreateDiaryRequest request
    ) {
        Long userId = Long.parseLong(principal.getName());

        CreateDiaryCommand cmd = CreateDiaryCommand.from(request);

        DiaryDto created = diaryService.createDiary(cmd, userId);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiaryDto> updateDiary(
        Principal principal,
        @PathVariable Long id,
        @Valid @RequestBody UpdateDiaryRequest request
    ) {
        Long userId = Long.parseLong(principal.getName());

        UpdateDiaryCommand cmd = UpdateDiaryCommand.of(request);
        DiaryDto updatedDiary = diaryService.updateDiary(userId, id, cmd);

        return ResponseEntity.ok().body(updatedDiary);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(
        Principal principal,
        @PathVariable Long id
    ) {
        Long userId = Long.parseLong(principal.getName());

        diaryService.deleteDiary(userId, id);

        return ResponseEntity.noContent().build();
    }
}
