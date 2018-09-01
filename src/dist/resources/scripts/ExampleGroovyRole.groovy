import uk.co.nickthecoder.tickle.AbstractRole

class ExampleGroovyRole extends AbstractRole {

    def void tick() {
        actor.y += 1
    }
}
