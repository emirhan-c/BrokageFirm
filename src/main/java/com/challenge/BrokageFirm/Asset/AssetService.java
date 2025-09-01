package com.challenge.BrokageFirm.Asset;

import com.challenge.BrokageFirm.Asset.Entity.Asset;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetService {
    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    public List<Asset> getAssetsByCustomerId(Long customerId) {
        return assetRepository.findByCustomerId(customerId);
    }
}

