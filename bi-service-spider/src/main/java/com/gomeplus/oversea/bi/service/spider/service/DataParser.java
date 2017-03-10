/***
 * The abstract class to parse the data.
 * Please refer to the instructions.txt
 *
 * @author vijay.v@flipkart.com
 * @version 1.0
 * Copyright (c) Flipkart India Pvt. Ltd.
 */
package com.gomeplus.oversea.bi.service.spider.service;
import java.net.*;
import java.io.*;

import com.gomeplus.oversea.bi.service.spider.exception.AffiliateAPIException;

public abstract class DataParser {

    /***
     * queries the URL and gets back the response as string.
     * @param urlString
     * @return
     * @throws AffiliateAPIException, with different error codes explained.
     */
    public String queryService(String urlString) throws AffiliateAPIException {

        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Fk-Affiliate-Token", getAffiliateToken());
            con.setRequestProperty("Fk-Affiliate-Id", getAffiliateId());

            int status = con.getResponseCode();

            switch(status) {

                case HttpURLConnection.HTTP_GONE:
                    // The timestamp is expired.
                    throw new AffiliateAPIException("URL expired");

                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    // The API Token or the Tracking ID is invalid.
                    throw new AffiliateAPIException("API Token or Affiliate Tracking ID invalid.");

                case HttpURLConnection.HTTP_FORBIDDEN:
                    // Tampered URL, i.e., there is a signature mismatch.
                    // The URL contents are modified from the originally returned value.
                    throw new AffiliateAPIException("Tampered URL - The URL contents are modified from the originally returned value");

                case HttpURLConnection.HTTP_OK:

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    return response.toString();

                default:
                    throw new AffiliateAPIException("Connection error with the Affiliate API service: HTTP/" + status);
            }
        }
        catch(MalformedURLException mfe) {
        }
        catch(IOException ioe) {
        }

        return "";
    }

    /***
     *  It gets the API Directory information (A list of categories and the corresponding URLs) from the API service
     *  and stores it locally.
     * @return true if initialization is successful.
     * @throws AffiliateAPIException
    
    public abstract boolean initializeProductDirectory() throws AffiliateAPIException;

    /***
     *
     * @return the locally stored product directory information (A list of categories and the corresponding URLs).
     * Originally updated using initializeProductDirectory() and it should be updated again if the URLs are expired.
    
    abstract Map<String, String> getProductDirectory();

    /***
     *
     * @param category
     * @return list of products for the given categery from the API service.
     * @throws AffiliateAPIException
     
    public abstract List<ProductInfo> getProductList(String category) throws AffiliateAPIException;

    // Affiliate related information. */
    public abstract String getAffiliateId();
    public abstract String getAffiliateToken();
    
}