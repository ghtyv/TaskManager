package controller;

import model.Tasks;
import org.jspecify.annotations.NonNull;
import org.springframework.web.bind.annotation.*;
import repository.TasksRepository;
import service.TasksService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TasksController {

    private TasksService tasksService;
    private TasksRepository tasksRepository;

    @GetMapping("/tasks")
    public List<Tasks> findAllTasks() {

        return tasksService.findAllTasks();

    }

    @GetMapping("/task/{id}")
    public Tasks findTaskById(@PathVariable("id") Long id) {

        return tasksService.findTaskById(id).orElseThrow();

    }

    @GetMapping("/tasks/open")
    public Optional<Tasks> findByOpenTrue() {

        return tasksRepository.findByOpenTrue();

    }

    @GetMapping("/tasks/closed")
    public Optional<Tasks> findByOpenFalse() {

        return tasksRepository.findByOpenFalse();

    }

    @PutMapping("/task/{id}")
    public Tasks closeTaskById(@PathVariable("id") Long id) {

        return tasksService.closeTaskById(id);

    }

    @PutMapping("/task/{id}")
    public  Tasks openTaskById(@PathVariable("id") Long id) {

        return tasksService.openTaskById(id);

    }

    @PostMapping("/task/create")
    public Tasks createTask(@RequestBody @NonNull Tasks task) {

        String title = task.getTitle();
        String description = task.getDescription();

        return tasksService.createTask(title, description);

    }

}
