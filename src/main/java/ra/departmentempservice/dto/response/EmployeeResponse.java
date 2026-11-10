package ra.departmentempservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.departmentempservice.entity.Department;
import ra.departmentempservice.entity.Status;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmployeeResponse {
    private Long empId;
    private String code;
    private String fullName;
    private String email;
    private String phone;
    private Double salary;
    private Status status;
    private Department department;
}
