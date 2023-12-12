package grade.tradeback.chat.chatRoom;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "chat_room")
public class ChatRoom {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    private String chatId;
    private String senderUsername;
    private String recipientUsername;
}
