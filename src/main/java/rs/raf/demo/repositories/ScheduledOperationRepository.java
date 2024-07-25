package rs.raf.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.raf.demo.model.ScheduleOperation;

import java.util.Date;
import java.util.List;

public interface ScheduledOperationRepository extends JpaRepository<ScheduleOperation, Long> {
    List<ScheduleOperation> findByScheduledTimeBefore(Date now);
}
