package fi.metatavu.metamind.images;


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

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.metamind.persistence.dao.KnotDAO;
import fi.metatavu.metamind.persistence.models.Knot;


@RequestScoped
@MultipartConfig
@WebServlet (urlPatterns = { "/images", "/images/*" })
public class ImageServlet extends HttpServlet{

  private static final long serialVersionUID = -5737983422302604984L;

  @Inject
  private Logger logger;
  
  @Inject 
  private KnotDAO knotDAO;
  
  private void setCorsHeaders(HttpServletResponse resp) {
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
    resp.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
  }
  @Override 
  protected void doGet(HttpServletRequest req, HttpServletResponse resp){
    System.out.println("Get request");
  }
  
  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    System.out.println("Put request");
    try {
      Part file = req.getPart("file");
      if (file == null) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
      
      setCorsHeaders(resp);
      String contentType = file.getContentType();
      UUID knotId = UUID.fromString(req.getPart("knotId").toString());     
      Knot knot = knotDAO.findById(knotId);
      UUID imageFileId = UUID.randomUUID();
      String extension = file.getSubmittedFileName().substring(file.getSubmittedFileName().lastIndexOf("."));
      
      String imageURL = "images/"+imageFileId.toString()+extension;
      
      knotDAO.updateImage(knot, imageURL, knot.getLastModifierId());   
      InputStream inputStream = file.getInputStream();
      FileWriter writer = new FileWriter(imageURL);
      
      while(inputStream.available()>0) {
        writer.write(inputStream.read());
      }
      writer.close();
      
      Map<String, String> result = new HashMap<>();
      result.put("fileName", imageFileId.toString()+extension);

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
  
  
  

}
