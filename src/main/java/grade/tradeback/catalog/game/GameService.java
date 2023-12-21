package grade.tradeback.catalog.game;


import grade.tradeback.catalog.asset.AssetService;
import grade.tradeback.catalog.asset.dto.AssetDetailsDto;
import grade.tradeback.catalog.assetType.AssetTypeService;
import grade.tradeback.catalog.assetType.dto.AssetTypeDetailsDto;
import grade.tradeback.catalog.game.dto.GameCreationDto;
import grade.tradeback.catalog.game.dto.GameDetailsDto;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor

public class GameService {
    private final GameRepository gameRepository;
    private AssetService assetService;
    private final AssetTypeService assetTypeService;
    @Autowired
    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }


/*
    public GameService(GameRepository gameRepository, AssetService assetService, AssetTypeService assetTypeService) {
        this.gameRepository = gameRepository;
        this.assetService = assetService;
        this.assetTypeService = assetTypeService;
    }*/

    public GameDetailsDto createGame(GameCreationDto dto) {
        Game game = new Game();
        game.setName(dto.getName());

        Game savedGame = gameRepository.save(game);
        return convertToGameDetailsDto(savedGame);
    }

    public GameDetailsDto updateGame(Long id, GameCreationDto dto) {
        Optional<Game> existingGame = gameRepository.findById(id);
        if (!existingGame.isPresent()) {
            return null;
        }

        Game game = existingGame.get();
        game.setName(dto.getName());

        // No need to update related entities for Game as they are managed by Asset and AssetType

        Game updatedGame = gameRepository.save(game);
        return convertToGameDetailsDto(updatedGame);
    }

    // Delete method remains as is since Game doesn't have a parent entity to be removed from
    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }

    public List<GameDetailsDto> getAllGames() {
        return gameRepository.findAll().stream()
                .map(this::convertToGameDetailsDto)
                .collect(Collectors.toList());
    }

    public Optional<GameDetailsDto> getGameById(Long id) {
        return gameRepository.findById(id)
                .map(this::convertToGameDetailsDto);
    }

    public GameDetailsDto convertToGameDetailsDto(Game game) {
        List<AssetTypeDetailsDto> assetTypeDtos = game.getAssetTypes().stream()
                .map(assetTypeService::convertToAssetTypeDetailsDto) // Use AssetTypeService's method
                .collect(Collectors.toList());

        List<AssetDetailsDto> assetDtos = game.getAssets().stream()
                .map(assetService::convertToAssetDetailsDto) // Use AssetService's method
                .collect(Collectors.toList());

        return GameDetailsDto.builder()
                .id(game.getId())
                .name(game.getName())
                .assetTypes(assetTypeDtos)
                .assets(assetDtos)
                .build();
    }
}
