package ra.departmentempservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.departmentempservice.dto.request.DepartmentRequest;
import ra.departmentempservice.dto.response.ApiResponse;
import ra.departmentempservice.dto.response.DepartmentResponse;
import ra.departmentempservice.service.DepartmentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAllDepartments() {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy danh sách phòng ban thành công",
                departmentService.getListDepartments(),
                null,
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(@Valid @RequestBody DepartmentRequest departmentRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Thêm mới phòng ban " + departmentRequest.getDepartName() + " thành công",
                departmentService.insertDepartment(departmentRequest),
                null,
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }
}
