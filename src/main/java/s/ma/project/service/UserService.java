package s.ma.project.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import s.ma.project.model.User;
import s.ma.project.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Manuel constructor: Spring otomatik olarak UserRepository ve PasswordEncoder bean'lerini enjekte edecek
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Kayıt metodu:
     * - Önce aynı username ile kayıtlı bir kullanıcı var mı diye kontrol eder.
     * - Eğer yoksa, gelen kullanıcının şifresini encode edip kaydeder.
     * - Aynı username varsa IllegalStateException fırlatır.
     */
    public User register(User user) {
        // 1) Aynı username'e sahip bir kullanıcı var mı diye kontrol edelim
        List<User> existing = userRepository.findAllByUsername(user.getUsername());
        if (!existing.isEmpty()) {
            // Zaten kayıtlı: aynı kullanıcı adına birden çok kayıt oluşmasını engelle
            throw new IllegalStateException("Bu kullanıcı adı zaten kullanılıyor: " + user.getUsername());
        }

        // 2) Şifreyi encode edip kaydedelim
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Giriş (login) metodu:
     * - Kullanıcı adına göre veritabanından kullanıcıları liste olarak çek (findAllByUsername).
     * - Eğer hiç kayıt yoksa false döndür.
     * - Eğer birden fazla kayıt dönmüşse false döndür (veya istenirse başka bir mantık koyulabilir).
     * - Tek bir kullanıcı döndüyse, rawPassword'i hashlenmiş şifre ile karşılaştır.
     */
    public boolean login(String username, String rawPassword) {
        List<User> users = userRepository.findAllByUsername(username);

        if (users.isEmpty()) {
            // Kullanıcı bulunamadı
            return false;
        }

        if (users.size() > 1) {
            // Aynı username ile birden fazla kayıt var: güvenlik veya mantık açısından girişe izin verme
            return false;
        }

        // 1 kullanıcı varsa:
        User foundUser = users.get(0);

        // Şifre eşleşiyor mu?
        return passwordEncoder.matches(rawPassword, foundUser.getPassword());
    }

    /**
     * Kullanıcıyı username'e göre bulup döndüren yardımcı metod:
     * - Eğer 0 sonuç varsa Optional.empty() döner.
     * - Eğer 1 sonuç varsa Optional<User> içinde döner.
     * - Eğer 2+ sonuç varsa Optional.empty() döner (hatayı üst katmana bırakabilirsiniz).
     */
    public Optional<User> findByUsername(String username) {
        List<User> users = userRepository.findAllByUsername(username);

        if (users.isEmpty()) {
            return Optional.empty();
        } else if (users.size() > 1) {
            // Birden fazla sonuç varsa yanlışlık olduğunu kabul edip boş dönebiliriz
            return Optional.empty();
        } else {
            // exactly one
            return Optional.of(users.get(0));
        }
    }
}
