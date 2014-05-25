package hello

import framework.model.Framework
import hello.FrameworkRepository
import hello.FrameworksController
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import javax.sql.DataSource

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

/**
 * User: jorgefrancoleza
 * Date: 25/02/14
 */
class FrameworksControllerSpec extends Specification {

    private MockMvc mockMvc
    private SimpMessagingTemplate messagingTemplate = Mock(SimpMessagingTemplate)
    private FrameworksController frameworksController
    private FrameworkRepository repository = Mock(FrameworkRepository)

    void setup() {
        frameworksController = new FrameworksController()
        frameworksController.simpMessagingTemplate = messagingTemplate
        frameworksController.repository = repository
        mockMvc = standaloneSetup(frameworksController).build()
    }

    def 'get frameworks list'() {
        when:
        def response = mockMvc.perform(get('/frameworks'))

        then:
        1 * repository.findAll() >> [[NAME: 'Grails', URL: 'grails.org', URL_IMAGE: 'image']]
        response.andExpect(status().isOk())
                .andExpect(content().string('[{"NAME":"Grails","URL":"grails.org","URL_IMAGE":"image"}]'))
        0 * _
    }

    def 'post a new framework'() {
        given:
        def newFramework = new Framework(name: 'Spock', url: 'spock.org', urlImage: 'image')

        when:
        def response = mockMvc.perform(post('/frameworks').
                param('name', newFramework.name).
                param('url', newFramework.url).
                param('urlImage', newFramework.urlImage))

        then:
        1 * repository.save(newFramework)
        response.andExpect(status().isOk())
                .andExpect(content().string('{"id":null,"name":"Spock","url":"spock.org","urlImage":"image"}'))
        1 * messagingTemplate.convertAndSend('/topic/newFramework', newFramework)
        0 * _
    }
}