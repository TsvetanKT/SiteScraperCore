package Application;

public class SourceObj {
  private String sourceName;
  private SourceExtractType type;
  private String urlQueryString;
  private String parentXpath;
  private String elementParser;
  private String elementValue;

  public enum SourceExtractType {
    WEB,
    API;
    
    public static SourceExtractType fromString(String type) {
      for (SourceExtractType extractType : SourceExtractType.values()) {
        if (extractType.name().toLowerCase().equals(type.toLowerCase())) {
          return extractType;
        }
      }
      
      throw new IllegalArgumentException("Bad SourceExtractType: " + type);
    }
  }
  
  public String getSourceName() {
    return sourceName;
  }

  public void setSourceName(String sourceName) {
    this.sourceName = sourceName;
  }

  public SourceExtractType getType() {
    return type;
  }

  public void setType(String type) {
    this.type = SourceExtractType.fromString(type);
  }

  public String getUrlQueryString() {
    return urlQueryString;
  }

  public void setUrlQueryString(String urlQueryString) {
    this.urlQueryString = urlQueryString;
  }

  public String getParentXpath() {
    return parentXpath;
  }

  public void setParentXpath(String parentXpath) {
    this.parentXpath = parentXpath;
  }

  public String getElementParser() {
    return elementParser;
  }

  public void setElementParser(String elementParser) {
    this.elementParser = elementParser;
  }

  public String getElementValue() {
    return elementValue;
  }

  public void setElementValue(String elementValue) {
    this.elementValue = elementValue;
  }
}
