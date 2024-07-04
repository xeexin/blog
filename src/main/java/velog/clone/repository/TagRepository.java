package velog.clone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import velog.clone.domain.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

}
