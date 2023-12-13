package grade.tradeback.catalog.game.dto;

import grade.tradeback.catalog.asset.dto.AssetDetailsDto;
import grade.tradeback.catalog.assetType.dto.AssetTypeDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDetailsDto {
    private Long id;
    private String name;
    private List<AssetTypeDetailsDto> assetTypes;
    private List<AssetDetailsDto> assets;
}
