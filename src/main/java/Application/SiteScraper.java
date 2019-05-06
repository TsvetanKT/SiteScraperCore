package Application;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import web.WebScraper;

public class SiteScraper {
  
  Log logger = LogFactory.getLog(SiteScraper.class);

  private List<SourceObj> sources;
  
  public SiteScraper() {
    initSources();
  }

  public List<String> getIteration(String sourceName, String query, int iteratio) {

    if (sourceName.contains("Google")) {
      WebScraper webScraper = new WebScraper();
      return webScraper.getGoogleImages("https://www.google.com/search?tbm=isch&q=" + query);
    }

    // TODO
    System.err.println("ERROR NOT implemented " + sourceName);
    return null;
  }

  public List<String> getImplementedSources() {
    return sources.stream().map(s -> s.getSourceName()).collect(Collectors.toList());
  }

  private void initSources() {
    try {
      if (sources == null) {
        sources = SourceLoader.laodAllSources();
      }
    } catch (IOException e) {
      logger.error("Cannot laod sources " + e.getMessage() );
    }
  }
}
