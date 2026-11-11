package com.taskmanager.taskmanager.service;

import com.taskmanager.taskmanager.dto.AttachmentResponseDto;
import com.taskmanager.taskmanager.mapper.AttachmentMapper;
import com.taskmanager.taskmanager.model.Attachment;
import com.taskmanager.taskmanager.model.Project;
import com.taskmanager.taskmanager.model.Task;
import com.taskmanager.taskmanager.repository.AttachmentRepository;
import com.taskmanager.taskmanager.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;
    private final ProjectRepository projectRepository;
    private final S3Client s3Client;

    @Value("${app.upload.temp-dir}")
    private String tempDir;

    @Value("${app.s3.bucket}")
    private String bucketName;

    /* Данный метод находит информацию (записанную в БД) о всех файлах проекта: */

    @Transactional(readOnly = true)
    public List<AttachmentResponseDto> findAllByProjectId(Long projectId) {
        projectRepository.findById(projectId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Provided Project Not Found"
                        )
                );

        return attachmentMapper.listToDto(attachmentRepository.findAllByProjectId(projectId));
    }

    /* Данный метод сохраняет переданный файл во временный каталог: */

    public UUID prepareUploadAttachment(MultipartFile attachment, Long projectId) {
        projectRepository.findById(projectId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Provided Project Not Found"
                        )
                );

        if (attachment.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File must not be empty");
        }

        UUID fileId = UUID.randomUUID();

        /* Преобразование строки пути временного каталога в объект типа Path: */

        Path directory = Path.of(tempDir);

        /* Создание временного каталога и его родительских каталогов. Если временный каталог уже существует,
        * то ошибки не будет: */

        try {
            Files.createDirectories(directory);
        }  catch (IOException e) {throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unable to create directory",
                e);
        }

        /* Формирование пути файла (то есть должен сформироваться следующий путь: tempDir/fileId): */

        Path filePath = directory.resolve(fileId.toString());

        /* Передача полученного файла во временный каталог: */

        try {
            attachment.transferTo(filePath);
        } catch (IOException e) {throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unable to save file",
                e
        );
        }

        return fileId;
    }

    @Transactional
    public void uploadAttachment(Project project, Task task, UUID fileId, String originalFilename) {
        /* Получение пути файла во временном каталоге */

        Path filePath = Path.of(tempDir).resolve(fileId.toString());

        if (!Files.isRegularFile(filePath)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Temporary file not found"
            );
        }

        Long size;
        try {
            size = Files.size(filePath);
        } catch (IOException e) {throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unable to determine temporary file size",
                e
        );
        }

        /* Формирование ключа для объекта s3-хранилища и загрузка файла из временного каталога в s3-хранилище: */

        String objectKey = "attachments/" + fileId;

        s3Client.putObject(request ->
                request
                        .bucket(bucketName)
                        .key(objectKey)
                        .ifNoneMatch("*"),
                RequestBody.fromFile(filePath)
        );

        /* Long size = s3Client.headObject(request ->
                request
                        .bucket(bucketName)
                        .key(objectKey))
                .contentLength(); */

        var attachment = new Attachment(objectKey, originalFilename, size, project, task);
        attachmentRepository.save(attachment);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unable to delete temporary file",
                e
        );
        }
    }


    @Transactional(readOnly = true)
    public ResponseEntity<Resource> downloadAttachmentById(Long projectId, Long attachmentId) {
        // Получение информации о файле из PostgreSQL
        Attachment attachment = attachmentRepository.findByIdAndProjectId(attachmentId, projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Attachment not found"
                ));

        // Получение содержимого файла из S3
        ResponseInputStream<GetObjectResponse> s3Stream;

        try {
            s3Stream = s3Client.getObject(request -> request
                    .bucket(bucketName)
                    .key(attachment.getObjectKey())
            );
        } catch (S3Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Unable to download attachment from S3",
                    e
            );
        }

        // Преобразование потока в Resource
        Resource resource = new InputStreamResource(s3Stream);

        // Формирование HTTP-ответа
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(attachment.getSize())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(
                                        attachment.getName(),
                                        StandardCharsets.UTF_8
                                )
                                .build()
                                .toString()
                )
                .body(resource);
    }

}
