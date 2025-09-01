package com.challenge.BrokageFirm.Asset;

import com.challenge.BrokageFirm.Asset.Entity.Asset;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/assets")
public class AssetController {
    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/list/{customerId}")
    public List<Asset> listAssetsByCustomer(@PathVariable Long customerId) {
        return assetService.getAssetsByCustomerId(customerId);
    }
}

