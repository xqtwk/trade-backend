package grade.tradeback.catalog.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findAssetById(Long id);
    @Query("SELECT a FROM Asset a WHERE a.game.name = :gameName")
    List<Asset> findAssetsByGameName(String gameName);

}
