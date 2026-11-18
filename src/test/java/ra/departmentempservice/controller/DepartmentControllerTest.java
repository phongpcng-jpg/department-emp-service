package ra.departmentempservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ra.departmentempservice.dto.request.DepartmentRequest;
import ra.departmentempservice.dto.response.DepartmentResponse;
import ra.departmentempservice.entity.Status;
import ra.departmentempservice.exception.DepartmentNameExistException;
import ra.departmentempservice.service.DepartmentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {

    private static final String BASE_URL = "/api/v1/departments";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;

    @Test
    void getAllDepartments_shouldReturnOkAndDepartmentList() throws Exception {
        DepartmentResponse department = DepartmentResponse.builder()
                .departId(1L)
                .departName("Engineering")
                .description("Engineering department")
                .status(Status.ACTIVE)
                .build();

        when(departmentService.getListDepartments()).thenReturn(List.of(department));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Lấy danh sách phòng ban thành công"))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].departId").value(1))
                .andExpect(jsonPath("$.data[0].departName").value("Engineering"))
                .andExpect(jsonPath("$.data[0].description").value("Engineering department"))
                .andExpect(jsonPath("$.data[0].status").value("ACTIVE"));

        verify(departmentService).getListDepartments();
        verifyNoMoreInteractions(departmentService);
    }

    @Test
    void getAllDepartments_whenNoDepartmentsExist_shouldReturnOkAndEmptyList() throws Exception {
        when(departmentService.getListDepartments()).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(departmentService).getListDepartments();
        verifyNoMoreInteractions(departmentService);
    }

    @Test
    void createDepartment_withValidRequest_shouldReturnCreated() throws Exception {
        DepartmentResponse response = DepartmentResponse.builder()
                .departId(10L)
                .departName("Finance")
                .description("Finance department")
                .status(Status.ACTIVE)
                .build();

        when(departmentService.insertDepartment(any(DepartmentRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "departName": "Finance",
                                  "description": "Finance department",
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Thêm mới phòng ban Finance thành công"))
                .andExpect(jsonPath("$.data.departId").value(10))
                .andExpect(jsonPath("$.data.departName").value("Finance"))
                .andExpect(jsonPath("$.data.description").value("Finance department"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        verify(departmentService).insertDepartment(any(DepartmentRequest.class));
        verifyNoMoreInteractions(departmentService);
    }

    @Test
    void createDepartment_whenNameAlreadyExists_shouldReturnBadRequest() throws Exception {
        when(departmentService.insertDepartment(any(DepartmentRequest.class)))
                .thenThrow(new DepartmentNameExistException("Tên phòng ban đã tồn tại"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "departName": "Finance",
                                  "description": "Finance department",
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Lỗi thêm mới phòng ban"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errors.error").value("Tên phòng ban đã tồn tại"))
                .andExpect(jsonPath("$.errors.timestamp").exists());

        verify(departmentService).insertDepartment(any(DepartmentRequest.class));
        verifyNoMoreInteractions(departmentService);
    }

    @Test
    void createDepartment_whenRequestFieldsAreInvalid_shouldReturnBadRequestWithFieldErrors() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "departName": " ",
                                  "description": "",
                                  "status": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Lỗi tham số truyền"))
                .andExpect(jsonPath("$.errors.error.departName")
                        .value("Không được để trống tên phòng ban"))
                .andExpect(jsonPath("$.errors.error.description")
                        .value("Không được để trống mô tả"))
                .andExpect(jsonPath("$.errors.error.status")
                        .value("Không được để trống trạng thái"))
                .andExpect(jsonPath("$.errors.timestamp").exists());

        verifyNoInteractions(departmentService);
    }

    @Test
    void createDepartment_whenRequiredFieldsAreMissing_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "departName": "Finance"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors.error.description")
                        .value("Không được để trống mô tả"))
                .andExpect(jsonPath("$.errors.error.status")
                        .value("Không được để trống trạng thái"));

        verifyNoInteractions(departmentService);
    }
}
