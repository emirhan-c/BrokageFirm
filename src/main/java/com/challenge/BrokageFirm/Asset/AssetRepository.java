package com.challenge.BrokageFirm.Asset;

import com.challenge.BrokageFirm.Asset.Entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByCustomerId(Long customerId);
    List<Asset> findByCustomerIdAndAssetName(Long customerId, String assetName);
}
