package az.aladdin.blogplatform.services.abstraction;

import az.aladdin.blogplatform.dao.entities.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface LoginHistoryService {

    void saveLoginHistory(User user, HttpServletRequest request);

    List<String> getDevicesInfo(String userId);
}
