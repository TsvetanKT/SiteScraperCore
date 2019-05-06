package Application;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"sourceName", "type", "urlQueryString", "parentXpath", "elementParser",
    "elementValue"})
public class SourcePojo {

  @JsonProperty("sourceName")
  private String sourceName;
  @JsonProperty("type")
  private String type;
  @JsonProperty("urlQueryString")
  private String urlQueryString;
  @JsonProperty("parentXpath")
  private String parentXpath;
  @JsonProperty("elementParser")
  private String elementParser;
  @JsonProperty("elementValue")
  private String elementValue;

  @JsonProperty("sourceName")
  public String getSourceName() {
    return sourceName;
  }

  @JsonProperty("sourceName")
  public void setSourceName(String sourceName) {
    this.sourceName = sourceName;
  }

  @JsonProperty("type")
  public String getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(String type) {
    this.type = type;
  }

  @JsonProperty("urlQueryString")
  public String getUrlQueryString() {
    return urlQueryString;
  }

  @JsonProperty("urlQueryString")
  public void setUrlQueryString(String urlQueryString) {
    this.urlQueryString = urlQueryString;
  }

  @JsonProperty("parentXpath")
  public String getParentXpath() {
    return parentXpath;
  }

  @JsonProperty("parentXpath")
  public void setParentXpath(String parentXpath) {
    this.parentXpath = parentXpath;
  }

  @JsonProperty("elementParser")
  public String getElementParser() {
    return elementParser;
  }

  @JsonProperty("elementParser")
  public void setElementParser(String elementParser) {
    this.elementParser = elementParser;
  }

  @JsonProperty("elementValue")
  public String getElementValue() {
    return elementValue;
  }

  @JsonProperty("elementValue")
  public void setElementValue(String elementValue) {
    this.elementValue = elementValue;
  }

}
