package grade.tradeback.trade;

import grade.tradeback.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepository;
    private final UserService userService;
    public Trade createTrade(Long sellerUserId, Long buyerUserId, double amount) {
        // Fetch usernames from user IDs
        String sellerUsername = userService.getUsernameById(sellerUserId);
        String buyerUsername = userService.getUsernameById(buyerUserId);

        Trade trade = Trade.builder()
                .senderUsername(sellerUsername)
                .receiverUsername(buyerUsername)
                .amount(amount)
                .senderConfirmed(false)
                .receiverConfirmed(false)
                .build();

        return tradeRepository.save(trade);
    }

    public Optional<Trade> confirmReceiver(String tradeId) {
        return tradeRepository.findById(Long.parseLong(tradeId)).map(trade -> {
            trade.setReceiverConfirmed(true);
            return tradeRepository.save(trade);
        });
    }

    public Optional<Trade> confirmSender(String tradeId) {
        return tradeRepository.findById(Long.parseLong(tradeId)).map(trade -> {
            if (trade.isReceiverConfirmed()) {  // Ensure receiver has already confirmed
                trade.setSenderConfirmed(true);
                // TODO: Process the trade, e.g., transfer amount
            }
            return tradeRepository.save(trade);
        });
    }
    public Optional<Trade> findById(Long id) {
        return tradeRepository.findById(id);
    }
}
