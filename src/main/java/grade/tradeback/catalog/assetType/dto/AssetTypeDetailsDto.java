package grade.tradeback.catalog.assetType.dto;

import grade.tradeback.catalog.assetType.AssetTypeType;
import grade.tradeback.catalog.game.dto.GameDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetTypeDetailsDto {
    private Long id;
    private String name;
    private AssetTypeType type;
    private GameDetailsDto game;
}