package Application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class ApiScraper implements IScraper {

    private String cookie;
    private HttpClient apiClient;

    private static final int PAGES_PER_ITERATION = 10;

    private static final Pattern TUMBLR_NAME_PATERN = Pattern.compile(
            "[^\"\\/www\\.\"](?<!w{3})[A-Za-z0-9-_]*(?=\\.tumblr\\.com)|(?<=\\.tumblr\\.com\\/blog\\/).*");
    private static final Pattern HTTP_PATERN = Pattern.compile("^(http://)");

    private HttpClient getApiClient() {
        if (apiClient == null) {
            apiClient = new HttpClient();
        }

        return apiClient;
    }

    private String getCookie(String blogUrl) {

        if (cookie == null) {
            try {
                cookie = getCookieStrategy(blogUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return cookie;
    }

    private String getCookieStrategy(String blogUrl) throws IOException {

        GetMethod get = new GetMethod(blogUrl);
        getApiClient().executeMethod(get);
        String body = get.getResponseBodyAsString();

        String key = getSubstring(body, "tumblr_form_key", ">").replace(" ", "");
        key = getSubstring(key, "=\"", "\"");

        PostMethod post = new PostMethod("https://www.tumblr.com/svc/privacy/consent");
        post.addRequestHeader("X-tumblr-form-key", key);
        post.addRequestHeader("X-Requested-With", "XMLHttpRequest");
        post.addRequestHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36");
        post.addRequestHeader("Referer",
                "https://www.tumblr.com/privacy/consent?redirect=https%3A%2F%2Fcutest-cats.tumblr.com%2Fpage%2F15%2Fjson");
        post.addRequestHeader("Content-Type", "application/json");
        post.addRequestHeader("Origin", "https://www.tumblr.com");
        post.addRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01");

        String requestBody =
                "{\"eu_resident\":true,\"gdpr_is_acceptable_age\":true,\"gdpr_consent_core\":true,\"gdpr_consent_first_party_ads\":true,\"gdpr_consent_third_party_ads\":true,\"gdpr_consent_search_history\":true,\"redirect_to\":\"https://cutest-cats.tumblr.com/page/15/json\",\"gdpr_reconsent\":false}";
        post.setRequestBody(requestBody);
        getApiClient().executeMethod(post);
        String coockie =
                getSubstring(post.getResponseHeader("Set-Cookie").toString(), "Set-Cookie: ", "; ");
        return coockie;
    }

    public List<String> getIteration(SourceObj sourceObj, String query, int iteration) {

        query = prepareUrl(query);

        List<String> iterationUrls = getIterationUrls(query, PAGES_PER_ITERATION, iteration);
        List<String> imageUrls = new ArrayList<>();
        String rawPage = "";

        for (int i = 0; i < iterationUrls.size(); i++) {
            rawPage = getRawPage(iterationUrls.get(i));
            imageUrls.addAll(parsePageImages(rawPage));
        }

        return imageUrls;
    }

    private static List<String> parsePageImages(String rawPage) {

        int lastStartIndex = 0;
        int lastEndIndex = 0;
        String startKeyword = "\"photo-url-1280\":\"";
        String endKeyword = "\"";
        String imageUrl = "";
        List<String> imageUrls = new ArrayList<>();
        while (lastStartIndex != -1) {

            lastStartIndex = rawPage.indexOf(startKeyword, lastStartIndex);

            if (lastStartIndex != -1) {
                lastStartIndex += startKeyword.length();
                lastEndIndex = rawPage.indexOf(endKeyword, lastStartIndex);
                imageUrl = rawPage.substring(lastStartIndex, lastEndIndex);
                imageUrl = imageUrl.replace("\\/", "/");
                imageUrls.add(imageUrl);
                lastStartIndex += startKeyword.length() + endKeyword.length() + imageUrl.length();
            }
        }

        return imageUrls;
    }

    /**
     * One 'iteration' for Tumblr means several pages of images
     * 
     * @param url base url
     * @param pagesPerIteration number of pages that makes one iteration
     * @param iteration the current number of iterations
     * @return the url-s of this several pages
     */
    private static List<String> getIterationUrls(String urlFormat, int pagesPerIteration,
            int iteration) {
        int startNum = pagesPerIteration * (iteration - 1);
        int endNum = startNum + pagesPerIteration;

        List<String> urList = new ArrayList<>(pagesPerIteration);

        for (int i = startNum; i <= endNum; i++) {
            if (i == 1) {
                // for some reason Tumblr counting is 0, 2, 3, ...
                continue;
            }

            urList.add(String.format(urlFormat, i));
        }

        return urList;
    }

    private String getRawPage(String url) {
        GetMethod get2 = new GetMethod(url);
        get2.addRequestHeader("cookie", getCookie(url));
        get2.addRequestHeader("referer", "https://www.tumblr.com/");
        get2.addRequestHeader("upgrade-insecure-requests", "1");
        get2.addRequestHeader("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36");

        String body = "";
        try {
            getApiClient().executeMethod(get2);
            body = get2.getResponseBodyAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return body;
    }

    /**
     * @param query the inputed value
     * @return standardized tumblr format string. Example
     *         http://its-a-cat-world.tumblr.com/page/%s/json
     */
    private static String prepareUrl(String query) {



        Matcher nameMatch = TUMBLR_NAME_PATERN.matcher(query);
        String tumblrName = nameMatch.find() ? nameMatch.group() : query;
        boolean httpMatch = HTTP_PATERN.matcher(query).find();
        String protocol = httpMatch ? "http://" : "https://";
        tumblrName = protocol + tumblrName + ".tumblr.com/page/%s/json";
        return tumblrName;
    }

    public static String getSubstring(String text, String startKeyword, String endKeyword) {

        int start = text.indexOf(startKeyword);
        int end = text.indexOf(endKeyword, start + startKeyword.length());
        return text.substring(start + startKeyword.length(), end);
    }
}
