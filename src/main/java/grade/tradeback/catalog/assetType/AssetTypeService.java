package grade.tradeback.catalog.assetType;



import grade.tradeback.catalog.assetType.dto.AssetTypeCreationDto;
import grade.tradeback.catalog.assetType.dto.AssetTypeDetailsDto;
import grade.tradeback.catalog.game.Game;
import grade.tradeback.catalog.game.GameRepository;
import grade.tradeback.catalog.game.dto.GameDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AssetTypeService {
    private AssetTypeRepository assetTypeRepository;
    private GameRepository gameRepository;
@Autowired
    public AssetTypeService(AssetTypeRepository assetTypeRepository, GameRepository gameRepository) {
        this.assetTypeRepository = assetTypeRepository;
        this.gameRepository = gameRepository;
    }

    public AssetTypeDetailsDto createAssetType(AssetTypeCreationDto dto) {
        AssetType assetType = new AssetType();
        assetType.setName(dto.getName());

        Game game = gameRepository.findById(dto.getGameId()).orElse(null);
        assetType.setGame(game);
        System.out.println("type: " + dto.getType());
        assetType.setType(dto.getType());
        // Update list in related entity
        if (game != null) game.getAssetTypes().add(assetType);

        AssetType savedAssetType = assetTypeRepository.save(assetType);
        return convertToAssetTypeDetailsDto(savedAssetType);
    }

    public AssetTypeDetailsDto updateAssetType(Long id, AssetTypeCreationDto dto) {
        Optional<AssetType> existingAssetType = assetTypeRepository.findById(id);
        if (!existingAssetType.isPresent()) {
            return null;
        }

        AssetType assetType = existingAssetType.get();
        assetType.setName(dto.getName());

        // Update the game only if it changes
        Game newGame = gameRepository.findById(dto.getGameId()).orElse(null);
        if (assetType.getGame() != null && !assetType.getGame().equals(newGame)) {
            assetType.getGame().getAssetTypes().remove(assetType);
        }
        assetType.setGame(newGame);
        if (newGame != null) newGame.getAssetTypes().add(assetType);

        AssetType updatedAssetType = assetTypeRepository.save(assetType);
        return convertToAssetTypeDetailsDto(updatedAssetType);
    }

    public void deleteAssetType(Long id) {
        assetTypeRepository.findById(id).ifPresent(assetType -> {
            if (assetType.getGame() != null) {
                assetType.getGame().getAssetTypes().remove(assetType);
            }
            assetTypeRepository.delete(assetType);
        });
    }

    public List<AssetTypeDetailsDto> getAllAssetTypes() {
        return assetTypeRepository.findAll().stream()
                .map(this::convertToAssetTypeDetailsDto)
                .collect(Collectors.toList());
    }

    public Optional<AssetTypeDetailsDto> getAssetTypeById(Long id) {
        return assetTypeRepository.findById(id)
                .map(this::convertToAssetTypeDetailsDto);
    }

    public AssetTypeDetailsDto convertToAssetTypeDetailsDto(AssetType assetType) {
        GameDetailsDto gameDto = null;
        if (assetType.getGame() != null) {
            gameDto = GameDetailsDto.builder()
                    .id(assetType.getGame().getId())
                    .name(assetType.getGame().getName())
                    // Additional fields as necessary
                    .build();
        }

        return AssetTypeDetailsDto.builder()
                .id(assetType.getId())
                .name(assetType.getName())
                .game(gameDto)
                .build();
    }

    public List<AssetTypeDetailsDto> getAssetTypesByGameName(String gameName) {
        return assetTypeRepository.findAssetTypesByGameName(gameName).stream()
                .map(this::convertToAssetTypeDetailsDto)
                .collect(Collectors.toList());
    }


}
