package fi.metatavu.metamind.polyglot;

/**
 * Consts for XMLHttpRequest response type
 * 
 * @author Heikki Kurhinen
 */
public class ResponseType {

  public static final String TEXT = "text";
  public static final String ARRAY_BUFFER = "arraybuffer";
  public static final String BLOB = "blob";
  public static final String DOCUMENT = "document";
  public static final String JSON = "json";

  private ResponseType() {
    //Hide implicit public constructor
  }
}