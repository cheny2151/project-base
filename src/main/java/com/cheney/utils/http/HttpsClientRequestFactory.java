package com.cheney.utils.http;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.HttpURLConnection;
import java.security.cert.X509Certificate;

/**
 * https client factory
 *
 * @author cheney
 * @date 2019-10-28
 */
public class HttpsClientRequestFactory extends SimpleClientHttpRequestFactory {

    @Override
    protected void prepareConnection(HttpURLConnection connection, String httpMethod) {
        try {
            if (!(connection instanceof HttpsURLConnection)) {
                throw new RuntimeException("An instance of HttpsURLConnection is expected");
            }

            HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;

            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }

                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            httpsConnection.setSSLSocketFactory(sslContext.getSocketFactory());

            httpsConnection.setHostnameVerifier((s, sslSession) -> true);

            super.prepareConnection(httpsConnection, httpMethod);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
