package me.snaptime.crawling.provider.brand;

import me.snaptime.crawling.enums.ProviderBrand;
import me.snaptime.crawling.provider.AbstractProvider;
import me.snaptime.util.JsoupAction;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class Signature extends AbstractProvider {

    @Override
    public String findCrawlingImageURL(String crawlingURL) {
        Document crawledPage = JsoupAction.crawlingDocument(crawlingURL);
        // URL에서 index.html 삭제
        String hostName = crawlingURL.replace("index.html", "");
        // URL에서 http:// 삭제
        return  hostName.replace("http://", "") +
                JsoupAction.findHrefByDocs(crawledPage, "body > div > div.download-buttons > a:nth-child(1)");
    }

    @Override
    public String getHostname() {
        // Signature브랜드는 호스트명이 crawlingURL 호스트와 동일하여 findCrawlingImageURL()에서 추출하기 떄문에 ""을 반환합니다.
        return "";
    }

    @Override
    public ProviderBrand getProviderBrand() {
        return ProviderBrand.PHOTO_SIGNATURE;
    }
}
