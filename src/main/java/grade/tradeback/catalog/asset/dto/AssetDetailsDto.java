package grade.tradeback.catalog.asset.dto;

import grade.tradeback.catalog.assetType.dto.AssetTypeDetailsDto;
import grade.tradeback.catalog.game.dto.GameDetailsDto;
import grade.tradeback.user.dto.UserPublicDataRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetDetailsDto {
    private Long id;
    private GameDetailsDto game;
    private AssetTypeDetailsDto assetType;
    private UserPublicDataRequest user;
    private String name;
    private String description;
    private double price;
    @Nullable
    private Integer amount;
}
