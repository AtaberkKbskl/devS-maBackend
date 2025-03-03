package s.ma.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import s.ma.project.model.User;
import s.ma.project.repository.UserRepository;

@CrossOrigin(origins = "http://localhost:5173")  // CORS Ayarları
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        // Şifreyi şifrele
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            userRepository.save(user);  // Kullanıcıyı veritabanına kaydet
        } catch (Exception e) {
            return "Error during registration: " + e.getMessage();  // Hata durumunda geri dönen mesaj
        }
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        // Kullanıcıyı veritabanında ara
        User foundUser = userRepository.findByUsername(user.getUsername());
        if (foundUser != null && passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
            return "Login successful!";
        } else {
            return "Invalid username or password!";
        }
    }
}
