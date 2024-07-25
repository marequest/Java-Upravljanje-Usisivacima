package rs.raf.demo.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.raf.demo.model.User;
import rs.raf.demo.model.Usisivac;

import java.util.Date;
import java.util.List;

@Repository
public interface UsisivacRepository extends JpaRepository<Usisivac, Long> {
    List<Usisivac> findByAddedByAndActiveTrue(User user);

    @Query("SELECT v FROM Usisivac v WHERE " +
            "(:name IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:status IS NULL OR v.status IN :status) AND " +
            "(:dateFrom IS NULL OR v.createdDate >= :dateFrom) AND " +
            "(:dateTo IS NULL OR v.createdDate <= :dateTo) AND " +
            "(:addedBy IS NULL OR v.addedBy = :addedBy)")
    List<Usisivac> findByCriteria(@Param("name") String name,
                                  @Param("status") List<Usisivac.Status> status,
                                  @Param("dateFrom") Date dateFrom,
                                  @Param("dateTo") Date dateTo,
                                  @Param("addedBy") User addedBy);

}
