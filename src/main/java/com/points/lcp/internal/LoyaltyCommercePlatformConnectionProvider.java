package com.points.lcp.internal;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class (as it's name implies) provides connection instances and the funcionality to disconnect and validate those
 * connections.
 * <p>
 * All connection related parameters (values required in order to create a connection) must be
 * declared in the connection providers.
 * <p>
 * This particular example is a {@link PoolingConnectionProvider} which declares that connections resolved by this provider
 * will be pooled and reused. There are other implementations like {@link CachedConnectionProvider} which lazily creates and
 * caches connections or simply {@link ConnectionProvider} if you want a new connection each time something requires one.
 */
public class LoyaltyCommercePlatformConnectionProvider implements PoolingConnectionProvider<LoyaltyCommercePlatformConnection> {

  private final Logger LOGGER = LoggerFactory.getLogger(LoyaltyCommercePlatformConnectionProvider.class);
  
  private final HttpClient httpClient = HttpClients.createDefault();
  

 /**
  * A parameter that is always required to be configured.
  */
  @DisplayName("Loyalty Commerce Platform Server")
  @Parameter
  private String server;
  
  /**
   * A parameter that is always required to be configured.
   */
   @DisplayName("Loyalty Program Identifier")
   @Parameter
   private String lpId;
   
   /**
    * A parameter that is always required to be configured.
    */
    @DisplayName("MAC ID")
    @Parameter
    private String macId;
    
    /**
     * A parameter that is always required to be configured.
     */
     @DisplayName("MAC KEY")
     @Parameter
     private String macKey;



  @Override
  public LoyaltyCommercePlatformConnection connect() throws ConnectionException {
    return new LoyaltyCommercePlatformConnection(httpClient, server, lpId, macId, macKey);
  }

  @Override
  public void disconnect(LoyaltyCommercePlatformConnection connection) {
    try {
      connection.invalidate();
    } catch (Exception e) {
      LOGGER.error("Error while disconnecting [" + connection.getServer() + "]: " + e.getMessage(), e);
    }
  }

  @Override
  public ConnectionValidationResult validate(LoyaltyCommercePlatformConnection connection) {
    return ConnectionValidationResult.success();
  }
}
