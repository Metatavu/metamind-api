package fi.metatavu.metamind.files;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class TestFileController implements FileController {
  @Override
  public FileInputStream getFile(String fileName) {
    File file = new File(System.getenv("IMAGE_UPLOAD_PATH")+"/"+fileName);
    try {
      return new FileInputStream(file);
    } catch (FileNotFoundException e) {
      return null;
    }
  }

  @Override
  public void deleteFile(String fileName) {

  }

  @Override
  public void addFile(InputStream file, String fileName, String contentType) throws IOException {
    File newFile = new File(System.getenv("IMAGE_UPLOAD_PATH")+"/"+fileName);
    newFile.createNewFile();
    Files.copy(file, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    file.close();
  }

}
