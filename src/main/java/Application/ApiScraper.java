package Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.httpclient.HttpClient;
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

  public List<String> getIteration(SourceObj sourceObj, String query, int iteration) {
    return null;
  }

  public static void main(String[] args) throws Exception {
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
    
    
    
    
    
    
    /*
     {"eu_resident":true,"gdpr_is_acceptable_age":true,"gdpr_consent_core":true,"gdpr_consent_first_party_ads":true,"gdpr_consent_third_party_ads":true,"gdpr_consent_search_history":true,"redirect_to":"https://cutest-cats.tumblr.com/page/15/json","gdpr_reconsent":false}
     */
    
    
 
    /*CloseableHttpResponse response = httpClient.execute(httpPost);
    System.out.println(response);
    System.out.println(response.getStatusLine().getStatusCode());
    httpClient.close();*/
    
    
    /*GetMethod get = new GetMethod("https://cutest-cats.tumblr.com/page/15/json");
    httpClient.executeMethod(get);
    InputStream stream = get.getResponseBodyAsStream();
    String theString = IOUtils.toString(stream, "UTF-8");
    System.out.println(theString.length());
    System.out.println(theString);
    countSubstring(theString, "a");*/
    
    
    
    
   /* String html = getHTML("https://cutest-cats.tumblr.com/page/15/json");
    
    //System.out.println(getHTML("https://cutest-cats.tumblr.com/page/15/json"));
    countSubstring(html, "_1280.");
    System.out.println(html.trim());*/
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
