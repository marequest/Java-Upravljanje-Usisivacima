package rs.raf.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Usisivac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String name;

    @ManyToOne
    @JoinColumn(name = "added_by")
    @JsonBackReference
    private User addedBy;

    private Boolean active;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private int cycleCount = 0;

    @OneToMany(mappedBy = "user")
    private List<ErrorMessage> errorMessages;

    public enum Status {
        ON, OFF, DISCHARGING, IN_PROGRESS
    }
}
