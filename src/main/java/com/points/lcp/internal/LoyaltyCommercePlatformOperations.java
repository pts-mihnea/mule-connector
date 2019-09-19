package com.points.lcp.internal;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;


/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class LoyaltyCommercePlatformOperations {
	

  /**
   * Example of an operation that uses the configuration and a connection instance to perform some action.
   */
  @MediaType(value = ANY, strict = false)
  public String validateMemberAccount(@Config LoyaltyCommercePlatformConfiguration configuration, @Connection LoyaltyCommercePlatformConnection connection){
    return "Using Configuration [" + configuration.getConfigId() + "] with Connection id [" + connection.getServer() + "]";
  }
  
  
  /**
   * Example of a simple operation that receives a string parameter and returns a new string message that will be set on the payload.
   */
  @MediaType(value = ANY, strict = false)
  public String debitMemberAccount(String memberValidation, String pic, int amount) {
    return buildHelloMessage(memberValidation);
  }

  /**
   * Private Methods are not exposed as operations
   */
  private String buildHelloMessage(String person) {
    return "Hello " + person + "!!!";
  }
}
