package me.snaptime.crawling.service;

import me.snaptime.crawling.enums.ProviderBrand;

public interface CrawlingService {

    /*
        크롤링하여 인생네컷 사진을 가져옵니다.
        사진의 byte[]을 반환합니다.

        providerBrand : 크롤링할 인생네컷 제공사 브랜드
        crawlingURL   : 크롤링할 URL주소
    */
    byte[] findPhotoByCrawling(ProviderBrand providerBrand, String crawlingURL);
}
