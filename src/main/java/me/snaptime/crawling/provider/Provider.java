package me.snaptime.crawling.provider;

import me.snaptime.crawling.enums.ProviderBrand;

public interface Provider {
    /*
        crawlingURL의 문서를 읽어와 크롤링을 위한 URL을 가져옵니다.
        이미지 크롤링URL을 반환합니다.

        crawlingURL : 문서를 읽어올 URL, 인생네컷 바코드를 스캔한 url입니다.
    */
    String findCrawlingImageURL(String crawlingURL);

    /*
        이미지를 가져오기 위해 Provider의 호스트명을 가져옵니다.
    */
    String getHostname();

    /*
        PhotoProvider브랜드를 가져오는 메소드입니다.
        ENUM타입의 PhotoProviderBrand 객체를 반환합니다.
    */
    ProviderBrand getProviderBrand();
}
