package az.aladdin.blogplatform.configuration.mappers;

import az.aladdin.blogplatform.dao.entities.LoginHistory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserHistoryMapper {

    public String response(LoginHistory loginHistory) {
        return historyInfo(loginHistory) + " " + date(loginHistory.getLoginTime());
    }

    public List<String> response(List<LoginHistory> loginHistories) {
        return loginHistories.stream().map(this::response).toList();
    }

    public String historyInfo(LoginHistory loginHistory) {
        String info = "";
        if (loginHistory.getDevice() != null) {
            info = loginHistory.getDevice();
        }
        if (loginHistory.getBrowser() != null) {
            info = " " + loginHistory.getBrowser();
        }
        if (loginHistory.getCity() != null) {
            info = " " + loginHistory.getCity();
        }
        if (loginHistory.getCountry() != null) {
            info = " " + loginHistory.getCountry();
        }
        return info;
    }

    public String date(LocalDateTime localDateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(localDateTime, now);

        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "1 m. ago";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " m. ago";
        } else {
            long hours = seconds / 3600;
            return hours + " h. ago";
        }
    }

}

