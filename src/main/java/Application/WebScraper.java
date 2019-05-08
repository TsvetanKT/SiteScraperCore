package Application;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

public class WebScraper implements IScraper {

  public List<String> getIteration(SourceObj sourceObj, String query, int iteration) {
    List<String> imageUrls = new ArrayList<>();
    try {
      WebClient webClient = getHtmlUnitClient();
      String queryUrl = String.format(sourceObj.getUrlQueryString(), query);
      HtmlPage page = webClient.getPage(queryUrl);
      
      List<Object> imageData = page.getByXPath(sourceObj.getParentXpath());

      for (Object parent : imageData) {
        DomElement divObj = (DomElement) parent;
        String inerText = divObj.getTextContent();
        JSONObject jsonObj = new JSONObject(inerText);
        String imgUrl = jsonObj.getString(sourceObj.getElementValue());
        imageUrls.add(imgUrl);
      }

    } catch (Exception e) {
      System.err.println("ERROR scrapping" + e.getMessage());
    }

    return imageUrls;
  }

  private static WebClient getHtmlUnitClient() {
    WebClient webClient;
    /*LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
        "org.apache.commons.logging.impl.NoOpLog");*/
    Logger.getLogger("org.apache.commons.logging.Log").setLevel(Level.OFF);
    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
    Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
    Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
    webClient = new WebClient(BrowserVersion.CHROME);
    webClient.setCssErrorHandler(new SilentCssErrorHandler());
    webClient.setIncorrectnessListener(new IncorrectnessListener() {
      @Override
      public void notify(String arg0, Object arg1) {}
    });
    webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {
      
      @Override
      public void warn(String message, String sourceName, int line, String lineSource, int lineOffset) {
      }
      
      @Override
      public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {
      }
      
      @Override
      public void scriptException(HtmlPage page, ScriptException scriptException) {
      }
      
      @Override
      public void malformedScriptURL(HtmlPage page, String url,
          MalformedURLException malformedURLException) {
      }
      
      @Override
      public void loadScriptError(HtmlPage page, URL scriptUrl, Exception exception) {
      }
    });

    webClient.getOptions().setThrowExceptionOnScriptError(false);
    return webClient;
  }
}
