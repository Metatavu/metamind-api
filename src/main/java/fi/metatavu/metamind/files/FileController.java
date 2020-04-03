package fi.metatavu.metamind.files;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public interface FileController {
  InputStream getFile(String fileName);

  void deleteFile(String fileName);

  void addFile(InputStream file, String fileName, String contentType) throws IOException;

}
