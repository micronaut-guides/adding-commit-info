package example.micronaut;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

public class InfoTest {
    private static EmbeddedServer server;
    private static HttpClient client;

    @BeforeClass
    public static void setupServer() {
        server = ApplicationContext.run(EmbeddedServer.class); // <1>
        client = server
                .getApplicationContext()
                .createBean(HttpClient.class, server.getURL()); // <2>
    }

    @AfterClass
    public static void stopServer() {
        if(server != null) {
            server.stop();
        }
        if(client != null) {
            client.stop();
        }
    }

    @Test
    public void testGitComitInfoAppearsInJson() {

        HttpRequest request = HttpRequest.GET("/info"); // <3>

        HttpResponse<Map> rsp = client.toBlocking().exchange(request, Map.class);

        assertEquals(rsp.status().getCode(), 200);

        Map json = rsp.body(); // <4>

        then:
        assertNotNull(json.get("git"));
        assertNotNull(((Map) json.get("git")).get("commit"));
        assertNotNull(((Map) ((Map) json.get("git")).get("commit")).get("message"));
        assertNotNull(((Map) ((Map) json.get("git")).get("commit")).get("time"));
        assertNotNull(((Map) ((Map) json.get("git")).get("commit")).get("id"));
        assertNotNull(((Map) ((Map) json.get("git")).get("commit")).get("user"));
        assertNotNull(((Map) json.get("git")).get("branch"));
    }
}
