package grade.tradeback.chat.chatMessage;

import grade.tradeback.chat.chatRoom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage) {
        System.out.println("save method fired");
        var chatId = chatRoomService
                .getChatRoomId(chatMessage.getSenderUsername(), chatMessage.getRecipientUsername(), true)
                .orElseThrow(() -> new RuntimeException("Chat room could not be created"));
        chatMessage.setChatId(chatId);
        return repository.save(chatMessage);
    }

    public List<ChatMessage> findChatMessages(String senderUsername, String recipientUsername) {
        System.out.println("findchatmessages fired");
        var chatId = chatRoomService.getChatRoomId(senderUsername, recipientUsername, false);

        // Retrieve messages for both directions (sender to recipient and recipient to sender)
        List<ChatMessage> senderToRecipientMessages = chatId.map(id -> repository.findByChatIdAndSenderUsername(id, senderUsername))
                .orElse(new ArrayList<>());
        List<ChatMessage> recipientToSenderMessages = chatId.map(id -> repository.findByChatIdAndSenderUsername(id, recipientUsername))
                .orElse(new ArrayList<>());

        // Combine and sort messages by timestamp or any relevant criteria
        List<ChatMessage> allMessages = new ArrayList<>();
        allMessages.addAll(senderToRecipientMessages);
        allMessages.addAll(recipientToSenderMessages);

        Collections.sort(allMessages, Comparator.comparing(ChatMessage::getTimestamp));

        return allMessages;
    }
}