package com.taskmanager.taskmanager.controller;

import com.taskmanager.taskmanager.dto.AttachmentResponseDto;
import com.taskmanager.taskmanager.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects/{projectId}/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @GetMapping
    public List<AttachmentResponseDto> findAllByProjectId(@PathVariable("projectId") Long projectId) {
        return attachmentService.findAllByProjectId(projectId);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UUID prepareUploadAttachment(@RequestParam("file") MultipartFile file,
                                        @PathVariable("projectId") Long projectId) {
        return attachmentService.prepareUploadAttachment(file, projectId);
    }

    @GetMapping("/{attachmentId}/download")
    public ResponseEntity<Resource> downloadAttachmentById(@PathVariable("projectId") Long projectId,
                                                           @PathVariable("attachmentId") Long attachmentId) {
        return attachmentService.downloadAttachmentById(projectId, attachmentId);
    }

}
