package az.aladdin.blogplatform.dao.repository;

import az.aladdin.blogplatform.dao.entities.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, String> {

    boolean existsByUserIdAndBrowserAndDeviceAndOs(String userId, String browser, String device, String os);

    List<LoginHistory> findByUserId(String userId);
}
