package Application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SourceLoader {

  private static final String SOURCEFILE_EXTENSION = ".json";
  private static final String SOURCES_FOLDER = "/sources/";

  
  public static List<SourceObj> laodAllSources() throws IOException {
    
    URL url = SourceLoader.class.getClass().getResource(SOURCES_FOLDER);
    String path = url.getPath();
    
    File folder = new File(path);
    File[] listOfFiles = folder.listFiles();

    List<SourceObj> sources = new ArrayList<>();
    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile() && listOfFiles[i].getName().toLowerCase().endsWith(SOURCEFILE_EXTENSION)) {
        InputStream resourceAsStream = new FileInputStream(listOfFiles[i]);
        String json = IOUtils.toString(resourceAsStream, "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper(); 
        SourceObj source = objectMapper.readValue(json, SourceObj.class); 
        sources.add(source);
      }
    }
    
    return sources;
  }
}
