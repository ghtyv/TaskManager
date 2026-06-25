package service;

import model.Tasks;
import org.springframework.stereotype.Service;
import repository.TasksRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TasksService {

    private final TasksRepository tasksRepository;

    public TasksService(TasksRepository tasksRepository) {

        this.tasksRepository = tasksRepository;

    }


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



}
