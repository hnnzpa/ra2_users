package com.ra2.users.ra2_users.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ra2.users.ra2_users.models.User;
import com.ra2.users.ra2_users.respositories.UserRepository;

@Service
public class UserService {

    private static final Path PATH_DIR = Paths.get("private");
    private static final Path PATH_CSV = Paths.get("csv");
    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(Long userId){
        try{
            return userRepository.findById(userId);
        }catch(Exception e){
            return null; 
        }
    }

    public int createUser(User user){
        return userRepository.save(user);
    }

    public int updateUser(Long userId, User usuari) {
        return userRepository.update(userId, usuari);
    }

    public int updateUserName( Long userId, String nom) {
        return userRepository.updateName(userId, nom);
    }

    public int deleteUser(Long userId) {
        return userRepository.delete(userId);
    }

    public String[] postImage(Long userId, MultipartFile imatge) throws Exception{

        User user = null; 
        try{
            user = userRepository.findById(userId);
        }catch(Exception e){
            return new String[] {"e", "L'usuari no hi és"};
        }

        String arxiu = imatge.getOriginalFilename();

        if (Files.notExists(PATH_DIR)){
            Files.createDirectories(PATH_DIR);
        }

        Path destino = PATH_DIR.resolve(arxiu);
        Files.copy(imatge.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        user.setImage_path(destino.toString());
        int update = userRepository.updateImage(userId, destino.toString());
        if (update == 0){
            return new String[] {"e", "No s'ha pogut actualitzar l'imatge"};
        }
        System.out.println(user.getImage_path() + " " + update);
        return new String[] {"ok", destino.toString()}; 
    }

    public String[] postCSV(MultipartFile csvFile){
        
        String linea;
        String arxiu = csvFile.getOriginalFilename();
        //primer guardem el fitzer per poder llegir-ho
        if (Files.notExists(PATH_CSV)){
            try {
                Files.createDirectories(PATH_CSV);
            } catch (IOException e) {
                return new String[] {"e", "No s'ha pogut crear el directori per guardar el fitxer"};
            }
        }

        Path destino = PATH_CSV.resolve(arxiu);
        try {
            Files.copy(csvFile.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return new String[] {"e", "No s'ha pogut guardar el fitxer en el directori"};
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(destino.toFile()))) {
            linea = br.readLine(); // Lee la primera línea (encabezados) y la ignora

            while ((linea = br.readLine()) != null) {
                // Divide el csv por comas
                String[] info = linea.split(",");

                // Añade la linea a la base de dades
                userRepository.save(new User(null, info[0], info[1], info[2], info[3],null, null, null, null));
            }

        } catch (IOException e) {
                return new String[] {"e", "No s'ha pogut lleguir el fitxer"};
        }
 
        return new String[] {"ok", destino.toString()};
    }


    public String[] postJson(MultipartFile jsonFile) throws Exception {
        // fitxer json principi
        JsonNode arrel = objectMapper.readTree(jsonFile.getInputStream());
        //accedir al primer nivell
        JsonNode data = arrel.path("data");
        int count = data.path("count").asInt();
        String control = data.path("control").asText();
        JsonNode users = data.path("users");

        for (JsonNode user : users) {
            //Obtenir el nom de l'habilitat
            String nom = user.path("name").asText();
            String description = user.path("description").asText();
            String email = user.path("email").asText();
            String password = user.path("password").asText();
            
            //crear usuari
            User usuari = new User(null, nom, description, email, password, null, null, null, null);
            try{
                userRepository.save(usuari);
            }catch(Exception e){
                System.out.println("No s'ha pogut desar l'usuari " + nom);
            }

        }
        
        return new String[] {"ok", "Usuari desat correctament"};
        
    }
}

