package grade.tradeback.trade;


import com.fasterxml.jackson.annotation.JsonBackReference;
import grade.tradeback.catalog.asset.Asset;
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
    private int amount;
    private boolean senderConfirmed;
    private boolean receiverConfirmed;
    private double sum;
    @ManyToOne
    @JoinColumn(name = "asset_id")
    @JsonBackReference
    private Asset asset;

    @Enumerated(EnumType.STRING)
    private TradeStatus status;
}