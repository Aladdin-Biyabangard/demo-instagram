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

    @Override
    public void saveLoginHistory(User user, HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        log.info("Saving login history for user ID: {}", user.getId());
        log.debug("User-Agent string received: {}", userAgent);

        DeviceInfoDto deviceInfoDto = parse(userAgent);

        if (checkDeviceRegistered(user.getId(), deviceInfoDto)) {
            log.info("User ID: {} has already logged in with this device ({} / {} / {})",
                    user.getId(),
                    deviceInfoDto.getDevice(),
                    deviceInfoDto.getOs(),
                    deviceInfoDto.getBrowser()
            );
        }

        LoginHistory loginHistory = LoginHistory.builder()
                .user(user)
                .device(deviceInfoDto.getDevice())
                .os(deviceInfoDto.getOs())
                .browser(deviceInfoDto.getBrowser())
                .build();

        loginHistoryRepository.save(loginHistory);
        log.info("Login history saved for user ID: {}", user.getId());
    }

    @Override
    public List<String> getDevicesInfo(String userId) {
        log.info("Fetching login device info for user ID: {}", userId);
        List<LoginHistory> loginHistories = loginHistoryRepository.findByUserId(userId);
        log.debug("Found {} login history entries for user ID: {}", loginHistories.size(), userId);
        return userHistoryMapper.response(loginHistories);
    }

    public boolean checkDeviceRegistered(String userId, DeviceInfoDto deviceInfoDto) {
        boolean exists = loginHistoryRepository.existsByUserIdAndBrowserAndDeviceAndOs(
                userId,
                deviceInfoDto.getBrowser(),
                deviceInfoDto.getDevice(),
                deviceInfoDto.getOs()
        );
        log.debug("Device registration check for user ID {}: {}", userId, exists);
        return exists;
    }

    public static DeviceInfoDto parseUserAgent(String user) {
        log.debug("Parsing user agent with ua-parser: {}", user);
        Parser parser = new Parser();
        Client client = parser.parse(user);

        String os = client.os.family;
        String browser = client.userAgent.family;
        String device = client.device.family;

        return new DeviceInfoDto(null, null, device, os, browser);
    }

    public DeviceInfoDto parse(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            log.warn("User-Agent header is null or empty.");
            return new DeviceInfoDto(null, null, "Unknown Device", "Unknown OS", "Unknown Browser");
        }

        log.debug("Parsing user agent with UserAgentAnalyzer: {}", userAgent);
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

        log.debug("Parsed device info: brand={}, name={}, os={}, browser={}", deviceBrand, deviceName, os, browser);
        return new DeviceInfoDto(null, null, deviceBrand, os, browser);
    }
}
