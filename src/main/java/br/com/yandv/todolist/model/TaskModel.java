package br.com.yandv.todolist.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tasks")
public class TaskModel {
    
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID uniqueId;

    @Column(length = 64)
    private String title;
    private String description;

    private UUID userId;

    @Column()
    private LocalDateTime startedAt = LocalDateTime.now();
    private LocalDateTime finishedAt = LocalDateTime.now();

    @CreationTimestamp
    private LocalDateTime createdAt;

}
