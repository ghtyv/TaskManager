package com.taskmanager.taskmanager.mapper;

import com.taskmanager.taskmanager.dto.AttachmentResponseDto;
import com.taskmanager.taskmanager.model.Attachment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AttachmentMapper {

    public AttachmentResponseDto toDto(Attachment attachment) {
        Long id = attachment.getId();
        String name = attachment.getName();
        Long size = attachment.getSize();

        return new AttachmentResponseDto(id, name, size);
    }

    public List<AttachmentResponseDto> listToDto(List<Attachment> attachmentList) {
        return attachmentList
                .stream()
                .map(this::toDto)
                .toList();
    }

}
