package fi.metatavu.metamind.images;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;


@RequestScoped
@MultipartConfig
@WebServlet (urlPatterns = { "/images", "/images/*" })
public class ImagesServlet extends HttpServlet{

  private static final long serialVersionUID = -5737983422302604984L;

  @Inject
  private Logger logger;

  private void setCorsHeaders(HttpServletResponse resp) {
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
    resp.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
  }
  

  
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      Part file = req.getPart("file");
      String knotId = req.getPart("knotId").toString();
      if (file == null) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
      
      setCorsHeaders(resp);
      String contentType = file.getContentType();
 
      InputStream inputStream = file.getInputStream();

      FileWriter writer = new FileWriter("images/"+knotId+".jpg");
      
      while(inputStream.available()>0) {
        writer.write(inputStream.read());
      }
      writer.close();
      
      Map<String, String> result = new HashMap<>();
      result.put("fileName", knotId+".jpg");

      resp.setContentType("application/json");
      ServletOutputStream servletOutputStream = resp.getOutputStream();
      try {
        (new ObjectMapper()).writeValue(servletOutputStream, result);
      } finally {
        servletOutputStream.flush();
      }
      
    } catch (IOException | ServletException e) {
      logger.error("Upload failed on internal server error", e);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    setCorsHeaders(resp);
    String fileRef = req.getPathInfo();
    if (fileRef == null) {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    getFile(resp, fileRef);
  }
  
  /**
   * Outputs file data
   * 
   * @param resp response object
   * @param fileRef file ref
   */
  private void getFile(HttpServletResponse resp, String knotId) {
    setCorsHeaders(resp);
    try {
      ServletOutputStream servletOutputStream = resp.getOutputStream();
      try (InputStream data = new FileInputStream("images/"+knotId+".jpg")) {
        IOUtils.copy(data, servletOutputStream);
      } finally {
        servletOutputStream.flush();
      }
    } catch (IOException e) {
      logger.warn("Failed to send response", e);
    }
}
  

}
