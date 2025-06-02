package s.ma.project.controller;

import org.springframework.web.bind.annotation.*;
import s.ma.project.model.User;
import s.ma.project.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        userService.register(user);
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        boolean success = userService.login(user.getUsername(), user.getPassword());
        return success ? "Login successful!" : "Invalid username or password!";
    }
}
