package com.AAZl3l4.NewsService.configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;

@Configuration
public class EsConfig {

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        String username = "elastic";
        String password = "123456";

        BasicCredentialsProvider credentials = new BasicCredentialsProvider();
        credentials.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(mapper);

        // 注意：http 而不是 https
        RestClient restClient = RestClient.builder(
                        new HttpHost("192.168.188.188", 9200, "http"))
                .setHttpClientConfigCallback(hcb -> hcb
                        .setDefaultCredentialsProvider(credentials))
                .build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, jsonpMapper);
        return new ElasticsearchClient(transport);
    }
}