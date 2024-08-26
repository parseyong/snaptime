package me.snaptime.crawling.provider;


import me.snaptime.util.JsoupAction;

public abstract class AbstractProvider implements Provider {

    @Override
    public byte[] findCrawlingPhotoBytes(String crawlingImagePath) {

        return JsoupAction.crawlingPhoto(getHostname(), crawlingImagePath);
    }
}
