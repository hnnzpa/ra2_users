package com.ra2.users.ra2_users.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ra2.users.ra2_users.models.User;
import com.ra2.users.ra2_users.respositories.UserRepository;
import com.ra2.users.ra2_users.loggins.CustomLoggin;

@Service
public class UserService {

    private static final Path PATH_DIR = Paths.get("private");
    private static final Path PATH_CSV = Paths.get("csv");
    
    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CustomLoggin customLoggin;

    public List<User> getAllUsers(){
        customLoggin.logInfo("UserService", "getAllUsers", "Accedint a getAllUsers");
        return userRepository.findAll();
    }

    public User getUserById(Long userId){
        customLoggin.logInfo("UserService", "getUserById", "Accedint a getUserById amb id: " + userId);
        
        try{
            User user = userRepository.findById(userId);
            return user;
        }catch(Exception e){
            customLoggin.logError("UserService", "getUserById", "Usuari amb id " + userId + " no trobat", e);
            return null; 
        }
    }

    public int createUser(User user){
        customLoggin.logInfo("UserService", "createUser", 
            "Accedint a createUser amb dades: " + user.getNom() + ", " + user.getEmail());
        
        try{
            int result = userRepository.save(user);
            customLoggin.logInfo("UserService", "createUser", "Usuari creat correctament");
            return result;
        }catch(Exception e){
            customLoggin.logError("UserService", "createUser", "Error creant usuari", e);
            return 0;
        }
    }

    public int updateUser(Long userId, User usuari) {
        customLoggin.logInfo("UserService", "updateUser", "Accedint a updateUser amb id: " + userId);
        
        try{
            int result = userRepository.update(userId, usuari);
            if(result > 0){
                customLoggin.logInfo("UserService", "updateUser", 
                    "Usuari amb id " + userId + " actualitzat correctament");
            }else{
                customLoggin.logError("UserService", "updateUser", 
                    "Usuari amb id " + userId + " no trobat per actualitzar", null);
            }
            return result;
        }catch(Exception e){
            customLoggin.logError("UserService", "updateUser", "Error actualitzant usuari", e);
            return 0;
        }
    }

    public int updateUserName(Long userId, String nom) {
        customLoggin.logInfo("UserService", "updateUserName", 
            "Accedint a updateUserName amb id: " + userId + " i nou nom: " + nom);
        
        try{
            int result = userRepository.updateName(userId, nom);
            if(result > 0){
                customLoggin.logInfo("UserService", "updateUserName", 
                    "Nom de l'usuari amb id " + userId + " actualitzat a: " + nom);
            }else{
                customLoggin.logError("UserService", "updateUserName", 
                    "Usuari amb id " + userId + " no trobat", null);
            }
            return result;
        }catch(Exception e){
            customLoggin.logError("UserService", "updateUserName", 
                "Error actualitzant nom d'usuari", e);
            return 0;
        }
    }

    public int deleteUser(Long userId) {
        customLoggin.logInfo("UserService", "deleteUser", "Accedint a deleteUser amb id: " + userId);
        
        try{
            int result = userRepository.delete(userId);
            if(result > 0){
                customLoggin.logInfo("UserService", "deleteUser", 
                    "Usuari amb id " + userId + " esborrat correctament");
            }else{
                customLoggin.logError("UserService", "deleteUser", 
                    "Usuari amb id " + userId + " no trobat per esborrar", null);
            }
            return result;
        }catch(Exception e){
            customLoggin.logError("UserService", "deleteUser", "Error esborrant usuari", e);
            return 0;
        }
    }

    public String[] postImage(Long userId, MultipartFile imatge) throws Exception{
        customLoggin.logInfo("UserService", "postImage", 
            "Accedint a uploadImage per usuari id: " + userId + " amb fitxer: " + imatge.getOriginalFilename());

        User user = null; 
        try{
            user = userRepository.findById(userId);
        }catch(Exception e){
            customLoggin.logError("UserService", "postImage", 
                "Error carregant imatge: L'usuari no existeix", e);
            return new String[] {"e", "L'usuari no hi és"};
        }

        String arxiu = imatge.getOriginalFilename();

        try{
            if (Files.notExists(PATH_DIR)){
                Files.createDirectories(PATH_DIR);
            }

            Path destino = PATH_DIR.resolve(arxiu);
            Files.copy(imatge.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            user.setImage_path(destino.toString());
            int update = userRepository.updateImage(userId, destino.toString());
            
            if (update == 0){
                customLoggin.logError("UserService", "postImage", 
                    "No s'ha pogut actualitzar l'imatge a la BD", null);
                return new String[] {"e", "No s'ha pogut actualitzar l'imatge"};
            }
            
            customLoggin.logInfo("UserService", "postImage", 
                "Imatge carregada correctament per usuari id: " + userId);
            return new String[] {"ok", destino.toString()};
            
        }catch(Exception e){
            customLoggin.logError("UserService", "postImage", 
                "Error carregant imatge per usuari id: " + userId, e);
            return new String[] {"e", "Error carregant imatge"};
        }
    }

    public String[] postCSV(MultipartFile csvFile){
        customLoggin.logInfo("UserService", "postCSV", 
            "Iniciant càrrega massiva de fitxer CSV: " + csvFile.getOriginalFilename());
        
        String linea;
        String arxiu = csvFile.getOriginalFilename();
        int processedCount = 0;
        int errorCount = 0;
        
        // guardar fitxer
        if (Files.notExists(PATH_CSV)){
            try {
                Files.createDirectories(PATH_CSV);
            } catch (IOException e) {
                customLoggin.logError("UserService", "postCSV", 
                    "No s'ha pogut crear el directori", e);
                return new String[] {"e", "No s'ha pogut crear el directori per guardar el fitxer"};
            }
        }

        Path destino = PATH_CSV.resolve(arxiu);
        try {
            Files.copy(csvFile.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            customLoggin.logError("UserService", "postCSV", 
                "No s'ha pogut guardar el fitxer", e);
            return new String[] {"e", "No s'ha pogut guardar el fitxer en el directori"};
        }
        
        // llegir i processar
        try (BufferedReader br = new BufferedReader(new FileReader(destino.toFile()))) {
            linea = br.readLine(); // capçalera

            while ((linea = br.readLine()) != null) {
                String[] info = linea.split(",");
                
                try{
                    userRepository.save(new User(null, info[0], info[1], info[2], info[3], null, null, null, null));
                    processedCount++;
                }catch(Exception e){
                    errorCount++;
                    customLoggin.logError("UserService", "postCSV", 
                        "Error processant registre: " + linea, e);
                }
            }

        } catch (IOException e) {
            customLoggin.logError("UserService", "postCSV", 
                "No s'ha pogut llegir el fitxer", e);
            return new String[] {"e", "No s'ha pogut lleguir el fitxer"};
        }
 
        customLoggin.logInfo("UserService", "postCSV", 
            "Càrrega massiva completada. " + processedCount + " registres processats correctament, " + errorCount + " errors");
        return new String[] {"ok", destino.toString()};
    }

    public String[] postJson(MultipartFile jsonFile) throws Exception {
        customLoggin.logInfo("UserService", "postJson", 
            "Iniciant càrrega massiva de fitxer JSON: " + jsonFile.getOriginalFilename());
        
        int processedCount = 0;
        int errorCount = 0;
        
        try{
            JsonNode arrel = objectMapper.readTree(jsonFile.getInputStream());
            JsonNode data = arrel.path("data");
            int count = data.path("count").asInt();
            String control = data.path("control").asText();
            JsonNode users = data.path("users");

            for (JsonNode user : users) {
                String nom = user.path("name").asText();
                String description = user.path("description").asText();
                String email = user.path("email").asText();
                String password = user.path("password").asText();
                
                User usuari = new User(null, nom, description, email, password, null, null, null, null);
                try{
                    userRepository.save(usuari);
                    processedCount++;
                }catch(Exception e){
                    errorCount++;
                    customLoggin.logError("UserService", "postJson", 
                        "Error processant registre usuari: " + nom, e);
                }
            }
            
            customLoggin.logInfo("UserService", "postJson", 
                "Càrrega massiva completada. " + processedCount + " registres processats correctament, " + errorCount + " errors");
            return new String[] {"ok", "Usuaris desats correctament"};
            
        }catch(Exception e){
            customLoggin.logError("UserService", "postJson", 
                "Error general processant JSON", e);
            return new String[] {"e", "Error processant fitxer JSON"};
        }
    }
}