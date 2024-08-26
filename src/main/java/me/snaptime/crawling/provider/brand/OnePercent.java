package me.snaptime.crawling.provider.brand;

import me.snaptime.crawling.enums.ProviderBrand;
import me.snaptime.crawling.provider.AbstractProvider;
import me.snaptime.util.JsoupAction;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class OnePercent extends AbstractProvider {

    @Override
    public String findCrawlingImageURL(String crawlingURL) {
        Document crawledPage = JsoupAction.crawlingDocument(crawlingURL);
        return JsoupAction.findSrcByDocs(crawledPage, "div.main_cont > img");
    }

    @Override
    public String getHostname() {
        return "bc1.mx2.co.kr";
    }

    @Override
    public ProviderBrand getProviderBrand() {
        return ProviderBrand.ONE_PERCENT;
    }
}
