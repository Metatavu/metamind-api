package fi.metatavu.metamind.files;

public class FileStorageException extends Exception {

  /**
   * Constructor
   *
   * @param reason exception reason
   */
  public FileStorageException(String reason) {
    super(reason);
  }

  /**
   * Constructor
   *
   * @param cause exception cause
   */
  public FileStorageException(Throwable cause) {
    super(cause);
  }

}