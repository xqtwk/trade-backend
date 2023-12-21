package grade.tradeback.chat.chatMessage;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ChatMessage")
public class ChatMessage {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    private String chatId;
    private String senderUsername;
    private String recipientUsername;
    private String content;

    private Date timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = new Date(); // Set timestamp when the entity is persisted
    }
}