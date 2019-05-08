package Application;

import java.util.List;

public class TestScraper {
  public static void main(String[] args) {
    
    SiteScraper scraper = new SiteScraper();
    List<String> urls = scraper.getIteration("Google Images", "mema", 1);
    
    System.out.println("Images number " + urls.size());
    for (String url : urls) {
      System.out.println(url);
    }
  }
}
