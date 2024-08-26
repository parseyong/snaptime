package me.snaptime.crawling.service.impl;

import me.snaptime.crawling.enums.ProviderBrand;
import me.snaptime.crawling.provider.Provider;
import me.snaptime.crawling.service.CrawlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class CrawlingServiceImpl implements CrawlingService {
    private final Map<ProviderBrand, Provider> providers;

    @Autowired
    public CrawlingServiceImpl(List<Provider> providers) {

        this.providers = providers.stream()
                .collect(Collectors.toMap(
                        provider -> provider.getProviderBrand(),
                        provider -> provider
                ));
    }

    @Override
    public byte[] findPhotoByCrawling(ProviderBrand providerBrand, String crawlingURL) {

        Provider provider = providers.get(providerBrand);
        String crawlingImagePath = provider.findCrawlingImageURL(crawlingURL);
        return provider.findCrawlingPhotoBytes(crawlingImagePath);
    }
}
