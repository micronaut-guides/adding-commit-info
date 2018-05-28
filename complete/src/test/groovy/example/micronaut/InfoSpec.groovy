package example.micronaut

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.RxHttpClient
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class InfoSpec extends Specification {

    @Shared
    @AutoCleanup // <1>
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)  // <2>

    @Shared
    @AutoCleanup
    RxHttpClient client = embeddedServer.applicationContext.createBean(RxHttpClient, embeddedServer.getURL()) // <3>

    def "test git commit info appears in JSON"() {
        given:
        HttpRequest request = HttpRequest.GET('/info') // <4>

        when:
        HttpResponse<Map> rsp = client.toBlocking().exchange(request, Map)

        then:
        rsp.status().code == 200

        when:
        Map json = rsp.body() // <5>

        then:
        json.git
        json.git.commit
        json.git.commit.message
        json.git.commit.time
        json.git.commit.id
        json.git.commit.user
        json.git.branch
    }
}
