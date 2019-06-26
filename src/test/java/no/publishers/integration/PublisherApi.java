package no.publishers.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.publishers.TestData;
import no.publishers.generated.model.Publisher;
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

    private static String publisherURI0;
    private static String publisherURI1;
    private static String publisherURI2;

    private static ObjectMapper mapper = new ObjectMapper();

    @BeforeClass
    public static void setup() throws Exception {
        if (testComposeFile != null && testComposeFile.exists()) {
            compose = new DockerComposeContainer<>(testComposeFile)
                .withExposedService(MONGO_SERVICE_NAME, MONGO_PORT, Wait.forListeningPort())
                .withExposedService(API_SERVICE_NAME, API_PORT, Wait.forHttp("/ready").forStatusCode(200))
                .withTailChildContainers(true)
                .withPull(false)
                .withLogConsumer(MONGO_SERVICE_NAME, mongoLog)
                .withLogConsumer(API_SERVICE_NAME, apiLog);

            compose.start();
        } else {
            logger.debug("Unable to start containers, missing test-compose.yml");
        }

        publisherURI2 = postJson(buildPublishersURL("/publishers"), mapper.writeValueAsString(TestData.PUBLISHER_2));
        publisherURI0 = postJson(buildPublishersURL("/publishers"), mapper.writeValueAsString(TestData.PUBLISHER_0));
        publisherURI1 = postJson(buildPublishersURL("/publishers"), mapper.writeValueAsString(TestData.PUBLISHER_1));
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
        String jsonResponse0 = simpleGet(new URL(publisherURI0));
        String jsonResponse1 = simpleGet(new URL(publisherURI1));
        String jsonResponse2 = simpleGet(new URL(publisherURI2));

        Publisher response0 = mapper.readValue(jsonResponse0, Publisher.class);
        Publisher response1 = mapper.readValue(jsonResponse1, Publisher.class);
        Publisher response2 = mapper.readValue(jsonResponse2, Publisher.class);

        Assert.assertEquals(TestData.PUBLISHER_0.getName(), response0.getName());
        Assert.assertEquals(TestData.PUBLISHER_1.getName(), response1.getName());
        Assert.assertEquals(TestData.PUBLISHER_2.getName(), response2.getName());
    }

    @Test
    public void getByNameSeveralPossibilities() throws Exception {
        String jsonResponse = simpleGet(buildPublishersURL("/publishers?name=name"));
        Publisher[] response = mapper.readValue(jsonResponse, Publisher[].class);

        Assert.assertEquals(2, response.length);
        Assert.assertEquals(TestData.PUBLISHER_0.getOrganizationId(), response[0].getOrganizationId());
        Assert.assertEquals(TestData.PUBLISHER_2.getOrganizationId(), response[1].getOrganizationId());
    }

    @Test
    public void getByNameSingle() throws Exception {
        String jsonResponse = simpleGet(buildPublishersURL("/publishers?name=name2"));
        Publisher[] response = mapper.readValue(jsonResponse, Publisher[].class);

        Assert.assertEquals(1, response.length);
        Assert.assertEquals(TestData.PUBLISHER_2.getOrganizationId(), response[0].getOrganizationId());
    }

    @Test
    public void getByOrgidSeveralPossibilities() throws Exception {
        String jsonResponse = simpleGet(buildPublishersURL("/publishers?organizationId=34"));
        Publisher[] response = mapper.readValue(jsonResponse, Publisher[].class);

        Assert.assertEquals(2, response.length);
        Assert.assertEquals(TestData.PUBLISHER_0.getName(), response[0].getName());
        Assert.assertEquals(TestData.PUBLISHER_1.getName(), response[1].getName());
    }

    @Test
    public void getByOrgidSingle() throws Exception {
        String jsonResponse = simpleGet(buildPublishersURL("/publishers?organizationId=3456"));
        Publisher[] response = mapper.readValue(jsonResponse, Publisher[].class);

        Assert.assertEquals(1, response.length);
        Assert.assertEquals(TestData.PUBLISHER_1.getName(), response[0].getName());
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
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("Content-Length", Integer.toString(jsonData.length()));
        connection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(jsonData);
        wr.flush();
        wr.close();

        return connection.getHeaderField("Location");
    }
}
