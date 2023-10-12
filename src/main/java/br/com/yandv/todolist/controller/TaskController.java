package br.com.yandv.todolist.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;

import br.com.yandv.todolist.model.TaskModel;
import br.com.yandv.todolist.repository.ITaskRepository;
import br.com.yandv.todolist.utils.JsonBuilder;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @GetMapping("/")
    public ResponseEntity<String> getTasks(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int itemsPerPage) {
        Page<TaskModel> tasksPage = this.taskRepository.findAll(PageRequest.of(page, Math.min(itemsPerPage, 50)));

        JsonArray jsonArray = new JsonArray();

        for (TaskModel taskModel : tasksPage.getContent()) {
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

    @PostMapping("/")
    public ResponseEntity<String> createTask(@RequestBody TaskModel taskModel) {
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

        TaskModel createdTaskModel = this.taskRepository.save(taskModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(new JsonBuilder()
                .addProperty("uniqueId", createdTaskModel.getUniqueId().toString())
                .addProperty("title", createdTaskModel.getTitle())
                .addProperty("description", createdTaskModel.getDescription())
                .addProperty("createdAt", createdTaskModel.getCreatedAt().toString())
                .toString());
    }

}
