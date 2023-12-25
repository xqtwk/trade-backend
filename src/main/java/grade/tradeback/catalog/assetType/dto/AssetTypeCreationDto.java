package grade.tradeback.catalog.assetType.dto;

import grade.tradeback.catalog.assetType.AssetTypeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetTypeCreationDto {
    private String name;
    private AssetTypeType type;
    private Long gameId;
}