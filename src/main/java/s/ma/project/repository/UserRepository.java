package s.ma.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import s.ma.project.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Tek bir User döndürmek yerine liste döndürelim:
    List<User> findAllByUsername(String username);
}