package me.snaptime.util;

import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class JsoupAction {

    /*
        URL에 접속해 크롤링하여 Docs를 가져옵니다.

        crawlingURL : Docs를 가져오기위한 크롤링URL
    */
    public static Document crawlingDocument(String crawlingURL) {
        try {
            return Jsoup.connect(crawlingURL)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
                    .timeout(3000)
                    .get();
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.CRAWLING_FAIL);
        }
    }

    /*
        doc의 특정 cssQuery요소를 사용하여 image요소를 가져오고 image요소 중 src값을 반환합니다.

        doc      : 요소를 가져올 문서
        cssQuery : 가져올 요소
    */
    public static String findSrcByDocs(Document doc, String cssQuery) {
        Elements image = doc.select(cssQuery);
        return image.attr("src");
    }

    /*
        doc의 특정 cssQuery요소를 사용하여 a요소를 가져오고 a요소 중 href값을 반환합니다.

        doc      : 요소를 가져올 문서
        cssQuery : 가져올 요소
    */
    public static String findHrefByDocs(Document doc, String cssQuery) {
        Elements a = doc.select(cssQuery);
        return a.attr("href");
    }

    /*
        크롤링된 이미지를 가져옵니다.

        hostName          : 이미지를 가져올 호스트명
        crawlingImagePath : 이미지를 가져올 경로
    */
    public static byte[] crawlingPhoto(String hostName, String crawlingImagePath) {
        try {
            URL imageURL = new URL("http://"+ hostName + crawlingImagePath);
            URLConnection connection = imageURL.openConnection();
            InputStream inputStream = connection.getInputStream();
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.CRAWLING_FAIL);
        }
    }
}
