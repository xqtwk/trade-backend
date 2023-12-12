package grade.tradeback.chat.chatRoom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public Optional<String> getChatRoomId(
            String senderUsername,
            String recipientUsername,
            boolean createNewRoomIfNotExists
    ) {
        return chatRoomRepository
                .findBySenderUsernameAndRecipientUsername(senderUsername, recipientUsername)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if(createNewRoomIfNotExists) {
                        var chatId = createChatId(senderUsername, recipientUsername);
                        return Optional.of(chatId);
                    }

                    return  Optional.empty();
                });
    }

    private String createChatId(String senderUsername, String recipientUsername) {
        var chatId = String.format("%s_%s", senderUsername, recipientUsername);

        ChatRoom senderRecipient = ChatRoom
                .builder()
                .chatId(chatId)
                .senderUsername(senderUsername)
                .recipientUsername(recipientUsername)
                .build();

        ChatRoom recipientSender = ChatRoom
                .builder()
                .chatId(chatId)
                .senderUsername(recipientUsername)
                .recipientUsername(senderUsername)
                .build();

        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        return chatId;
    }

    public List<String> getChatListForUser(String username) {
        return chatRoomRepository.findBySenderUsernameOrRecipientUsername(username, username)
                .stream()
                .map(chatRoom -> chatRoom.getSenderUsername().equals(username) ? chatRoom.getRecipientUsername() : chatRoom.getSenderUsername())
                .distinct()
                .collect(Collectors.toList());
    }
}
