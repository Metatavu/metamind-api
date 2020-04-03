package fi.metatavu.metamind.test.functional;

import fi.metatavu.metamind.client.model.KnotType;
import fi.metatavu.metamind.client.model.Story;
import fi.metatavu.metamind.test.functional.builder.TestBuilder;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Test class for uploading Knot images
 */
public class KnotImageTestIT {
  @Test
  public void KnotImageTest() throws Exception{
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "Test story", "hint");
      HttpURLConnection testImageConnection = (HttpURLConnection) (new URL("https://thumbor.forbes.com/thumbor/960x0/https%3A%2F%2Fblogs-images.forbes.com%2Fmeggentaylor%2Ffiles%2F2018%2F04%2FMt-Taranaki-1200x800.jpg")).openConnection();
      testImageConnection.setRequestMethod("GET");

      HttpURLConnection putImageConnection = (HttpURLConnection) (new URL("http://localhost:1234/v2/images/")).openConnection();
      putImageConnection.setRequestMethod("PUT");
      putImageConnection.setDoOutput(true);
      String boundary = UUID.randomUUID().toString();
      putImageConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

      UUID knotId = builder.admin().knots().create(story, KnotType.TEXT, "Test knot", "Test content", 10.0, 100.0).getId();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      IOUtils.copy(testImageConnection.getInputStream(), outputStream);
      byte[] imageBytes = outputStream.toByteArray();

      DataOutputStream request = new DataOutputStream(putImageConnection.getOutputStream());

      request.writeBytes("--" + boundary + "\r\n");
      request.writeBytes("Content-Disposition: form-data; name=\"knotId\"\r\n\r\n");
      request.writeBytes(knotId.toString() + "\r\n");

      request.writeBytes("--" + boundary + "\r\n");
      request.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"" + "randomname.jpg"+ "\r\n" + "Content-Type: image/jpeg"  + "\"\r\n\r\n");
      request.write(imageBytes);
      request.writeBytes("\r\n");

      request.writeBytes("--" + boundary + "--\r\n");
      request.flush();

      String putImageResponse = readFromConnection(putImageConnection);
      assertNotNull(putImageResponse);
      JSONObject jsonObject = new JSONObject(putImageResponse);
      String fileName = jsonObject.getString("filename");
      assertEquals(knotId + ".jpg", fileName);
      HttpURLConnection getImageConnection = (HttpURLConnection) (new URL("http://localhost:1234/v2/images/"+fileName)).openConnection();
      getImageConnection.setRequestMethod("GET");
      ByteArrayOutputStream anotherOutputStream = new ByteArrayOutputStream();
      IOUtils.copy(getImageConnection.getInputStream(), anotherOutputStream);
      byte[] newImageBytes = anotherOutputStream.toByteArray();
      assertEquals(imageBytes.length, newImageBytes.length);
      assertArrayEquals(imageBytes, newImageBytes);
    }
  }

  String readFromConnection(HttpURLConnection connection) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(
            connection.getInputStream()));
    String inputLine;
    StringBuilder response = new StringBuilder();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    return response.toString();
  }
}
