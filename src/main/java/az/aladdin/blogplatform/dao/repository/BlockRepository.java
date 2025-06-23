package az.aladdin.blogplatform.dao.repository;

import az.aladdin.blogplatform.dao.entities.Block;
import az.aladdin.blogplatform.dao.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, String> {

    @Query("select b.blocked from Block b where b.blocker.id = :blockerId")
    List<User> findBlockedByBlockerId(String blockerId);

    @Modifying
    @Query("DELETE FROM Block b WHERE b.blocked.id=:blockedId AND b.blocker.id=:blockerId")
    void deleteBlockByBlockedIdAndBlockerId(String blockedId, String blockerId);

    @Query("SELECT (COUNT(b.id)>0) FROM Block  b WHERE b.blocked.id=:blockedId AND b.blocker.id=:blockerId")
    boolean existsBlockByBlockedIdAndBlockerId(String blockedId, String blockerId);
}
