package br.com.yandv.todolist.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.yandv.todolist.model.UserModel;

public interface IUserRepository extends JpaRepository<UserModel, UUID> {
    
    UserModel findByUserName(String userName);

}