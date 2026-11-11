package com.taskmanager.taskmanager.repository;

import com.taskmanager.taskmanager.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findAllByProjectId(Long projectId);
    Optional<Attachment> findByIdAndProjectId(Long attachmentId, Long projectId);
}
