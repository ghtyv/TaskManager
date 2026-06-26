package com.taskmanager.task_manager_rest_api_spring_boot.service;

import com.taskmanager.task_manager_rest_api_spring_boot.model.Tasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.taskmanager.task_manager_rest_api_spring_boot.repository.TasksRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TasksService {

    @Autowired
    private TasksRepository tasksRepository;

    public List<Tasks> findAllTasks() {

        return tasksRepository.findAll();

    }

    public Optional<Tasks> findTaskById(Long id) {

        return tasksRepository.findById(id);

    }

    public Tasks updateTask(Tasks task) {

        return tasksRepository.save(task);

    }

    public Tasks closeTaskById(Long id) {

        Tasks task = findTaskById(id).orElseThrow();
        task.setOpen(false);

        return updateTask(task);

    }

    public Tasks openTaskById(Long id) {

        Tasks task = findTaskById(id).orElseThrow();
        task.setOpen(true);

        return updateTask(task);

    }

    public Tasks createTask(String title, String description) {

        Tasks task = new Tasks();

        task.setTitle(title);
        task.setDescription(description);

        tasksRepository.save(task);

        return task;

    }



}
