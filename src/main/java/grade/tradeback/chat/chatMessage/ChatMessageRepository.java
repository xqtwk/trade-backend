package grade.tradeback.chat.chatMessage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatId(String chatId);
    List<ChatMessage> findByChatIdAndSenderUsername(String chatId, String username);
}
