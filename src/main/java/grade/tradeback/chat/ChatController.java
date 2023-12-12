package grade.tradeback.chat;

import grade.tradeback.chat.chatMessage.ChatMessage;
import grade.tradeback.chat.chatMessage.ChatMessageService;
import grade.tradeback.chat.chatRoom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        System.out.println("chat caught");
        ChatMessage savedMsg = chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientUsername(), "/queue/messages",
                new ChatNotification(
                        savedMsg.getId(),
                        savedMsg.getSenderUsername(),
                        savedMsg.getRecipientUsername(),
                        savedMsg.getContent()
                )
        );
    }

    @GetMapping("/messages/{senderUsername}/{recipientUsername}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable String senderUsername,
                                                              @PathVariable String recipientUsername) {
        System.out.println("blablabla findchatmessages fired");
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderUsername, recipientUsername));
    }

    @GetMapping("/chat-list/{username}")
    public ResponseEntity<List<String>> getChatList(@PathVariable String username) {
        List<String> chatList = chatRoomService.getChatListForUser(username);
        return ResponseEntity.ok(chatList);
    }

}