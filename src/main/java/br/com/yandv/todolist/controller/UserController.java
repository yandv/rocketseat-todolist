
package br.com.yandv.todolist.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;

import br.com.yandv.todolist.model.UserModel;
import br.com.yandv.todolist.repository.IUserRepository;
import br.com.yandv.todolist.utils.JsonBuilder;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")
    public ResponseEntity<String> createUser(@RequestBody UserModel userModel) {
        if (this.userRepository.findByUserName(userModel.getUserName()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new JsonBuilder()
                            .addProperty("errorMessage", "The user " + userModel.getUserName() + " already exists.")
                            .toString());
        }

        UserModel userCreated = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(new JsonBuilder()
                .addProperty("uniqueId", userCreated.getUniqueId().toString())
                .addProperty("userName", userCreated.getUserName())
                .toString());
    }

    @GetMapping("/")
    public ResponseEntity<String> getUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int itemsPerPage) {
        Page<UserModel> usersPage = this.userRepository.findAll(PageRequest.of(page, Math.min(itemsPerPage, 50)));

        JsonArray jsonArray = new JsonArray();

        for (UserModel userModel : usersPage.getContent()) {
            JsonBuilder jsonBuilder = new JsonBuilder();

            jsonBuilder.addProperty("uniqueId", userModel.getUniqueId().toString());
            jsonBuilder.addProperty("userName", userModel.getUserName());

            jsonArray.add(jsonBuilder.create());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(jsonArray
                .toString());
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getUserById(@PathVariable UUID id) {
        UserModel userModel = this.userRepository.findById(id).orElse(null);

        if (userModel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new JsonBuilder()
                            .addProperty("errorMessage", "The user with id " + id.toString() + " was not found.")
                            .toString());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new JsonBuilder()
                .addProperty("uniqueId", userModel.getUniqueId().toString())
                .addProperty("userName", userModel.getUserName())
                .toString());
    }
}
