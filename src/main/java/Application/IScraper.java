package Application;

import java.util.List;

public interface IScraper {
  List<String> getIteration(SourceObj sourceObj, String query, int iteration);
}
