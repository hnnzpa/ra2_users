package com.ra2.users.ra2_users.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ra2.users.ra2_users.models.User;
import com.ra2.users.ra2_users.respositories.UserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody User usuari) {
        int result = userRepository.save(usuari);
        if (result > 0) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuari inserit correctament.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear l'usuari.");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<User> getUserById(@PathVariable("user_id") Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return ResponseEntity.ok(user.orElse(null));
    }

    @PutMapping("/user/update/{user_id}")
    public ResponseEntity<String> updateUser(@PathVariable("user_id") Long userId, @RequestBody User usuari) {
        int result = userRepository.update(userId, usuari);
        if (result > 0) {
            return ResponseEntity.ok("Usuari modificat correctament.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuari no trobat.");
        }
    }

    @PatchMapping("/user/update/{user_id}/name")
    public ResponseEntity<User> updateUserName(@PathVariable("user_id") Long userId,
                                               @RequestParam("nom") String nom) {
        int result = userRepository.updateName(userId, nom);
        if (result > 0) {
            Optional<User> updatedUser = userRepository.findById(userId);
            return ResponseEntity.ok(updatedUser.orElse(null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/user/delete/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable("user_id") Long userId) {
        int result = userRepository.delete(userId);
        if (result > 0) {
            return ResponseEntity.ok("Usuari eliminat correctament.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuari no trobat.");
        }
    }
}
