package grade.tradeback.catalog.asset;

import grade.tradeback.catalog.game.Game;
import grade.tradeback.catalog.assetType.AssetType;
import grade.tradeback.trade.Trade;
import grade.tradeback.user.entity.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "asset")
public class Asset {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    Game game;
    @ManyToOne
    @JoinColumn(name = "assetType_id")
    AssetType assetType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Trade> tradeList = new ArrayList<>();

    private String name;

    private String description;

    private double price;
    @Nullable
    private Integer amount;
}
