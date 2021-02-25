package fi.metatavu.metamind.server.rest;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter (urlPatterns = "/v2/*")
public class AccessControlAllowOriginFilter implements Filter {
  
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
     // Nothing to init
  }
  
  /**
   * Adds allows Access-Control-Allow-Origin: * header to all requests
   * 
   * @param request request
   * @param response response
   * @param chain filter chain
   * @throws IOException IOException
   * @throws ServletException  ServletException
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (response instanceof HttpServletResponse) { 
      HttpServletResponse httpServletResponse = (HttpServletResponse) response;
      httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
      httpServletResponse.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
      httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
    }
    
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    // Nothing to destroy
  }
  
}
