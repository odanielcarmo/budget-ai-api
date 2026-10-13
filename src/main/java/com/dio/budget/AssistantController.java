package com.dio.budget;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/** Endpoints do assistente: comando por texto ou por audio. */
@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final AssistantService assistantService;
    private final TranscriptionService transcriptionService;

    public AssistantController(AssistantService assistantService, TranscriptionService transcriptionService) {
        this.assistantService = assistantService;
        this.transcriptionService = transcriptionService;
    }

    @PostMapping("/text")
    public Map<String, String> text(@Valid @RequestBody CommandRequest request) {
        return Map.of("answer", assistantService.handle(request.command()));
    }

    @PostMapping(value = "/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> audio(@RequestPart("file") MultipartFile file) throws IOException {
        String transcription = transcriptionService.transcribe(file);
        String answer = assistantService.handle(transcription);
        return Map.of("transcription", transcription, "answer", answer);
    }
}
