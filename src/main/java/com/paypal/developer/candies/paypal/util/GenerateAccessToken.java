/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paypal.developer.candies.paypal.util;

import com.paypal.core.ConfigManager;
import com.paypal.core.rest.OAuthTokenCredential;
import com.paypal.core.rest.PayPalRESTException;

/**
 *
 * @author jprestes
 */

public class GenerateAccessToken    {
    
    public static String getAccessToken() throws PayPalRESTException {
        // ###AccessToken
        // Retrieve the access token from
        // OAuthTokenCredential by passing in
        // ClientID and ClientSecret
        String clientID = ConfigManager.getInstance().getValue("clientID");
        String clientSecret = ConfigManager.getInstance().getValue("clientSecret");

        return new OAuthTokenCredential(clientID, clientSecret).getAccessToken();
    }
    
}

