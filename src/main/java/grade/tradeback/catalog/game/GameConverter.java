package grade.tradeback.catalog.game;

import grade.tradeback.catalog.game.dto.GameDetailsDto;

public class GameConverter {
    public static GameDetailsDto convertToGameDetailsDto(Game game) {
        if (game == null) {
            return null;
        }
        return GameDetailsDto.builder()
                .id(game.getId())
                .name(game.getName())
                // add other fields as necessary
                .build();
    }
}
