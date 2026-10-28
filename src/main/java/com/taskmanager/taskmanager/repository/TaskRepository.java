package com.taskmanager.taskmanager.repository;

import com.taskmanager.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndDeletedIsFalse(Long id);
    List<Task> findAllByProjectIdAndParentTaskIsNullAndDeletedIsFalse(Long id);
    List<Task> findAllByProjectIdAndOpenAndParentTaskIsNullAndDeletedIsFalse(Long id, boolean open);

}

