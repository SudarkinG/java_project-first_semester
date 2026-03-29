package com.mipt.sudarkingeorgiy.controller;

import com.mipt.sudarkingeorgiy.dto.AttachmentResponseDto;
import com.mipt.sudarkingeorgiy.model.TaskAttachment;
import com.mipt.sudarkingeorgiy.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Attachments", description = "File upload and download")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @Operation(summary = "Upload file attachment")
    @ApiResponse(responseCode = "201", description = "File uploaded")
    @PostMapping(value = "/tasks/{taskId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentResponseDto> uploadAttachment(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        TaskAttachment saved = attachmentService.storeAttachment(taskId, file);
        AttachmentResponseDto dto = toDto(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "Download file attachment")
    @ApiResponse(responseCode = "200", description = "File returned")
    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) {
        TaskAttachment attachment = attachmentService.getAttachment(attachmentId);
        Resource resource = attachmentService.loadAsResource(attachmentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(resource);
    }

    @Operation(summary = "Delete file attachment")
    @ApiResponse(responseCode = "204", description = "File deleted")
    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all attachments for a task")
    @ApiResponse(responseCode = "200", description = "List of attachments returned")
    @GetMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<List<AttachmentResponseDto>> getAttachments(@PathVariable Long taskId) {
        List<AttachmentResponseDto> dtos = attachmentService.getAttachmentsByTaskId(taskId)
                .stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    private AttachmentResponseDto toDto(TaskAttachment attachment) {
        return new AttachmentResponseDto(
                attachment.getId(),
                attachment.getFileName(),
                attachment.getSize(),
                attachment.getUploadedAt());
    }
}
