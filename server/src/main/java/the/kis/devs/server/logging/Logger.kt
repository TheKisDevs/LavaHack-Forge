package the.kis.devs.server.logging

import the.kis.devs.server.LOGS_PATH
import the.kis.devs.server.now
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset
import java.time.LocalDateTime

/**
 * @author _kisman_
 * @since 16:45 of 02.04.2023
 */
@Suppress("BlockingMethodInNonBlockingContext")
open class Logger(
    private val name : String
) {
    private val history = mutableListOf<String>()
    private val file = File("$LOGS_PATH/${now.dayOfMonth}.${now.monthValue}.${now.year}-${now.hour}.${now.minute}.${now.second}.txt")

    open fun prefix() : String = ""
    fun print(
        text : String
    ) {
        LocalDateTime.now().also { it0 ->
            "<${it0.dayOfMonth}.${it0.monthValue}.${it0.year} ${it0.hour}:${it0.minute}:${it0.second}/$name>${prefix()}: $text".also { it1 ->
                println(it1)

                if(!file.exists()) {
                    file.createNewFile()
                }

                val fos = FileOutputStream(file)

                synchronized(fos) {
                    history += it1

                    for(message in history) {
                        fos.write("$message\n".toByteArray(Charset.defaultCharset()))
                    }

                    fos.flush()
                    fos.close()
                }
            }
        }
    }
}