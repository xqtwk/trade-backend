package grade.tradeback.catalog.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findAssetById(Long id);
    @Query("SELECT a FROM Asset a WHERE a.game.name = :gameName")
    List<Asset> findAssetsByGameName(String gameName);

    @Query("SELECT a FROM Asset a WHERE a.assetType.name = :assetTypeName")
    List<Asset> findAssetsByAssetTypeName(String assetTypeName);

    @Query("SELECT a FROM Asset a WHERE a.user.username = :username")
    List<Asset> findAssetsByUsername(String username);

    @Query("SELECT a FROM Asset a WHERE a.game.name = :gameName AND a.assetType.name = :assetTypeName")
    List<Asset> findAssetsByGameNameAndAssetTypeName(String gameName, String assetTypeName);
}
