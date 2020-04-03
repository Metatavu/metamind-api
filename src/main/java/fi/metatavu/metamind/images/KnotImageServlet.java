package fi.metatavu.metamind.images;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.metatavu.metamind.files.FileController;
import fi.metatavu.metamind.files.AwsFileController;
import fi.metatavu.metamind.files.FileStorageException;
import fi.metatavu.metamind.files.TestFileController;
import fi.metatavu.metamind.persistence.models.Knot;
import fi.metatavu.metamind.story.StoryController;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequestScoped
@MultipartConfig
@WebServlet (urlPatterns = {"/v2/images", "/v2/images/*"})
public class KnotImageServlet extends HttpServlet {
  @Inject
  private StoryController storyController;
  @Inject
  private Logger logger;

  private FileController getFileController () {
    if (System.getenv("runmode").equals("TEST")) {
      return new TestFileController();
    }

    try {
      return new AwsFileController();
    } catch (FileStorageException e) {
      logger.error("File storage exception", e);
      return null;
    }

  }

  private void setCorsHeaders(HttpServletResponse response) {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    response.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
  }

  @Override
  protected void doPut(HttpServletRequest request, HttpServletResponse response) {
    try {
      setCorsHeaders(response);

      Part knotIdPart = request.getPart("knotId");
      Part image = request.getPart("image");
      String contentType = image.getContentType();

      if (knotIdPart == null || !contentType.startsWith("image")) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }

      UUID knotId = UUID.fromString(convertStreamToString(knotIdPart.getInputStream()));
      Knot knot = storyController.findKnotById(knotId);

      if (knot == null) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return;
      }

      String extension = image.getSubmittedFileName().substring(image.getSubmittedFileName().lastIndexOf("."));
      String fileName = knotId.toString()+extension;

      FileController fileController = getFileController();
      if (fileController == null) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        logger.error("Failed to initialize fileController.");
        return;
      }

      fileController.addFile(image.getInputStream(), fileName, request.getContentType());
      Map<String, String> result = new HashMap<>();
      result.put("filename", fileName);

      response.setContentType("application/json");
      ServletOutputStream outputStream = response.getOutputStream();

      try {
        (new ObjectMapper()).writeValue(outputStream, result);
      } finally {
        outputStream.flush();
      }

      response.setStatus(HttpServletResponse.SC_CREATED);
    } catch (Exception e) {
      logger.error("Upload failed on internal server error", e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    setCorsHeaders(response);
    String fileName = request.getPathInfo();
    FileController fileController = getFileController();

    if (fileController == null) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      logger.error("Failed to initialize fileController.");
      return;
    }

    InputStream inputStream = fileController.getFile(fileName);

    if (inputStream == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    response.setStatus(HttpServletResponse.SC_FOUND);

    try {
      ServletOutputStream outputStream = response.getOutputStream();
      try {
        IOUtils.copy(inputStream, outputStream);
      } finally {
        inputStream.close();
        outputStream.flush();
      }
    } catch (Exception e) {
      logger.error("Failed to send file", e);
    }
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
    setCorsHeaders(response);
    String fileName = request.getPathInfo();
    FileController fileController = getFileController();

    if (fileController == null) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      logger.error("Failed to initialize fileController.");
      return;
    }

    fileController.deleteFile(fileName);
    response.setStatus(HttpServletResponse.SC_NO_CONTENT);

  }

  private String convertStreamToString(InputStream inputStream) throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    String line = null;
    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()))) {
      while ((line = bufferedReader.readLine()) != null) {
        stringBuilder.append(line);
      }
    }
    return stringBuilder.toString();
  }
}
