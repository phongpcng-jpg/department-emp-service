package ra.departmentempservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.departmentempservice.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department,Long> {
    boolean existsByDepartName(String departName);
}
