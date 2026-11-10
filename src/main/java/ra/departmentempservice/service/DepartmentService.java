package ra.departmentempservice.service;


import ra.departmentempservice.dto.request.DepartmentRequest;
import ra.departmentempservice.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    List<DepartmentResponse> getListDepartments();
    DepartmentResponse insertDepartment(DepartmentRequest departmentRequest);
}
