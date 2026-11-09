package com.taskmanager.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentResponseDto {

    private Long id;
    private String name;
    private Long size;
    private Long taskId;

}
