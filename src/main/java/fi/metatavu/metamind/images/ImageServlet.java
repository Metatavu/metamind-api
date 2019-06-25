package fi.metatavu.metamind.images;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.metamind.persistence.dao.KnotDAO;
import fi.metatavu.metamind.persistence.models.Knot;


@RequestScoped
@MultipartConfig
@WebServlet (urlPatterns = { "/v2/images", "/v2/images/*" })
public class ImageServlet extends HttpServlet{

  private static final long serialVersionUID = -5737983422302604984L;
  @Inject
  private Logger logger;
  
  @Inject 
  private KnotDAO knotDAO;
  
  private void setCorsHeaders(HttpServletResponse resp) {
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    resp.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
  }
  @Override 
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
    setCorsHeaders(resp);
    String fileURL = req.getPathInfo();
    File file = new File("images/"+fileURL);
    if(!file.exists()) {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    resp.setStatus(HttpServletResponse.SC_FOUND);
    try {
      ServletOutputStream servletOutputStream = resp.getOutputStream();
      try (InputStream data = new FileInputStream(file)) {
        IOUtils.copy(data, servletOutputStream);
      } finally {
        servletOutputStream.flush();
      }
    } catch (IOException e) {
      logger.warn("Failed to send response", e);
    }
  }
  
  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    
    try {
      Part file = req.getPart("file");
      Part idPart = req.getPart("knotId");
      String contentType = file.getContentType();
      if ( file == null || idPart == null || !contentType.startsWith("image") ) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
      
      setCorsHeaders(resp);
      
      InputStream knotIdInputStream = idPart.getInputStream();
      UUID knotId = UUID.fromString(convertStreamToString(knotIdInputStream,Charset.defaultCharset()));
      
      Knot knot = knotDAO.findById(knotId);
      
      if ( knot == null ) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
      
      String extension = file.getSubmittedFileName().substring(file.getSubmittedFileName().lastIndexOf("."));
      String fileURL = "images/"+knotId.toString()+extension;
      InputStream fileInputStream = file.getInputStream();
      
      File dir = new File("images");
      if( !dir.exists()) {
        dir.mkdir();
      }
      
      File imageFile = new File(fileURL);
      if(!imageFile.exists()) {
        imageFile.createNewFile();
      }
      
      Files.copy(fileInputStream, imageFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
      fileInputStream.close();
      
      Map<String, String> result = new HashMap<>();
      
      result.put("fileUrl", fileURL);

      resp.setContentType("application/json");
      ServletOutputStream servletOutputStream = resp.getOutputStream();
      try {
        (new ObjectMapper()).writeValue(servletOutputStream, result);
      } finally {
        servletOutputStream.flush();
      }
      resp.setStatus(HttpServletResponse.SC_CREATED);
    } catch (IOException | ServletException e) {
      logger.error("Upload failed on internal server error", e);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

    
  private String convertStreamToString(InputStream inputStream, Charset charset) throws IOException {
   
    StringBuilder stringBuilder = new StringBuilder();
    String line = null;
    
    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charset))) { 
      while ((line = bufferedReader.readLine()) != null) {
        stringBuilder.append(line);
      }
    }
   
    return stringBuilder.toString();
  }
  
  
  

}
