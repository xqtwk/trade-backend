package grade.tradeback.catalog.assetType.dto;

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
    private Long gameId;
}