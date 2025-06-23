package az.aladdin.blogplatform.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceInfoDto {


    private String city;
    private String country;
    private String device;
    private String os;
    private String browser;

}
