package grade.tradeback.catalog.asset;


import grade.tradeback.catalog.asset.dto.AssetCreationDto;
import grade.tradeback.catalog.asset.dto.AssetDetailsDto;
import grade.tradeback.catalog.assetType.AssetType;
import grade.tradeback.catalog.assetType.AssetTypeRepository;
import grade.tradeback.catalog.assetType.AssetTypeService;
import grade.tradeback.catalog.assetType.dto.AssetTypeDetailsDto;
import grade.tradeback.catalog.game.Game;
import grade.tradeback.catalog.game.GameRepository;
import grade.tradeback.catalog.game.GameService;
import grade.tradeback.catalog.game.dto.GameDetailsDto;
import grade.tradeback.user.UserRepository;
import grade.tradeback.user.UserService;
import grade.tradeback.user.dto.UserPublicDataRequest;
import grade.tradeback.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import grade.tradeback.catalog.game.GameConverter;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class AssetService {
    private final AssetRepository assetRepository;
    private final GameRepository gameRepository;
    private final AssetTypeRepository assetTypeRepository;
    private final UserRepository userRepository;

  //  private final GameService gameService;
    private final AssetTypeService assetTypeService;




    public AssetDetailsDto createAsset(AssetCreationDto dto) {
        Asset asset = new Asset();
        // Set fields from dto
        asset.setName(dto.getName());
        asset.setDescription(dto.getDescription());
        asset.setPrice(dto.getPrice());
        asset.setAmount(dto.getAmount());

        // Fetch and set related entities
        Game game = gameRepository.findById(dto.getGameId()).orElse(null);
        AssetType assetType = assetTypeRepository.findById(dto.getAssetTypeId()).orElse(null);
        User user = userRepository.findById(dto.getUserId()).orElse(null);

        asset.setGame(game);
        asset.setAssetType(assetType);
        asset.setUser(user);

        // Update lists in related entities
        if (game != null) game.getAssets().add(asset);
        if (assetType != null) assetType.getAssets().add(asset);
        if (user != null) user.getAssets().add(asset);

        Asset savedAsset = assetRepository.save(asset);
        return convertToAssetDetailsDto(savedAsset);
    }

    public AssetDetailsDto updateAsset(Long id, AssetCreationDto dto) {
        Optional<Asset> existingAsset = assetRepository.findById(id);
        if (!existingAsset.isPresent()) {
            return null;
        }

        Asset asset = existingAsset.get();
        // Update fields from dto
        asset.setName(dto.getName());
        asset.setDescription(dto.getDescription());
        asset.setPrice(dto.getPrice());
        asset.setAmount(dto.getAmount());

        // Update related entities if they change
        updateRelatedEntitiesForAsset(asset, dto);

        Asset updatedAsset = assetRepository.save(asset);
        return convertToAssetDetailsDto(updatedAsset);
    }


    public void deleteAsset(Long id) {
        assetRepository.findById(id).ifPresent(asset -> {
            // Remove the asset from related entities' collections
            if (asset.getGame() != null) asset.getGame().getAssets().remove(asset);
            if (asset.getAssetType() != null) asset.getAssetType().getAssets().remove(asset);
            if (asset.getUser() != null) asset.getUser().getAssets().remove(asset);

            assetRepository.delete(asset);
        });
    }

    private UserPublicDataRequest convertToUserPublicDataRequest(User user) {
        return new UserPublicDataRequest(user.getId(), user.getUsername());
    }

    public List<AssetDetailsDto> getAllAssets() {
        return assetRepository.findAll().stream()
                .map(this::convertToAssetDetailsDto)
                .collect(Collectors.toList());
    }

    public Optional<AssetDetailsDto> getAssetById(Long id) {
        return assetRepository.findById(id)
                .map(this::convertToAssetDetailsDto);
    }

    private void updateRelatedEntitiesForAsset(Asset asset, AssetCreationDto dto) {
        Game newGame = gameRepository.findById(dto.getGameId()).orElse(null);
        AssetType newAssetType = assetTypeRepository.findById(dto.getAssetTypeId()).orElse(null);
        User newUser = userRepository.findById(dto.getUserId()).orElse(null);

        // Remove asset from old relations
        if (asset.getGame() != null && !asset.getGame().equals(newGame)) {
            asset.getGame().getAssets().remove(asset);
        }
        if (asset.getAssetType() != null && !asset.getAssetType().equals(newAssetType)) {
            asset.getAssetType().getAssets().remove(asset);
        }
        if (asset.getUser() != null && !asset.getUser().equals(newUser)) {
            asset.getUser().getAssets().remove(asset);
        }

        // Set new relations
        asset.setGame(newGame);
        asset.setAssetType(newAssetType);
        asset.setUser(newUser);

        // Add asset to new relations
        if (newGame != null) newGame.getAssets().add(asset);
        if (newAssetType != null) newAssetType.getAssets().add(asset);
        if (newUser != null) newUser.getAssets().add(asset);
    }

    public AssetDetailsDto convertToAssetDetailsDto(Asset asset) {
        GameDetailsDto gameDto = GameConverter.convertToGameDetailsDto(asset.getGame());

        //GameDetailsDto gameDto = asset.getGame() != null ? gameService.convertToGameDetailsDto(asset.getGame()) : null;
        AssetTypeDetailsDto assetTypeDto = asset.getAssetType() != null ? assetTypeService.convertToAssetTypeDetailsDto(asset.getAssetType()) : null;
        UserPublicDataRequest userDto = asset.getUser() != null ? convertToUserPublicDataRequest(asset.getUser()) : null;

        return AssetDetailsDto.builder()
                .id(asset.getId())
                .game(gameDto)
                .assetType(assetTypeDto)
                .user(userDto)
                .name(asset.getName())
                .description(asset.getDescription())
                .price(asset.getPrice())
                .amount(asset.getAmount())
                .build();
    }

    public List<AssetDetailsDto> getAssetsByGameName(String gameName) {
        return assetRepository.findAssetsByGameName(gameName).stream()
                .map(this::convertToAssetDetailsDto)
                .collect(Collectors.toList());
    }
    public Optional<Asset> findById(Long id) {
        return assetRepository.findById(id);
    }
}
