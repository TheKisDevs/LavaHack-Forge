import org.junit.jupiter.api.Test
import the.kis.devs.server.keyauth.KeyAuthApp

/**
 * @author _kisman_
 * @since 20:51 of 10.01.2023
 */
internal class KeyAuthTest {
    @Test
    fun initTest() {
        val time = System.currentTimeMillis()

        KeyAuthApp.keyAuth.init()

        val delta = System.currentTimeMillis() - time

        println(delta)
    }
}