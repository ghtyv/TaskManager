package repository;

import model.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Long> {

    Optional<Tasks> findByOpenFalse();
    Optional<Tasks> findByOpenTrue();

}

