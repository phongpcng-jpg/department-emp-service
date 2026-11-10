package ra.departmentempservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.departmentempservice.entity.Employee;

@Repository
public interface EmployeRepository extends JpaRepository<Employee,Long> {
}
