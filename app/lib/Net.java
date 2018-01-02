package lib;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Singleton
public class Net {
    @Inject
    WSClient ws;

    private static long urlTimeout = 30 * 1000l;// 30 sec might be too much for most cases!

    public WSRequest getRequester (String url, long urlTimeout, List<Pair<String, String>> extraHeaders) {
        WSRequest urlRequest = ws.url(url);
        urlRequest = urlRequest.setFollowRedirects(true).setRequestTimeout(urlTimeout)
                .setHeader("User-Agent", "Mozilla/5.0 (compatible; WigzoBot/2.1; +http://www.wigzo.com/bot.html)");

        if (null != extraHeaders) {
            for (Pair<String, String> header : extraHeaders) {
                urlRequest.setHeader(header.getLeft(), header.getRight());
            }
        }

        return urlRequest;
    }

    public JsonNode postJson(String url, JsonNode jsonNode, List<Pair<String, String>> extraHeaders) {
        return postJson(url, jsonNode, 0, extraHeaders, null);
    }

    public JsonNode postJson(String url, JsonNode jsonNode) {
        return postJson(url, jsonNode, 0, null, null);
    }

    public JsonNode postJson(String url, JsonNode jsonNode, long urlTimeout) {
        return postJson(url, jsonNode, urlTimeout, null, null);
    }

    public JsonNode postJson(String url, JsonNode jsonNode, long urlTimeout, List<Pair<String, String>> extraHeaders, Map<String, String> qsParams) {
        JsonNode response = play.libs.Json.newObject();
        if (urlTimeout == 0) {
            urlTimeout = this.urlTimeout;
        }
        WSRequest urlRequest = getRequester(url, urlTimeout, extraHeaders);
        if(qsParams!=null) {
            for (String key : qsParams.keySet()) {
                urlRequest.setQueryParameter(key, qsParams.get(key));
            }
        }
        try {
            response = urlRequest.setContentType("application/json").post(jsonNode).handle((wsResponse, ex) -> {
                if (ex != null) {
                    Logger.error("[NET] Stage completed exceptionally while posting data to " + url, ex);
                }
                System.out.println (wsResponse.getBody ().toString ());
                return wsResponse.asJson();
            }).toCompletableFuture().get(urlTimeout, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            Logger.error("[NET] Error occurred while posting data to " + url, ex);
        }
        return response;
    }

    /**
     * POST request using Gson
     *
     * @param url: request url
     * @param values: json body in Map<String, Object>
     * @param extraHeaders: extra headers if any
     * @return Parsed response as Map<String, Object>
     */
    public Map<String, Object> postJsonViaHashMap (String url, Map<String, Object> values, List<Pair<String, String>> extraHeaders) {
        Gson gson = new Gson ();
        Map<String, Object> resp = new HashMap<> ();
        String postData = gson.toJson (values);
        WSRequest urlRequest = getRequester(url, urlTimeout, extraHeaders);

        try {
            String response = urlRequest.setContentType("application/json")
                    .post(postData).handle((wsResponse, ex) -> {
                        if (ex != null) {
                            Logger.error("[NET] Stage completed exceptionally while posting data to " + url, ex);
                        }
                        String sResp = new String (wsResponse.asByteArray ());
                        return sResp;
                    }).toCompletableFuture().get(urlTimeout, TimeUnit.MILLISECONDS);
            if(StringUtils.isNotEmpty(response)) {
                resp = gson.fromJson (response, resp.getClass ());
            }
        } catch (Exception ex) {
            Logger.error("[NET] Error occurred while posting data to " + url, ex);
        }

        return resp;
    }

    public Map<String, Object> getViaHashMap (String url, Map<String, String> qsParams, List<Pair<String, String>> extraHeaders) {
        Gson gson = new Gson ();
        Map<String, Object> resp = new HashMap<> ();
        WSRequest urlRequest = getRequester(url, urlTimeout, extraHeaders);
        if(qsParams!=null) {
            for (String key : qsParams.keySet()) {
                urlRequest.setQueryParameter(key, qsParams.get(key));
            }
        }

        try {
            String response = urlRequest.setContentType("application/json")
                    .get().handle((wsResponse, ex) -> {
                        if (ex != null) {
                            Logger.error("[NET] Stage completed exceptionally while posting data to " + url, ex);
                        }
                        String sResp = new String (wsResponse.asByteArray ());
                        return sResp;
                    }).toCompletableFuture().get(urlTimeout, TimeUnit.MILLISECONDS);
            if(StringUtils.isNotEmpty(response)) {
                resp = gson.fromJson (response, resp.getClass ());
            }
        } catch (Exception ex) {
            Logger.error("[NET] Error occurred while sending get request to " + url, ex);
        }

        return resp;
    }

    public String postJsonString(String url, String json) {
        String response = null;
        try {
            response = getRequester(url, urlTimeout, null).setContentType("application/json")
                    .post(json).handle((wsResponse, ex) -> {
                        if (ex != null) {
                            Logger.error("[NET] Stage completed exceptionally while posting data to " + url, ex);
                        }
                        return wsResponse.getBody();
                    }).toCompletableFuture().get(Net.urlTimeout, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            Logger.error("[NET] Error occurred while sending get request to " + url, ex);
        }
        return response;
    }

    /**
     *
     * @param url
     * @param postData It will be following format - <p><code>key1=value1&key2=value2</code></p>
     * @return
     */
    public String postValues(String url, String  postData, List<Pair<String, String>> extraHeaders) {
        String response = null;
        try {
            response = getRequester(url, urlTimeout, null).setContentType("application/json")
                    .post(postData).handle((wsResponse, ex) -> {
                        if (ex != null) {
                            Logger.error("[NET] Stage completed exceptionally while posting data to " + url, ex);
                        }
                        return new String(wsResponse.asByteArray());
                    }).toCompletableFuture().get(Net.urlTimeout, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            Logger.error("[NET] Error occurred while sending get request to " + url, ex);
        }
        return response;
    }

    public JsonNode postValues(String url, String post) {
        JsonNode resp = null;
        try {
            resp = getRequester(url, Net.urlTimeout, null).post(post).handle((wsResponse, ex) -> {
                if (ex != null) {
                    Logger.error("[NET] Stage completed exceptionally while posting data to " + url, ex);
                }
                return wsResponse.asJson();
            }).toCompletableFuture().get(Net.urlTimeout, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            Logger.error("[NET] Error occurred while sending get request to " + url, ex);
        }

        return resp;
    }

    public String postValues(String url, Map<String, String> post, List<Pair<String, String>> extraHeaders) {
        String responseStr = null;
        StringBuilder postData = new StringBuilder();
        for (String k: post.keySet ()) {
            if (postData.length() != 0) postData.append('&');
            postData.append (k + "=" + post.get (k));
        }
        String postDataStr = postData.toString();

        try {
            responseStr = getRequester(url, Net.urlTimeout, extraHeaders).post(postDataStr)
                    .handle((response, ex) -> {
                        if (ex != null) {
                            Logger.error("[NET] Stage completed exceptionally while posting data to " + url, ex);
                        }
                        return new String (response.asByteArray ());
                    }).toCompletableFuture().get(Net.urlTimeout, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            Logger.error("[NET] Error occurred while sending get request to " + url, ex);
        }
        return responseStr;
    }

    public JsonNode getUrl(String url, Map<String, String> qsParams, List<Pair<String, String>> extraHeaders) {
        return getUrl (url, qsParams, extraHeaders, Net.urlTimeout);
    }


    public InputStream getInputStream(String url, Map<String, String> qsParams, List<Pair<String, String>> extraHeaders, long urlTimeout) {
        InputStream is = null;
        WSRequest urlRequest = getRequester(url, urlTimeout, extraHeaders);
        if(qsParams!=null) {
            for (String key : qsParams.keySet()) {
                urlRequest.setQueryParameter(key, qsParams.get(key));
            }
        }
        try {
            is = urlRequest.get().handleAsync(((wsResponse, ex) -> {
                if (ex != null) {
                    Logger.error("[NET] Stage completed exceptionally while posting data to " + url, ex);
                }
                return wsResponse.getBodyAsStream();
            })).toCompletableFuture().get(urlTimeout, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            Logger.error("Error while sending request, or processing response", ex);
        }

        return is;
    }

    public JsonNode getUrl(String url, Map<String, String> qsParams, List<Pair<String, String>> extraHeaders, long urlTimeout) {
        JsonNode jsonResponse = null;
        WSRequest urlRequest = getRequester(url, urlTimeout, extraHeaders);
        if(qsParams!=null) {
            for (String key : qsParams.keySet()) {
                urlRequest.setQueryParameter(key, qsParams.get(key));
            }
        }
        try {
            jsonResponse = urlRequest.get().handleAsync(((wsResponse, ex) -> {
                if (ex != null) {
                    Logger.error("[NET] Stage completed exceptionally while posting data to " + url, ex);
                }
                return wsResponse.asJson();
            })).toCompletableFuture().get(urlTimeout, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            Logger.error("Error while sending request, or processing response", ex);
        }

        return jsonResponse;
    }

    public JsonNode getUrl(String url, Map<String, String> qsParams, long urlTimeout) {
        return getUrl(url, qsParams, null, urlTimeout);
    }

    public JsonNode getUrl(String url, Map<String, String> qsParams) {
        return getUrl(url, qsParams, null, Net.urlTimeout);
    }

    public JsonNode getUrl(String url, long urlTimeout) {
        HashMap<String, String> hm = new HashMap<>();
        return getUrl(url, hm, urlTimeout);
    }

    public JsonNode getUrl(String url) {
        HashMap<String, String> hm = new HashMap<>();
        return getUrl(url, hm, Net.urlTimeout);
    }

    public byte[] getUrlContent(String url, Map<String, String> qsParams) {
        byte response[] = null;
        if (url.startsWith("//")) {
            url = "http:" + url;
        }
        WSRequest urlRequest = getRequester(url, Net.urlTimeout, null);

        for (String key: qsParams.keySet()) {
            urlRequest.setQueryParameter(key, qsParams.get(key));
        }

        try {
            response = urlRequest.get().handleAsync((wsResponse, ex) -> {
                if (ex != null) {
                    Logger.error("[NET] Stage completed exceptionally while posting data", ex);
                }
                return wsResponse.asByteArray();
            }).toCompletableFuture().get(urlTimeout, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            Logger.error("Error while sending request, or processing response", ex);
        }
        return response;
    }

    public byte[] getUrlContent(String url) {
        HashMap<String, String> hm = new HashMap<>();
        return getUrlContent(url, hm);
    }

    public JsonNode delete(String url) {
        return delete(url, Net.urlTimeout, null, null);
    }

    public JsonNode delete(String url, long urlTimeout, List<Pair<String, String>> extraHeaders, Map<String, String> qsParams) {
        JsonNode jsonNode = Json.newObject();
        if (urlTimeout == 0) {
            urlTimeout = Net.urlTimeout;
        }
        WSRequest urlRequest = getRequester(url, urlTimeout, extraHeaders);
        if(qsParams!=null) {
            for (String key : qsParams.keySet()) {
                urlRequest.setQueryParameter(key, qsParams.get(key));
            }
        }
        try {
            jsonNode = urlRequest.setContentType("application/json").delete().handleAsync((wsResponse, ex) -> {
                if (ex != null) {
                    Logger.error("[NET] Stage completed exceptionally while posting data", ex);
                }
                return wsResponse.asJson();
            }).toCompletableFuture().get(urlTimeout, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            Logger.debug("cannot send DELETE request : ", ex);
        }
        return jsonNode;
    }

    public String addQueryParam(String uri, String appendQuery) {
        try {
            URI oldUri = new URI(uri);

            String newQuery = oldUri.getQuery();
            if (newQuery == null) {
                newQuery = appendQuery;
            } else {
                newQuery += "&" + appendQuery;
            }

            URI newUri = new URI(oldUri.getScheme(), oldUri.getAuthority(),
                    oldUri.getPath(), newQuery, oldUri.getFragment());

            return newUri.toString();
/*          Removing this code as this does not work if the url has already some query params
            URL oldURL = new URL(url);
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.addParameter(paramName, paramVal);
            return oldURL.getProtocol()+ "://" + oldURL.getAuthority() + oldURL.getFile() + "?" + uriBuilder.build().getQuery();
*/
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
        return null;
    }

    public String removeQueryParams(String url) {
        try {
            URL oldURL = new URL(url);
            return oldURL.getProtocol()+ "://" + oldURL.getAuthority() + oldURL.getFile();
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
        return null;
    }

    public String swapProtocol(String url) {
        try {
            URL oldURL = new URL(url);
            if (oldURL.getProtocol().equals("http"))
                return "https"+ "://" + oldURL.getAuthority() + oldURL.getFile();
            else if (oldURL.getProtocol().equals("https"))
                return "http"+ "://" + oldURL.getAuthority() + oldURL.getFile();
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
        return null;
    }

    public static long getUrlTimeout() {
        return urlTimeout;
    }

    public boolean isUrl(String string){
        if (StringUtils.isEmpty(string)){
            return false;
        }
        try {
            URL url = new URL(string);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

}
