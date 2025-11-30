package com.ra2.users.ra2_users.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ra2.users.ra2_users.models.User;
import com.ra2.users.ra2_users.services.UserService;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService; 

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody User usuari) {
        int result = userService.createUser(usuari);
        if (result > 0) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuari inserit correctament.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear l'usuari.");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User u = userService.getUserById(userId); 
        if (u != null){
            return ResponseEntity.status(HttpStatus.OK).body(u);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        
    }

    @PutMapping("/user/update/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestBody User usuari) {
        int result = userService.updateUser(userId, usuari);
        if (result > 0) {
            return ResponseEntity.ok("Usuari modificat correctament.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuari no trobat.");
        }
    }

    @PatchMapping("/user/update/{userId}/name")
    public ResponseEntity<User> updateUserName(@PathVariable Long userId,
                                               @RequestParam String nom) {
        int result = userService.updateUserName(userId, nom);
        if (result > 0) {
            User updatedUser = userService.getUserById(userId);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/user/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        int result = userService.deleteUser(userId);
        if (result > 0) {
            return ResponseEntity.ok("Usuari eliminat correctament.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuari no trobat.");
        }
    }

    // fixers imatges
    @PostMapping("/user/{userId}/imatge")
    public ResponseEntity<String> postImage(@PathVariable Long userId, @RequestParam("imageFile") MultipartFile imageFile) throws Exception{
        if (userService.getUserById(userId) != null){
            String[] resposta = userService.postImage(userId, imageFile);

            if (resposta[0].equals("ok")){
                return ResponseEntity.ok(resposta[1]);

            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta[1]);
            }
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuari no trobat");
        }
    }

    // fitxers csv
    @PostMapping("/users/upload-csv")
    public ResponseEntity<String> postCSV(@RequestParam MultipartFile csvFile) throws Exception{
        String[] resposta = userService.postCSV(csvFile);
        if (resposta[0].equals("ok")){
            return ResponseEntity.ok("S'han afegit " + resposta[1]);
        }else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta[1]);
        }
    }
    
    //fitres json 
    @PostMapping("/users/upload-json")
    public ResponseEntity<String> postJson(@RequestParam MultipartFile jsonFile) throws Exception{
        String[] resposta = userService.postJson(jsonFile);
        if (resposta[0].equals("ok")){
            return ResponseEntity.ok("S'han afegit " + resposta[1]);
        }else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta[1]);
        }
    }


}
