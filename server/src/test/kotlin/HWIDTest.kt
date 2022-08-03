import org.junit.jupiter.api.Test
import the.kis.devs.server.hwid.HWID

/**
 * @author _kisman_
 * @since 11:49 of 06.07.2022
 */
internal class HWIDTest {
    private fun getProperties() : String {
        val builder = StringBuilder()

        for(property in System.getProperties().keys()) {
            if(property is String && property != "line.separator") {
                builder.append("$property|${System.getProperty(property)}&")
            }
        }

        for(env in System.getenv().keys) {
            if(env is String) {
                builder.append("$env|${System.getenv(env)}&")
            }
        }

        return builder.toString()
    }

    @Test fun test() {
        println(getProperties().replace(" ", "_"))
        /*for(env in System.getenv().keys) {
            if(env is String && env != "line.separator") {
                println("$env | ${System.getenv(env)}&")
            }
        }*/
    }

    @Test fun getHWIDTest() {
        println(HWID(getProperties(), Runtime.getRuntime().availableProcessors()).hwid)
    }
}