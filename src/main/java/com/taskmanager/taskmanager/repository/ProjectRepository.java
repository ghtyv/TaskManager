package com.taskmanager.taskmanager.repository;

import com.taskmanager.taskmanager.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByIdAndDeletedIsFalse(Long id);
    List<Project> findAllByDeletedIsFalse();
    List<Project> findAllByOpenAndDeletedIsFalse(boolean open);

}
