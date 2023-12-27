package grade.tradeback.trade;

import grade.tradeback.chat.chatRoom.ChatRoom;
import grade.tradeback.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findBySenderUsernameOrReceiverUsername(String senderUsername, String recipientUsername);
    List<Trade> findBySenderUsername(String senderUsername);
    List<Trade> findByReceiverUsername(String recipientUsername);
    List<Trade> findByStatus(TradeStatus status);
    Optional<Trade> findById(Long id);
}
