package web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import com.gargoylesoftware.css.parser.CSSErrorHandler;
import com.gargoylesoftware.css.parser.CSSException;
import com.gargoylesoftware.css.parser.CSSParseException;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

public class WebScraper {

  public List<String> getGoogleImages(String url) {
    List<String> imageUrls = new ArrayList<>();
    try {
      WebClient webClient = gethtmlUnitClient();
      HtmlPage page = webClient.getPage(url);
      
      
      webClient.getCurrentWindow().setInnerHeight(8000);

      /*
       * page.executeJavaScript("window.scrollTo(0,document.body.scrollHeight);");
       * webClient.waitForBackgroundJavaScript(2000);
       * page.executeJavaScript("window.scrollTo(0,document.body.scrollHeight);");
       * webClient.waitForBackgroundJavaScript(2000);
       */

      // webClient.getCurrentWindow().setInnerHeight(60000);
      
      List<Object> imageData = page.getByXPath("//div[@class='rg_meta notranslate']");


      for (Object div : imageData) {
        DomElement divObj = (DomElement) div;
        String inerText = divObj.getTextContent();
        JSONObject jsonObj = new JSONObject(inerText);
        String imgUrl = jsonObj.getString("ou");
        imageUrls.add(imgUrl);
      }

    } catch (Exception e) {
      System.err.println("ERROR scrapping" + e.getMessage());
    }

    return imageUrls;
  }

  static public WebClient gethtmlUnitClient() {
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
