package rs.raf.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

//    @Column(unique = true, nullable = false)
    private String username;

//    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "addedBy", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Usisivac> usisivaci;

    @Column
    private String lastName;

    @Column
    private String firstName;

    @Column
    private String permissions;

}
