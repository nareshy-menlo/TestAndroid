package android.template.com.defaultprojecttemplate.network;

import android.os.Build;
import android.template.com.defaultprojecttemplate.BuildConfig;
import android.template.com.defaultprojecttemplate.network.deserializer.GsonUTCDateAdapter;
import android.template.com.defaultprojecttemplate.utils.Constants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.Date;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static ApiService userService;
    private static final int READ_TIMEOUT = 60;
    private static final int CONNECTION_TIMEOUT = 60;

    private static final String API_BASE_URL = BuildConfig.BASE_URI;

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static Retrofit getRetrofit(){
        return builder.client(getOkClient()).build();
    }

    private static <S> S createService(Class<S> serviceClass) {
        return getRetrofit().create(serviceClass);
    }

    public static ApiService getInstance(){
        if (userService == null) {
            userService = createService(ApiService.class);
        }
        return userService;
    }

    private static OkHttpClient getOkClient(){

        ConnectionSpec spec = new
                ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT ? TlsVersion.TLS_1_2:TlsVersion.TLS_1_0)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_256_GCM_SHA384 ,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA ,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384 ,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA256 ,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA ,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA256 ,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA )
                .build();


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.followSslRedirects(true);
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            // Customize the request
            Request.Builder requestBuilder = original.newBuilder()
                    .header(Constants.HEADER_CONTENT_TYPE, Constants.CONTENT_TYPE)
                    .addHeader(Constants.HEADER_OS_TYPE, Constants.OS_TYPE)
                    .method(original.method(), original.body());

            return chain.proceed(requestBuilder.build());
        });
        httpClient.connectionSpecs(Collections.singletonList(spec));
        httpClient.addInterceptor(addLoggingInterceptor());
        httpClient.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        httpClient.writeTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        httpClient.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        //httpClient.retryOnConnectionFailure(true);


        return httpClient.build();
    }

    /*This is used for internal testing when get sslHandshake exception
    while making network request*/
    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            httpClient.hostnameVerifier((hostname, session) -> true);

            httpClient.followSslRedirects(true);
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();

                // Customize the request
                Request.Builder requestBuilder = original.newBuilder()
                        .header(Constants.HEADER_CONTENT_TYPE, Constants.CONTENT_TYPE)
                        .addHeader(Constants.HEADER_OS_TYPE, Constants.OS_TYPE)
                        .method(original.method(), original.body());

                return chain.proceed(requestBuilder.build());
            });
            httpClient.addInterceptor(addLoggingInterceptor());
            httpClient.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
            httpClient.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);

            return httpClient.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Interceptor addLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            // development build
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            // production build
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }
        return logging;
    }

    private static GsonConverterFactory buildGsonConverterForReview() {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.serializeSpecialFloatingPointValues();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonUTCDateAdapter());

        Gson myGson = gsonBuilder.create();
        return GsonConverterFactory.create(myGson);
    }
}