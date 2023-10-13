package br.com.yandv.todolist.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.yandv.todolist.model.TaskModel;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {

    List<TaskModel> findByTitle(String title);

    List<TaskModel> findByUserId(UUID userId);
    
}
