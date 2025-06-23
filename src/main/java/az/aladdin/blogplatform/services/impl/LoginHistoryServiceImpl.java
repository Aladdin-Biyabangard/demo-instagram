package az.aladdin.blogplatform.services.impl;

import az.aladdin.blogplatform.configuration.mappers.UserHistoryMapper;
import az.aladdin.blogplatform.dao.entities.LoginHistory;
import az.aladdin.blogplatform.dao.entities.User;
import az.aladdin.blogplatform.dao.repository.LoginHistoryRepository;
import az.aladdin.blogplatform.model.dto.response.DeviceInfoDto;
import az.aladdin.blogplatform.services.abstraction.LoginHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Service;
import ua_parser.Client;
import ua_parser.Parser;

import java.util.List;


@RequiredArgsConstructor
@Service
@Slf4j
public class LoginHistoryServiceImpl implements LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;
    private final UserHistoryMapper userHistoryMapper;

    public void saveLoginHistory(User user, HttpServletRequest request) {
        DeviceInfoDto deviceInfoDto = parse(request.getHeader("User-Agent"));

        if (!checkDeviceRegistered(user.getId(), deviceInfoDto)) {
            log.info("Bu cihazla artiq giris var.");
            LoginHistory loginHistory = LoginHistory.builder()
                    .user(user)
                    .device(deviceInfoDto.getDevice())
                    .os(deviceInfoDto.getOs())
                    .browser(deviceInfoDto.getBrowser())
                    .build();
            loginHistoryRepository.save(loginHistory);
        }

    }

    public List<String> getDevicesInfo(String userId) {
        List<LoginHistory> loginHistories = loginHistoryRepository.findByUserId(userId);
        return userHistoryMapper.response(loginHistories);
    }

    public boolean checkDeviceRegistered(String userId, DeviceInfoDto deviceInfoDto) {
        return loginHistoryRepository.existsByUserIdAndBrowserAndDeviceAndOs(userId, deviceInfoDto.getBrowser(), deviceInfoDto.getDevice(), deviceInfoDto.getOs());
    }

    public static DeviceInfoDto parseUserAgent(String user) {

        Parser parser = new Parser();
        Client client = parser.parse(user);

        String os = client.os.family;
        String browser = client.userAgent.family;
        String device = client.device.family;

        return new DeviceInfoDto(null, null, device, os, browser);
    }

    public DeviceInfoDto parse(String userAgent) {
        UserAgentAnalyzer uaa = UserAgentAnalyzer
                .newBuilder()
                .hideMatcherLoadStats()
                .withCache(10000)
                .build();

        UserAgent agent = uaa.parse(userAgent);

        String deviceBrand = agent.getValue("DeviceBrand");
        String deviceName = agent.getValue("DeviceName");
        String os = agent.getValue("OperatingSystemName");
        String browser = agent.getValue("AgentName");

        return new DeviceInfoDto(null, null, deviceBrand, deviceName, browser);
    }


}
