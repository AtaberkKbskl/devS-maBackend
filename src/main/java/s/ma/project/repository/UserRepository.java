package s.ma.project.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import s.ma.project.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}