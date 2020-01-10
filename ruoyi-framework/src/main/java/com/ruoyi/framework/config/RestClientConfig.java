/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.framework.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.SSLContext;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author wmao
 */
@Configuration
public class RestClientConfig {

    private static final Logger LOG = LoggerFactory.getLogger(RestClientConfig.class);

    @Value("${http-pool.max-total}")
    private Integer maxTotal;
    @Value("${http-pool.default-max-per-route}")
    private Integer defaultMaxPerRoute;
    @Value("${http-pool.connect-timeout}")
    private Integer connectTimeout;
    @Value("${http-pool.connection-request-timeout}")
    private Integer connectionRequestTimeout;
    @Value("${http-pool.socket-timeout}")
    private Integer socketTimeout;
    @Value("${http-pool.validate-after-inactivity}")
    private Integer validateAfterInactivity;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
        List<HttpMessageConverter<?>> httpMessageConverterList = restTemplate.getMessageConverters();
        if (isNotEmpty(httpMessageConverterList)) {
            Iterator<HttpMessageConverter<?>> iterator = httpMessageConverterList.iterator();
            if (iterator.hasNext()) {
                HttpMessageConverter<?> converter = iterator.next();
                if (converter instanceof StringHttpMessageConverter) {
                    iterator.remove();
                }

            }
            httpMessageConverterList.add(new StringHttpMessageConverter(Charset.forName("utf-8")));
            FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
            FastJsonConfig fastJsonConfig = new FastJsonConfig();
            fastJsonConfig.setSerializerFeatures(
                    SerializerFeature.QuoteFieldNames,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteNullStringAsEmpty,
                    SerializerFeature.WriteNullListAsEmpty,
                    SerializerFeature.DisableCircularReferenceDetect);
            List<MediaType> mediaTypeList = new ArrayList<>();
            mediaTypeList.add(MediaType.APPLICATION_JSON_UTF8);
            mediaTypeList.add(MediaType.APPLICATION_JSON);
            fastJsonHttpMessageConverter.setSupportedMediaTypes(mediaTypeList);
            fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
            httpMessageConverterList.add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
            httpMessageConverterList.add(1, fastJsonHttpMessageConverter);
            restTemplate.setMessageConverters(httpMessageConverterList);
        }
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public HttpClient httpClient() {
        HttpClient httpClient = null;
        try {
            TrustStrategy acceptingTrustStrategy = (X509Certificate[] x509Certificates, String s) -> true;
            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", socketFactory)
                    .build();
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
            connectionManager.setMaxTotal(maxTotal);
            connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
            connectionManager.setValidateAfterInactivity(validateAfterInactivity);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(socketTimeout)
                    .setConnectTimeout(connectTimeout)
                    .setConnectionRequestTimeout(connectionRequestTimeout)
                    .build();
            httpClient = HttpClients.custom()
                    .setSSLSocketFactory(socketFactory)
                    .setDefaultRequestConfig(requestConfig)
                    .setConnectionManager(connectionManager)
                    .setConnectionManagerShared(true).build();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException ex) {
            LOG.error("failed to create http client:{}", Arrays.toString(ex.getStackTrace()));
        }
        return httpClient;
    }
}
