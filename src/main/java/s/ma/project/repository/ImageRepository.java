package s.ma.project.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import s.ma.project.model.ImageFile;

public interface ImageRepository extends JpaRepository<ImageFile, Long> {
}