package rs.raf.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class ScheduleOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usisivacId;

    @Enumerated(EnumType.STRING)
    private Operation operation;

    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduledTime;

    public enum Operation {
        START, STOP, DISCHARGE
    }

}
