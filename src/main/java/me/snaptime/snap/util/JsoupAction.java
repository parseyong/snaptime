package me.snaptime.snap.util;

import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;


public class JsoupAction {

    public static Document getDocument(String url) {
        try {
            return Jsoup.connect(url).header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36").timeout(3000).get();
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.URL_HAVING_PROBLEM);
        }
    }

    public static String returnSrcBySelectedElements(Document doc, String cssQuery) {
        Elements image = doc.select(cssQuery);
        return image.attr("src");
    }
}
