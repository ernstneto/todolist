package br.com.ernstneto.todolist.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserRepository userRepository;
    
    @PostMapping("/")
    public ResponseEntity<Object> create(@RequestBody UserModel userModel){
        var userExists = this.userRepository.findByUsername(userModel.getUsername());
        if(userExists != null) {
            //System.out.println("\n\nERROR: User already exists.\n\n");
            //throw new RuntimeException("User already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
        }
        //var userCreated = this.userRepository.save(userModel);
        //return userCreated;
        var passwordHasred = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(passwordHasred);
        return ResponseEntity.status(HttpStatus.OK).body(this.userRepository.save(userModel));
    }
    @GetMapping("/")
    public ResponseEntity<Object> get(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        var user = this.userRepository.findById((UUID)idUser).orElse(null);
        if(user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
