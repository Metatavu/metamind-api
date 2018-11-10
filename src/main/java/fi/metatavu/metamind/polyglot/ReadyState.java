package fi.metatavu.metamind.polyglot;

/**
 * Consts for XMLHttpRequest ready state
 * 
 * @author Heikki Kurhinen
 */
public class ReadyState {

  public static final int UNSENT = 0;
  public static final int OPENED = 1;
  public static final int HEADERS_RECEIVED = 2;
  public static final int LOADING = 3;
  public static final int DONE = 4;

  private ReadyState() {
    //Hide implicit public constructor
  }
}