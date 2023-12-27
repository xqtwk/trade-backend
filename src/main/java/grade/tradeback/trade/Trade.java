package grade.tradeback.trade;


import com.fasterxml.jackson.annotation.JsonBackReference;
import grade.tradeback.catalog.asset.Asset;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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

    @Column(name = "creation_time")
    private LocalDateTime creationTime;

    @PrePersist
    protected void onCreate() {
        creationTime = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }
}