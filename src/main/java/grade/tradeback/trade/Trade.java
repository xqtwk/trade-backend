package grade.tradeback.trade;


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
@Table(name = "trade")
public class Trade {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    private String senderUsername;
    private String receiverUsername;
    private double amount;
    private boolean senderConfirmed;
    private boolean receiverConfirmed;
}