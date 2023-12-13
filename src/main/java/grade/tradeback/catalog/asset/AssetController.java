package grade.tradeback.catalog.asset;

import grade.tradeback.catalog.asset.dto.AssetCreationDto;
import grade.tradeback.catalog.asset.dto.AssetDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/assets")
public class AssetController {
    private final AssetService assetService;

    @GetMapping("")
    public ResponseEntity<List<AssetDetailsDto>> getAllAssets() {
        List<AssetDetailsDto> assets = assetService.getAllAssets();
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetDetailsDto> getAsset(@PathVariable Long id) {
        return assetService.getAssetById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping("/create")
    public ResponseEntity<AssetDetailsDto> createAsset(@RequestBody AssetCreationDto dto) {
        return ResponseEntity.ok(assetService.createAsset(dto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AssetDetailsDto> updateAsset(@PathVariable Long id, @RequestBody AssetCreationDto dto) {
        AssetDetailsDto updatedAsset = assetService.updateAsset(id, dto);
        if (updatedAsset == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedAsset);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/game/{gameName}")
    public ResponseEntity<List<AssetDetailsDto>> getAssetsByGameName(@PathVariable String gameName) {
        List<AssetDetailsDto> assets = assetService.getAssetsByGameName(gameName);
        return ResponseEntity.ok(assets);
    }
}
