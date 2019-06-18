package no.publishers.integration;

import no.publishers.TestData;
import no.publishers.testcategories.IntegrationTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static no.publishers.TestData.*;

@Category(IntegrationTest.class)
public class PublisherApi {
    private static File testComposeFile = createTmpComposeFile();
    private final static Logger logger = LoggerFactory.getLogger(PublisherApi.class);
    private static Slf4jLogConsumer mongoLog = new Slf4jLogConsumer(logger).withPrefix("mongo-container");
    private static Slf4jLogConsumer apiLog = new Slf4jLogConsumer(logger).withPrefix("api-container");
    private static DockerComposeContainer compose;

    private static String publisherId0;
    private static String publisherId1;
    private static String publisherId2;

    @BeforeClass
    public static void setup() throws Exception {
        if (testComposeFile != null && testComposeFile.exists()) {
            compose = new DockerComposeContainer<>(testComposeFile)
                .withExposedService(MONGO_SERVICE_NAME, MONGO_PORT, Wait.forListeningPort())
                .withExposedService(API_SERVICE_NAME, API_PORT, Wait.forHttp("/ready").forStatusCode(200))
                .withTailChildContainers(true)
                .withPull(false)
                .withLocalCompose(true)
                .withLogConsumer(MONGO_SERVICE_NAME, mongoLog)
                .withLogConsumer(API_SERVICE_NAME, apiLog);

            compose.start();
        } else {
            logger.debug("Unable to start containers, missing test-compose.yml");
        }

        publisherId2 = postJson(buildPublishersURL("/publisher/create"), TestData.createPublisherJson(TestData.CREATE_PUBLISHER_2));
        publisherId0 = postJson(buildPublishersURL("/publisher/create"), TestData.createPublisherJson(TestData.CREATE_PUBLISHER_0));
        publisherId1 = postJson(buildPublishersURL("/publisher/create"), TestData.createPublisherJson(TestData.CREATE_PUBLISHER_1));
    }

    @AfterClass
    public static void teardown() {
        if (testComposeFile != null && testComposeFile.exists()) {
            compose.stop();

            logger.debug("Delete temporary test-compose.yml: " + testComposeFile.delete());
        } else {
            logger.debug("Teardown skipped, missing test-compose.yml");
        }
    }

    @Test
    public void pingTest() throws Exception {
        String response = simpleGet(buildPublishersURL("/ping"));
        Assert.assertEquals("RegnskapAPI is available", "pong", response);
    }

    @Test
    public void getById() throws Exception {
        String response0 = simpleGet(buildPublishersURL("/publishers/" + publisherId0));
        String response1 = simpleGet(buildPublishersURL("/publishers/" + publisherId1));
        String response2 = simpleGet(buildPublishersURL("/publishers/" + publisherId2));

        Assert.assertEquals(publisherReturnJson(TestData.CREATE_PUBLISHER_0, publisherId0), response0);
        Assert.assertEquals(publisherReturnJson(TestData.CREATE_PUBLISHER_1, publisherId1), response1);
        Assert.assertEquals(publisherReturnJson(TestData.CREATE_PUBLISHER_2, publisherId2), response2);
    }

    @Test
    public void getByName() throws Exception {
        String response = simpleGet(buildPublishersURL("/publishers?q=name"));

        String expectedResponse = "[" + publisherReturnJson(TestData.CREATE_PUBLISHER_0, publisherId0) + "," +
            publisherReturnJson(TestData.CREATE_PUBLISHER_1, publisherId1) + "," +
            publisherReturnJson(TestData.CREATE_PUBLISHER_2, publisherId2) + "]";

        Assert.assertEquals(expectedResponse, response);
    }

    private static String simpleGet(URL address) throws Exception {
        HttpURLConnection con = (HttpURLConnection) address.openConnection();
        con.setRequestMethod("GET");

        StringBuilder content = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            reader.lines().forEach(line -> content.append(line));
        }

        return content.toString();
    }

    private static URL buildPublishersURL(String address) throws MalformedURLException {
        return new URL("http", compose.getServiceHost(API_SERVICE_NAME, API_PORT), compose.getServicePort(API_SERVICE_NAME, API_PORT), address);
    }

    private static File createTmpComposeFile() {
        try {
            File tmpComposeFile = File.createTempFile("test-compose", ".yml");
            InputStream testCompseStream = IOUtils.toInputStream(TEST_COMPOSE, StandardCharsets.UTF_8);

            try (FileOutputStream outputStream = new FileOutputStream(tmpComposeFile)) {
                int read;
                byte[] bytes = new byte[1024];

                while ((read = testCompseStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            }

            return tmpComposeFile;
        } catch (IOException e) {
            return null;
        }
    }

    private static String postJson(URL address, String jsonData) throws Exception {
        HttpURLConnection con = (HttpURLConnection) address.openConnection();
        con.setRequestMethod("POST");
        con.addRequestProperty("Content-Type", "application/json");
        con.addRequestProperty("Content-Length", Integer.toString(jsonData.length()));
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(jsonData);
        wr.flush();
        wr.close();

        StringBuilder content = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            reader.lines().forEach(line -> content.append(line));
        }

        return content.toString();
    }
}
