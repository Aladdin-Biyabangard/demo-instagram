package az.aladdin.blogplatform.services.impl;

import az.aladdin.blogplatform.configuration.mappers.UserMapper;
import az.aladdin.blogplatform.dao.entities.Block;
import az.aladdin.blogplatform.dao.entities.User;
import az.aladdin.blogplatform.dao.repository.BlockRepository;
import az.aladdin.blogplatform.model.dto.response.UserFollowResponseDto;
import az.aladdin.blogplatform.services.abstraction.BlockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class BlockServiceImpl implements BlockService {

    private final UserServiceImpl userServiceImpl;
    private final BlockRepository blockRepository;
    private final UserMapper userMapper;


    @Transactional
    public void blockUser(String blockerId, String blockedId) {
        log.info("Attempting to block user with ID: {} by user with ID: {}", blockedId, blockerId);

        if (blockerId.equals(blockedId)) {
            log.warn("User {} attempted to block themselves.", blockerId);
            throw new IllegalArgumentException("User cannot block themselves.");
        }

        User blockerUser = userServiceImpl.findUserById(blockerId);
        User blockedUser = userServiceImpl.findUserById(blockedId);

        if (blockRepository.existsBlockByBlockedIdAndBlockerId(blockedId, blockerId)) {
            log.warn("User {} has already blocked user {}", blockerId, blockedId);
            throw new IllegalArgumentException("User is already blocked.");
        }

        Block block = new Block();
        block.setBlocked(blockedUser);
        block.setBlocker(blockerUser);

        // Remove any follower/following relationships
        blockerUser.getFollowers().remove(blockedUser);
        blockedUser.getFollowing().remove(blockerUser);

        blockedUser.getFollowing().remove(blockerUser);
        blockedUser.getFollowers().remove(blockerUser);

        blockRepository.save(block);
        log.info("User {} successfully blocked user {}", blockerId, blockedId);
    }

    @Transactional
    public void unBlock(String blockerId, String blockedId) {
        log.info("User is unblocked:{}", blockedId);
        blockRepository.deleteBlockByBlockedIdAndBlockerId(blockedId, blockerId);
        log.info("User unblocked.{}", blockedId);
    }

    @Override
    public List<UserFollowResponseDto> blockList(String blockerId) {
        log.info("Fetching blocked users for blocker with ID: {}", blockerId);
        List<User> blockedUsers = blockRepository.findBlockedByBlockerId(blockerId);

        if (blockedUsers.isEmpty()) {
            log.info("No users blocked by user {}", blockerId);
            return List.of();
        } else {
            log.info("User {} has blocked {} user(s)", blockerId, blockedUsers.size());
            return userMapper.mapToFollowResponseDto(blockedUsers);
        }
    }

}
