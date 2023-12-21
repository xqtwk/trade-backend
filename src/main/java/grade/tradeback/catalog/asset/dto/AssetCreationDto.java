package grade.tradeback.catalog.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetCreationDto {
    private Long gameId;
    private Long assetTypeId;
    private Long userId;
    private String name;
    private String description;
    private double price;
    @Nullable
    private Integer amount;
}
