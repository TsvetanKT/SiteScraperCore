package Application;

import java.util.Arrays;
import java.util.List;
import web.WebScraper;

public class SiteScraper {

  public List<String> getIteration(String sourceName, String query, int iteratio) {
    // TODO Auto-generated method stub
    
    if (sourceName.contains("Google")) {
      WebScraper webScraper = new WebScraper();
      return webScraper.getGoogleImages("https://www.google.com/search?tbm=isch&q=" + query);
    }
    
    // TODO
    System.err.println("ERROR NOT implemented " + sourceName);
    return null;
  }
  
 public static List<String> getImplementedSources() {
    
    // TODO just for tests
    return Arrays.asList("Google Images", "Other");
  }
}
