package az.aladdin.blogplatform.controller;

import az.aladdin.blogplatform.model.dto.response.UserFollowResponseDto;
import az.aladdin.blogplatform.services.abstraction.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/blocks")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "blockers/{blockerId}/blocked/{blockedId}")
    public void blockUser(@PathVariable String blockerId,
                          @PathVariable String blockedId) {
        blockService.blockUser(blockerId, blockedId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "blockers/{blockerId}/blocked/{blockedId}")
    public void unblockUser(@PathVariable String blockerId,
                            @PathVariable String blockedId) {
        blockService.unBlock(blockerId, blockedId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "blockers/{blockerId}")
    public List<UserFollowResponseDto> blockList(@PathVariable String blockerId) {
        return blockService.blockList(blockerId);
    }


}
