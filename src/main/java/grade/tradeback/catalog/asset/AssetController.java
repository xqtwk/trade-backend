package grade.tradeback.catalog.asset;

import grade.tradeback.catalog.asset.dto.AssetCreationDto;
import grade.tradeback.catalog.asset.dto.AssetDetailsDto;
import grade.tradeback.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

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

    @GetMapping("/user/{username}")
    public ResponseEntity<List<AssetDetailsDto>> getAllUserAssets(@PathVariable String username) {
        List<AssetDetailsDto> assets = assetService.getAllUserAssets(username);
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetDetailsDto> getAsset(@PathVariable Long id) {
        return assetService.getAssetById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping("/create")
    public ResponseEntity<?> createAsset(@RequestBody AssetCreationDto dto, Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Optional<AssetDetailsDto> createdAsset = assetService.createAsset(dto, user);

        if (createdAsset.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error Message Here");
        }

        return ResponseEntity.ok(createdAsset.get());
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAsset(@PathVariable Long id, @RequestBody AssetCreationDto dto,
                                                       Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            Optional<AssetDetailsDto> updatedAsset = assetService.updateAsset(id, dto, user);

            System.out.println(dto);
            if (updatedAsset.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error Message Here");
            }
            return ResponseEntity.ok(updatedAsset);

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id, Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        boolean isDeleted = assetService.deleteAsset(id, user);

        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/game/{gameName}")
    public ResponseEntity<List<AssetDetailsDto>> getAssetsByGameName(@PathVariable String gameName) {
        List<AssetDetailsDto> assets = assetService.getAssetsByGameName(gameName);
        return ResponseEntity.ok(assets);
    }
}
