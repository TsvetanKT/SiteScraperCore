package Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class ApiScraper implements IScraper {
  
  private String cookie;
  private HttpClient apiClient;
  
  private static final int PAGES_PER_ITERATION = 10; 
  
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
    
    String key = getSubstring(body, "tumblr_form_key", ">").replace(" " , "");
    key =  getSubstring(key, "=\"", "\"");
    
    PostMethod post = new PostMethod("https://www.tumblr.com/svc/privacy/consent");
    post.addRequestHeader("X-tumblr-form-key", key);
    post.addRequestHeader("X-Requested-With", "XMLHttpRequest");
    post.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36");
    post.addRequestHeader("Referer", "https://www.tumblr.com/privacy/consent?redirect=https%3A%2F%2Fcutest-cats.tumblr.com%2Fpage%2F15%2Fjson");
    post.addRequestHeader("Content-Type", "application/json");
    post.addRequestHeader("Origin", "https://www.tumblr.com");
    post.addRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01");
    
    String requestBody = "{\"eu_resident\":true,\"gdpr_is_acceptable_age\":true,\"gdpr_consent_core\":true,\"gdpr_consent_first_party_ads\":true,\"gdpr_consent_third_party_ads\":true,\"gdpr_consent_search_history\":true,\"redirect_to\":\"https://cutest-cats.tumblr.com/page/15/json\",\"gdpr_reconsent\":false}";
    post.setRequestBody(requestBody);
    getApiClient().executeMethod(post);
    String coockie = getSubstring(post.getResponseHeader("Set-Cookie").toString(), "Set-Cookie: ", "; "); 
    return coockie;
  }

  public List<String> getIteration(SourceObj sourceObj, String query, int iteration) {
    
    query = cleanUpUrl(query);
    
    List<String> iterationUrls = getIterationUrls(query,PAGES_PER_ITERATION, iteration);
    List<String> imageUrls = new ArrayList<>();
    String rawPage = "";
    for (int i = 0; i < iterationUrls.size(); i++) {
      rawPage = getRawPage(iterationUrls.get(i));
      imageUrls.addAll(parsePageImages(rawPage));
    }
    
    System.out.println("called API");
    return imageUrls;
  }

  private static List<String> parsePageImages(String rawPage) {

    int lastStartIndex = 0;
    int lastEndIndex = 0;
    String startKeyword = "\"photo-url-1280\":\"";
    String endKeyword = "\"";
    String imageUrl ="";
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
   * @param url base url
   * @param pagesPerIteration number of pages that makes one iteration
   * @param iteration the current number of iterations
   * @return the url-s of this several pages
   */
  private static List<String> getIterationUrls(String urlFormat, int pagesPerIteration, int iteration) {
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
    } catch (IOException  e) {
      e.printStackTrace();
    }
    
    return body;
  }
  
  public static void main(String[] args) {
    /*String format = cleanUpUrl("https://cutest-cats.tumblr.com");
    List<String> urList = getIterationUrls(format, PAGES_PER_ITERATION, 1);
    for (String url : urList) {
      System.out.println(url);
    }*/
    
    /*String rawPage = "dth\":2816,\"height\":2112,\"photo-url-1280\":\"https:\\/\\/66.media.tumblr.com\\/64d5b62a7e14952eb12e9a4b9dd49952\\/tumblr_mhgk7mhcNB1r2h6ioo1_1280.jpg\",\"photo-";
    
    List<String> parsed =  parsePageImages(rawPage);
    System.out.println("wat");
    for (String url : parsed) {
      System.out.println(url);
    }*/
    
    
    
    
    ApiScraper apiScraper = new ApiScraper();
    
    List<String> iteration = apiScraper.getIteration(null, "https://cutest-cats.tumblr.com", 1);
    
    for (String url : iteration) {
      System.out.println(url);
    }
    
  }

  private static String cleanUpUrl(String query) {
    if (query.endsWith("/")) {
      query = query.substring(0, query.length() - 2);
    }
    
    if (query.endsWith("tumblr.com")) {
      query += "/page/%s/json";
    }
    
    return query;
  }

  public static void main1(String[] args) throws Exception {
    System.out.println("hi");

    CloseableHttpClient httpClient = HttpClients.createDefault();
    
    /*HttpPost httpPost = new HttpPost("https://www.tumblr.com/svc/privacy/consent");
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("eu_resident", "true"));
    params.add(new BasicNameValuePair("gdpr_consent_core", "true"));
    params.add(new BasicNameValuePair("gdpr_consent_first_party_ads", "true"));
    params.add(new BasicNameValuePair("gdpr_consent_search_history", "true"));
    params.add(new BasicNameValuePair("gdpr_consent_third_party_ads", "true"));
    params.add(new BasicNameValuePair("gdpr_is_acceptable_age", "true"));
    params.add(new BasicNameValuePair("gdpr_reconsent", "false"));
    params.add(new BasicNameValuePair("redirect_to", "https://cutest-cats.tumblr.com/page/15/json"));
    httpPost.setEntity(new UrlEncodedFormEntity(params));
    httpPost.addHeader("", "");*/
    
    
    HttpClient client = new HttpClient();
    GetMethod get = new GetMethod("https://cutest-cats.tumblr.com/page/15/json");
    client.executeMethod(get);
    String body = get.getResponseBodyAsString();
    //System.out.println(body);
    
    countSubstring(body, "tumblr_form_key");
    String key = getSubstring(body, "tumblr_form_key", ">").replace(" " , "");
    key =  getSubstring(key, "=\"", "\"");
    
    System.out.println(key);
    
    PostMethod post = new PostMethod("https://www.tumblr.com/svc/privacy/consent");
    post.addRequestHeader("X-tumblr-form-key", key);
    post.addRequestHeader("X-Requested-With", "XMLHttpRequest");
    post.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36");
    post.addRequestHeader("Referer", "https://www.tumblr.com/privacy/consent?redirect=https%3A%2F%2Fcutest-cats.tumblr.com%2Fpage%2F15%2Fjson");
    post.addRequestHeader("Content-Type", "application/json");
    post.addRequestHeader("Origin", "https://www.tumblr.com");
    post.addRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01");
    
    String requestBody = "{\"eu_resident\":true,\"gdpr_is_acceptable_age\":true,\"gdpr_consent_core\":true,\"gdpr_consent_first_party_ads\":true,\"gdpr_consent_third_party_ads\":true,\"gdpr_consent_search_history\":true,\"redirect_to\":\"https://cutest-cats.tumblr.com/page/15/json\",\"gdpr_reconsent\":false}";
    post.setRequestBody(requestBody);
    client.executeMethod(post);
    
    String coockie = getSubstring(post.getResponseHeader("Set-Cookie").toString(), "Set-Cookie: ", "; "); 
    System.out.println("cookie? " + coockie);
    
    
    GetMethod get2 = new GetMethod("https://cutest-cats.tumblr.com/page/15/json");
    get2.addRequestHeader("cookie", coockie);
    get2.addRequestHeader("referer", "https://www.tumblr.com/");
    get2.addRequestHeader("upgrade-insecure-requests", "1");
    get2.addRequestHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36");
    client.executeMethod(get2);
    
    
    System.out.println("lenn " + get2.getResponseBodyAsString().length());
    countSubstring(get2.getResponseBodyAsString(), "_1280.");
    
  }
  
  

  static void countSubstring(String text, String substring) {
    int lastIndex = 0;
    int count = 0;

    while (lastIndex != -1) {

      lastIndex = text.indexOf(substring, lastIndex);

      if (lastIndex != -1) {
        count++;
        lastIndex += substring.length();
      }
    }
    System.out.println(count);
  }

  public static String getHTML(String urlToRead) throws Exception {
    StringBuilder result = new StringBuilder();
    URL url = new URL(urlToRead);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line;
    while ((line = rd.readLine()) != null) {
      result.append(line);
    }
    rd.close();
    return result.toString();
  }
  
  public static String getSubstring(String text, String startKeyword, String endKeyword) {

    int start = text.indexOf(startKeyword);
    int end = text.indexOf(endKeyword, start + startKeyword.length());
    return text.substring(start + startKeyword.length(), end);
  }

}
