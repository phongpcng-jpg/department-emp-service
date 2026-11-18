package ra.departmentempservice.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ra.departmentempservice.dto.request.DepartmentRequest;
import ra.departmentempservice.dto.response.DepartmentResponse;
import ra.departmentempservice.entity.Department;
import ra.departmentempservice.entity.Status;
import ra.departmentempservice.exception.DepartmentNameExistException;
import ra.departmentempservice.repository.DepartmentRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @Test
    void getListDepartments_shouldReturnMappedDepartmentResponses() {
        Department first = Department.builder()
                .departId(1L)
                .departName("Human Resources")
                .description("HR department")
                .status(Status.ACTIVE)
                .build();

        Department second = Department.builder()
                .departId(2L)
                .departName("Engineering")
                .description("Engineering department")
                .status(Status.INACTIVE)
                .build();

        when(departmentRepository.findAll()).thenReturn(List.of(first, second));

        List<DepartmentResponse> result = departmentService.getListDepartments();

        assertEquals(2, result.size());

        assertAll(
                () -> assertEquals(1L, result.get(0).getDepartId()),
                () -> assertEquals("Human Resources", result.get(0).getDepartName()),
                () -> assertEquals("HR department", result.get(0).getDescription()),
                () -> assertEquals(Status.ACTIVE, result.get(0).getStatus())
        );

        assertAll(
                () -> assertEquals(2L, result.get(1).getDepartId()),
                () -> assertEquals("Engineering", result.get(1).getDepartName()),
                () -> assertEquals("Engineering department", result.get(1).getDescription()),
                () -> assertEquals(Status.INACTIVE, result.get(1).getStatus())
        );

        verify(departmentRepository).findAll();
        verifyNoMoreInteractions(departmentRepository);
    }

    @Test
    void getListDepartments_whenRepositoryIsEmpty_shouldReturnEmptyList() {
        when(departmentRepository.findAll()).thenReturn(List.of());

        List<DepartmentResponse> result = departmentService.getListDepartments();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(departmentRepository).findAll();
        verifyNoMoreInteractions(departmentRepository);
    }

    @Test
    void insertDepartment_whenNameDoesNotExist_shouldSaveAndReturnMappedResponse() {
        DepartmentRequest request = DepartmentRequest.builder()
                .departName("Finance")
                .description("Finance department")
                .status(Status.ACTIVE)
                .build();

        when(departmentRepository.existsByDepartName("Finance")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> {
            Department department = invocation.getArgument(0);
            department.setDepartId(10L);
            return department;
        });

        DepartmentResponse result = departmentService.insertDepartment(request);

        assertAll(
                () -> assertEquals(10L, result.getDepartId()),
                () -> assertEquals("Finance", result.getDepartName()),
                () -> assertEquals("Finance department", result.getDescription()),
                () -> assertEquals(Status.ACTIVE, result.getStatus())
        );

        ArgumentCaptor<Department> departmentCaptor = ArgumentCaptor.forClass(Department.class);
        verify(departmentRepository).existsByDepartName("Finance");
        verify(departmentRepository).save(departmentCaptor.capture());

        Department savedDepartment = departmentCaptor.getValue();
        assertAll(
                () -> assertNull(savedDepartment.getDepartId()),
                () -> assertEquals("Finance", savedDepartment.getDepartName()),
                () -> assertEquals("Finance department", savedDepartment.getDescription()),
                () -> assertEquals(Status.ACTIVE, savedDepartment.getStatus())
        );

        verifyNoMoreInteractions(departmentRepository);
    }

    @Test
    void insertDepartment_whenNameAlreadyExists_shouldThrowExceptionAndNotSave() {
        DepartmentRequest request = DepartmentRequest.builder()
                .departName("Finance")
                .description("Finance department")
                .status(Status.ACTIVE)
                .build();

        when(departmentRepository.existsByDepartName("Finance")).thenReturn(true);

        DepartmentNameExistException exception = assertThrows(
                DepartmentNameExistException.class,
                () -> departmentService.insertDepartment(request)
        );

        assertEquals("Tên phòng ban đã tồn tại", exception.getMessage());
        verify(departmentRepository).existsByDepartName("Finance");
        verify(departmentRepository, never()).save(any(Department.class));
        verifyNoMoreInteractions(departmentRepository);
    }

    @Test
    void insertDepartment_whenRepositorySaveFails_shouldPropagateException() {
        DepartmentRequest request = DepartmentRequest.builder()
                .departName("Operations")
                .description("Operations department")
                .status(Status.ACTIVE)
                .build();

        RuntimeException repositoryException = new RuntimeException("Database is unavailable");

        when(departmentRepository.existsByDepartName("Operations")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenThrow(repositoryException);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> departmentService.insertDepartment(request)
        );

        assertSame(repositoryException, thrown);
        verify(departmentRepository).existsByDepartName("Operations");
        verify(departmentRepository).save(any(Department.class));
        verifyNoMoreInteractions(departmentRepository);
    }
}
