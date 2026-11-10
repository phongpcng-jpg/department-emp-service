package ra.departmentempservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.departmentempservice.entity.Status;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DepartmentResponse {
    private Long departId;
    private String departName;
    private String description;
    private Status status;
}
