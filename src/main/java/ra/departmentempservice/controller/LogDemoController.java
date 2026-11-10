package ra.departmentempservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ra.departmentempservice.dto.response.ApiResponse;
import ra.departmentempservice.dto.response.DepartmentResponse;
import ra.departmentempservice.service.DepartmentService;

import java.util.List;

@Controller
@RequestMapping("/api/v1/demo-log")
@RequiredArgsConstructor
public class LogDemoController {
    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getDepartments() {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy danh sách phòng ban thành công",
                departmentService.getListDepartments(),
                null,
                HttpStatus.OK
        ), HttpStatus.OK);
    }
}
