package fi.metatavu.metamind.polyglot;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class to use with fi.metatavu.metamind.polyglot javascripts to provide XMLHttpRequest
 *
 * @author Heikki Kurhinen
 */
public class XMLHttpRequest {

  private static final Logger logger = LoggerFactory.getLogger(XMLHttpRequest.class);

  /**
   * Public properties modifiable from javascript
   */
  public Value onreadystatechange;
  public int readyState;
  public Object response;
  public String responseType;
  public String responseText;
  //TODO: responseUrl
  //TODO: responseXML
  public int status;
  public String statusText;
  public long timeout;
  public Value ontimeout;
  //TODO: upload
  //TODO: withCredentials

  private HttpUriRequest request;
  private HttpResponse httpResponse;
  private String httpMethod;
  private String targetUrl;
  private String username;
  private String password;
  private Map<String, String> requestHeaders;

  public XMLHttpRequest() {
    this.status = 0;
    this.readyState = ReadyState.UNSENT;
    this.requestHeaders = new HashMap<>();
  }

  /**
   * Aborts the request if it has already been sent. 
   */
  public void abort() {
    if (request != null) {
      request.abort();
      this.status = 0;
      this.statusText = null;
      this.httpResponse = null;
      this.requestHeaders = new HashMap<>();
      readyStateChange(ReadyState.UNSENT);
    }
  }

  /**
   * Returns all the response headers, separated by CRLF, as a string,
   * or returns null if no response has been received.
   * 
   * @return Response headers separated by CRLF or null
   */
  public String getAllResponseHeaders() {
    if (httpResponse == null) {
      return null;
    }

    List<String> headers = Arrays.asList(httpResponse.getAllHeaders()).stream().map(header -> {
      return String.format("%s: %s", header.getName(), header.getValue());
    }).collect(Collectors.toList());

    return StringUtils.join(headers, "\r\n");
  }

  /**
   *  Returns the string containing the text of of a particular header's value. 
   *  If there are multiple response headers with the same name,
   *  then their values are returned as a single concatenated string,
   *  where each value is separated from the previous one by a pair of comma and space. 
   * 
   * @param name the name of the header you want to return the text value of.
   * @return header's text value, or null if either the response has not yet been received or the header doesn't exist in the response
   */
  public String getResponseHeader(String name) {
    if (httpResponse == null || !httpResponse.containsHeader(name)) {
      return null;
    }

    List<String> headerValues = Arrays.asList(httpResponse.getHeaders(name))
      .stream()
      .map(Header::getValue)
      .collect(Collectors.toList());

    return StringUtils.join(headerValues, ", ");
  }

  /**
   * Initializes a newly-created request, or re-initializes an existing one.
   * 
   * @param httpMethod The HTTP request method to use, such as "GET", "POST", "PUT", "DELETE"
   * @param url The URL to send the request to.
   */
  public void open(String httpMethod, String url) {
    this.open(httpMethod, url, false, null, null);
  }

  /**
   * Initializes a newly-created request, or re-initializes an existing one.
   * 
   * @param httpMethod The HTTP request method to use, such as "GET", "POST", "PUT", "DELETE"
   * @param url The URL to send the request to.
   * @param async Whether or not to perform the operation asynchronously. (Not implemented yet)
   */
  public void open(String httpMethod, String url, boolean async) {
    this.open(httpMethod, url, async, null, null);
  }

  /**
   * Initializes a newly-created request, or re-initializes an existing one.
   * 
   * @param httpMethod The HTTP request method to use, such as "GET", "POST", "PUT", "DELETE"
   * @param url The URL to send the request to.
   * @param async Whether or not to perform the operation asynchronously. (Not implemented yet)
   * @param username Username to use for authentication purposes
   */
  public void open(String httpMethod, String url, boolean async, String username) {
    this.open(httpMethod, url, async, username, null);
  }

  /**
   * Initializes a newly-created request, or re-initializes an existing one.
   * 
   * @param httpMethod The HTTP request method to use, such as "GET", "POST", "PUT", "DELETE"
   * @param url The URL to send the request to.
   * @param async Whether or not to perform the operation asynchronously. (Not implemented yet)
   * @param username Username to use for authentication purposes
   * @param password Password to use for authentication purposes
   */
  public void open(String httpMethod, String url, boolean async, String username, String password) {
    if (request != null) {
      this.abort();
    }

    this.username = username;
    this.password = password;
    this.httpMethod = httpMethod;
    this.targetUrl = url;
    this.readyStateChange(ReadyState.OPENED);
  }

  /**
   * specifies a MIME type other than the one provided by the server
   * to be used instead when interpreting the data being transferred in a request. 
   */
  public void overrideMimeType() {
    // Not implemented yet
  }

  /**
   * Sends the request to the server.
   */
  public void send() {
    sendRequest(null); 
  }

  /**
   * Sends the request to the server.
   * 
   * @param data A body of string data to be sent in the request
   */
  public void send(String data) {
    if (StringUtils.isBlank(data)) {
      this.sendRequest(null);
      return;
    }

    EntityBuilder builder = EntityBuilder.create();
    builder.setText(data);
    sendRequest(builder.build());
  }

  /**
   * Sends the request to the server.
   * 
   * @param data A body of binary data to be sent in the request
   */
  public void send(byte[] data) {
    if (data == null) {
      this.sendRequest(null);
      return;
    }

    EntityBuilder builder = EntityBuilder.create();
    builder.setBinary(data);
    sendRequest(builder.build());
  }

  /**
   * sets the value of an HTTP request header. When using setRequestHeader(),
   * you must call it after calling open(), but before calling send().
   * If this method is called several times with the same header,
   * the values are merged into one single request header.
   * 
   * @param header The name of the header whose value is to be set.
   * @param value The value to set as the body of the header.
   */
  public void setRequestHeader(String header, String value) {
    String headerBody = value;
    if (this.requestHeaders.containsKey(header)) {
      String previousValue = requestHeaders.get(header);
      headerBody = String.format("%s%s", previousValue, value);
    }

    this.requestHeaders.put(header, headerBody);
  }

  /**
   * Sends the request to the server
   * 
   * @param entity entity containing data to be sent with the request
   */
  private void sendRequest(HttpEntity entity) {
    if (StringUtils.isBlank(targetUrl) || StringUtils.isBlank(httpMethod)) {
      return;
    }

    try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
      RequestBuilder builder = RequestBuilder.create(httpMethod);
      builder.setUri(targetUrl);
      builder.setEntity(entity);
      this.requestHeaders.keySet().stream().forEach(headerName -> {
        builder.addHeader(headerName, this.requestHeaders.get(headerName));
      });

      if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
        builder.addHeader(createAuthorizationHeader(username, password));
      }

      request = builder.build();
      httpResponse = client.execute(request);
      status = httpResponse.getStatusLine().getStatusCode();
      statusText = httpResponse.getStatusLine().getReasonPhrase();

      HttpEntity httpEntity = httpResponse.getEntity();
      if (httpEntity != null) {
        try (InputStream body = httpEntity.getContent()) {
          responseText = IOUtils.toString(body, StandardCharsets.UTF_8);
          if (StringUtils.isBlank(responseType) || responseType.equals(ResponseType.TEXT)) {
            response = responseText;
          } else {
            switch (responseType) {
              case ResponseType.JSON:
                Context context = Context.getCurrent();
                response = context.eval("js", String.format("JSON.parse('%s');", responseText));
              break;
              case ResponseType.BLOB:
              case ResponseType.ARRAY_BUFFER:
                response = IOUtils.toByteArray(body);
              break;
              case ResponseType.DOCUMENT:
                //TODO: not implemented yet
                response = null;
              default:
                response = null;
              break;
            }
          }
        }
      }
    } catch(Exception e) {
      response = null;
      responseText = null;
      logger.error("Error executing XMLHttpRequest", e);
    } finally {
      readyStateChange(ReadyState.DONE);
    }
  }

  /**
   * Executes onreadychange callback after ready state changes
   * 
   * @param readyState new ready state
   */
  private void readyStateChange(int readyState) {
    this.readyState = readyState;
    if (onreadystatechange != null && onreadystatechange.canExecute()) {
      onreadystatechange.execute();
    }
  }

  /**
   * Constructs Basic authorization header
   * 
   * @param user username
   * @param pass password
   * @return Base64 encoded authorization header
   */
  private Header createAuthorizationHeader(String user, String pass) {
    String auth = String.format("%s:%s", user, pass);
    String authHeader = String.format("Basic %s", Base64.getEncoder().encodeToString(
      auth.getBytes(StandardCharsets.UTF_8)
    ));
    return new BasicHeader(HttpHeaders.AUTHORIZATION, authHeader);
  }

}