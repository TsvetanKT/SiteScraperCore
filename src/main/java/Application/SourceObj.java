package Application;

public class SourceObj {
  public String sourceName;
  public String type;
  public String urlQueryString;
  public String parentXpath;
  public String elementParser;
  public String elementValue;

  public String getSourceName() {
    return sourceName;
  }

  public void setSourceName(String sourceName) {
    this.sourceName = sourceName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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
