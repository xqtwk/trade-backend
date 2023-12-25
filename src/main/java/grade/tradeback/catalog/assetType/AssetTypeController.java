package grade.tradeback.catalog.assetType;

import grade.tradeback.catalog.assetType.dto.AssetTypeDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/asset-types")
public class AssetTypeController {
    private final AssetTypeRepository assetTypeRepository;
    private final AssetTypeService assetTypeService;
    @GetMapping("")
    public ResponseEntity<List<AssetTypeDetailsDto>> getAllAssetTypes() {
        List<AssetTypeDetailsDto> assetTypes = assetTypeService.getAllAssetTypes();
        return ResponseEntity.ok(assetTypes);

    }

    public ResponseEntity<AssetTypeDetailsDto> getAssetType(@PathVariable Long id) {
        return assetTypeService.getAssetTypeById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/game/{gameName}")
    public ResponseEntity<List<AssetTypeDetailsDto>> getAssetTypesByGameName(@PathVariable String gameName) {
        List<AssetTypeDetailsDto> assetTypes = assetTypeService.getAssetTypesByGameName(gameName);
        return ResponseEntity.ok(assetTypes);
    }
}
