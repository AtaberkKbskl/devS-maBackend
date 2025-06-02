package s.ma.project.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import s.ma.project.model.User;
import s.ma.project.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Manuel constructor: Spring otomatik olarak UserRepository ve PasswordEncoder bean’lerini enjekte edecek
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Kayıt metodu: şifreyi encode edip veritabanına kaydeder
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Giriş metodu: kullanıcı adını ve ham şifreyi alır, veritabanındaki hash’lenmiş şifreyle karşılaştırır
    public boolean login(String username, String rawPassword) {
        User foundUser = userRepository.findByUsername(username);
        if (foundUser == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, foundUser.getPassword());
    }

    // İleride gerekirse kullanıcıyı kullanıcı adına göre döndüren yardımcı metod
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
