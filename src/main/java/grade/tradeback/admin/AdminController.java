package grade.tradeback.admin;


import grade.tradeback.catalog.asset.AssetService;
import grade.tradeback.catalog.asset.dto.AssetCreationDto;
import grade.tradeback.catalog.asset.dto.AssetDetailsDto;
import grade.tradeback.catalog.assetType.AssetTypeService;
import grade.tradeback.catalog.assetType.dto.AssetTypeCreationDto;
import grade.tradeback.catalog.assetType.dto.AssetTypeDetailsDto;
import grade.tradeback.catalog.game.GameService;
import grade.tradeback.catalog.game.dto.GameCreationDto;
import grade.tradeback.catalog.game.dto.GameDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private GameService gameService;
    private AssetService assetService;
    private  AssetTypeService assetTypeService;
@Autowired
    public AdminController(GameService gameService, AssetService assetService, AssetTypeService assetTypeService) {
        this.gameService = gameService;
        this.assetService = assetService;
        this.assetTypeService = assetTypeService;
    }

    @PostMapping("/games/create")
    public ResponseEntity<GameDetailsDto> createGame(@RequestBody GameCreationDto dto) {
        return ResponseEntity.ok(gameService.createGame(dto));
    }

    @PutMapping("/games/update/{id}")
    public ResponseEntity<GameDetailsDto> updateGame(@PathVariable Long id, @RequestBody GameCreationDto dto) {
        GameDetailsDto updatedGame = gameService.updateGame(id, dto);
        if (updatedGame == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedGame);
    }

    @DeleteMapping("/games/delete/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/asset-types/create")
    public ResponseEntity<AssetTypeDetailsDto> createAssetType(@RequestBody AssetTypeCreationDto dto) {
        return ResponseEntity.ok(assetTypeService.createAssetType(dto));
    }

    @PutMapping("/asset-types/update/{id}")
    public ResponseEntity<AssetTypeDetailsDto> updateAssetType(@PathVariable Long id, @RequestBody AssetTypeCreationDto dto) {
        AssetTypeDetailsDto updatedAssetType = assetTypeService.updateAssetType(id, dto);
        if (updatedAssetType == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedAssetType);
    }

    @DeleteMapping("/asset-types/delete/{id}")
    public ResponseEntity<Void> deleteAssetType(@PathVariable Long id) {
        assetTypeService.deleteAssetType(id);
        return ResponseEntity.ok().build();
    }
    
}
