package br.com.yandv.todolist.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.yandv.todolist.model.TaskModel;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {

    TaskModel findByTitle(String title);

    TaskModel findByUserId(UUID userId);
    
}
