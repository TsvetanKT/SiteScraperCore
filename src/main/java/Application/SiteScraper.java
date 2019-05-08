package Application;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import Application.SourceObj.SourceExtractType;

public class SiteScraper {
  
  Log logger = LogFactory.getLog(SiteScraper.class);

  private List<SourceObj> sources;
  
  public SiteScraper() {
    initSources();
  }
  
  public List<String> getIteration(String sourceName, String query, int iteration) {
    
    if (query == null || query.isEmpty()) {
      return null;
    }

    SourceObj sourceObj = sources.stream().filter(s -> s.getSourceName().equals(sourceName)).findFirst().get();
    
    if (sourceObj == null) {
      // Throw exception
      return null;
    }
    
    IScraper scraper = null;
    switch (sourceObj.getType()) {
      case WEB:
        scraper = new WebScraper();
        break;
      case API:
        scraper = new ApiScraper();
        break;
      default:
        throw new NotImplementedException("Not implemented extract type " + sourceObj.getType());
    }

    return scraper.getIteration(sourceObj, query, iteration);
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
      logger.error("Cannot laod sources " + e.getMessage());
    }
  }
}
