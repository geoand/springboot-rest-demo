package framework.presenter

import framework.model.Framework
import framework.model.FrameworksModel
import framework.view.FrameworksView
import org.grooscript.jquery.Binder
import org.grooscript.jquery.GQuery
import org.grooscript.jquery.GQueryImpl

/**
 * User: jorgefrancoleza
 * Date: 25/02/14
 */
class Presenter {

    FrameworksView view
    FrameworksModel model

    List<Framework> frameworks = []
    String nameFramework
    String urlFramework
    String urlImageFramework

    def onLoad() {
        frameworks = []
        nameFramework = ''
        urlFramework = ''
        urlImageFramework = ''
        model.loadFrameworks(updateFrameworksList)
    }

    def start() {
        view = new FrameworksView()
        model = new FrameworksModel()
    }

    def validUrl(url) {
        ['http://', 'https://'].any { url && url.startsWith(it) }
    }

    def hasEvilChars = {data ->
        data && data.indexOf('<') >= 0
    }

    def buttonAddFrameworkClick() {
        def validationErrors = []

        def insertError = { errorMessage ->
            return { validationErrors << errorMessage }
        }

        _if !nameFramework then insertError('Missing name framework')
        _if !urlFramework then insertError('Missing url framework')
        _if urlFramework and !validUrl(urlFramework) then insertError('Wrong url framework')
        _if urlImageFramework and !validUrl(urlImageFramework) then insertError('Wrong url image')
        _if ([nameFramework, urlFramework, urlImageFramework].any(hasEvilChars)) then insertError('Wrong chars')
        _if existsNameFramework(nameFramework) then insertError("Framework ${nameFramework} already exists")

        if (!validationErrors.size()) {
            model.addFramework(nameFramework, urlFramework, urlImageFramework, addNewFrameworkToList)
        }

        view.validationError(validationErrors)
    }

    def updateFrameworksList = { List<Framework> newFrameworks ->
        frameworks = newFrameworks
        view.updateFrameworks(newFrameworks)
    }

    def addNewFrameworkToList = { Framework framework ->
        if (!frameworks.contains(framework)) {
            frameworks << framework
            view.updateFrameworks(frameworks)
        } else {
            println 'Repeated framework!'
        }
    }

    private existsNameFramework(name) {
        frameworks.any { it.name.toUpperCase() == name.toUpperCase() }
    }

    def _if(eval) {
        def result = evaluation(eval)
        [and: and.rcurry(result), then: then.rcurry(result)]
    }

    Closure and = { eval, previousResult ->
        def result = previousResult && evaluation(eval)
        [and: and.rcurry(result), then: then.rcurry(result)]
    }

    Closure then = { closure, result ->
        if (result) {
            closure()
        }
        [otherwise: otherwise.rcurry(result)]
    }

    Closure otherwise = { closure, result ->
        if (!result) {
            closure()
        }
    }

    def evaluation = { value ->
        value instanceof Closure ? value() : value
    }
}
