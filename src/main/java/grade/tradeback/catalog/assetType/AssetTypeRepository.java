package grade.tradeback.catalog.assetType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AssetTypeRepository extends JpaRepository<AssetType, Long> {
    Optional<AssetType> findAssetTypeById(Long id);
    @Query("SELECT at FROM AssetType at WHERE at.game.name = :gameName")
    List<AssetType> findAssetTypesByGameName(String gameName);
}
