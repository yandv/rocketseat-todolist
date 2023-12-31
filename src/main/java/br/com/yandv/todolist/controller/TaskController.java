package br.com.yandv.todolist.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;

import br.com.yandv.todolist.model.TaskModel;
import br.com.yandv.todolist.model.UserModel;
import br.com.yandv.todolist.repository.ITaskRepository;
import br.com.yandv.todolist.repository.IUserRepository;
import br.com.yandv.todolist.utils.JsonBuilder;
import br.com.yandv.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @Autowired
    private IUserRepository userRepository;

    @GetMapping("/")
    public ResponseEntity<String> getTasks(HttpServletRequest request) {
        List<TaskModel> tasksPage = this.taskRepository.findByUserId((UUID) request.getAttribute("userId"));

        JsonArray jsonArray = new JsonArray();

        for (TaskModel taskModel : tasksPage) {
            JsonBuilder jsonBuilder = new JsonBuilder();

            jsonBuilder.addProperty("uniqueId", taskModel.getUniqueId().toString());
            jsonBuilder.addProperty("title", taskModel.getTitle());
            jsonBuilder.addProperty("description", taskModel.getDescription());

            if (taskModel.getUserId() != null)
                jsonBuilder.addProperty("userId", taskModel.getUserId().toString());

            if (taskModel.getStartedAt() != null)
                jsonBuilder.addProperty("startedAt", taskModel.getStartedAt().toString());

            if (taskModel.getFinishedAt() != null)
                jsonBuilder.addProperty("finishedAt", taskModel.getFinishedAt().toString());

            jsonArray.add(jsonBuilder.create());
        }

        return ResponseEntity.status(HttpStatus.OK).body(jsonArray.toString());
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getTaskById(@PathVariable UUID id) {
        TaskModel taskModel = this.taskRepository.findById(id).orElse(null);

        if (taskModel == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new JsonBuilder()
                    .addProperty("errorMessage", "The task with id " + id.toString() + " was not found.")
                    .toString());

        JsonBuilder jsonBuilder = new JsonBuilder();

        jsonBuilder.addProperty("uniqueId", taskModel.getUniqueId().toString());
        jsonBuilder.addProperty("title", taskModel.getTitle());
        jsonBuilder.addProperty("description", taskModel.getDescription());

        if (taskModel.getUserId() != null)
            jsonBuilder.addProperty("userId", taskModel.getUserId().toString());

        if (taskModel.getStartedAt() != null)
            jsonBuilder.addProperty("startedAt", taskModel.getStartedAt().toString());

        if (taskModel.getFinishedAt() != null)
            jsonBuilder.addProperty("finishedAt", taskModel.getFinishedAt().toString());

        return ResponseEntity.status(HttpStatus.OK).body(jsonBuilder.toString());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> putTaskById(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {
        TaskModel task = this.taskRepository.findById(id).orElse(null);

        if(!task.getUserId().equals((UUID) request.getAttribute("userId")))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonBuilder()
                    .addProperty("errorMessage", "You don't have permission to update this task.")
                    .toString());

        Utils.copyNonNullProperties(taskModel, task);

        task = this.taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.OK).body(new JsonBuilder()
                .addProperty("uniqueId", task.getUniqueId().toString())
                .addProperty("title", task.getTitle())
                .addProperty("description", task.getDescription())
                .addProperty("startedAt", task.getStartedAt().toString())
                .addProperty("finishedAt", task.getFinishedAt().toString())
                .addProperty("userId", task.getUserId().toString())
                .addProperty("createdAt", task.getCreatedAt().toString())
                .toString());
    }

    @PostMapping("/")
    public ResponseEntity<String> createTask(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        if (taskModel.getTitle() == null || taskModel.getTitle().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonBuilder()
                    .addProperty("errorMessage", "The title is required.")
                    .toString());
        }

        if (taskModel.getDescription() == null || taskModel.getDescription().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonBuilder()
                    .addProperty("errorMessage", "The description is required.")
                    .toString());
        }

        taskModel.setUserId((UUID) request.getAttribute("userId"));

        LocalDateTime currentTime = LocalDateTime.now();

        if (currentTime.isAfter(taskModel.getStartedAt()) || currentTime.isAfter(taskModel.getFinishedAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonBuilder()
                    .addProperty("errorMessage", "The startedAt must be after the current time.")
                    .toString());
        }

        if (taskModel.getStartedAt().isAfter(taskModel.getFinishedAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonBuilder()
                    .addProperty("errorMessage", "The startedAt must be before the finishedAt.")
                    .toString());
        }

        TaskModel createdTaskModel = this.taskRepository.save(taskModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(new JsonBuilder()
                .addProperty("uniqueId", createdTaskModel.getUniqueId().toString())
                .addProperty("title", createdTaskModel.getTitle())
                .addProperty("description", createdTaskModel.getDescription())
                .addProperty("startedAt", createdTaskModel.getStartedAt().toString())
                .addProperty("finishedAt", createdTaskModel.getFinishedAt().toString())
                .addProperty("userId", createdTaskModel.getUserId().toString())
                .addProperty("createdAt", createdTaskModel.getCreatedAt().toString())
                .toString());
    }

}
