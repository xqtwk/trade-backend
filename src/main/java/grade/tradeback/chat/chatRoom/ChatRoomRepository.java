package grade.tradeback.chat.chatRoom;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findBySenderUsernameAndRecipientUsername(String senderUsername, String recipientUsername);
    List<ChatRoom> findBySenderUsernameOrRecipientUsername(String senderUsername, String recipientUsername);

}
