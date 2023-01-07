package the.kis.devs.server.command.commands

import the.kis.devs.server.data.SocketMessage
import the.kis.devs.server.capeapi.CapeAPIManager
import the.kis.devs.server.capeapi.CapeAPIManager.syncCapes
import the.kis.devs.server.capeapi.cape.Cape
import the.kis.devs.server.capeapi.cape.Capes
import the.kis.devs.server.command.Command

/**
 * `args[1]` - key
 *
 * `args[2] - properties for hwid
 *
 * `args[3] - processors
 *
 * `args[4]` - uuid
 *
 * `args[5]` - status( 1 - client was started | 2 - client was ended )
 *
 * or
 *
 * `args[1]` - uuid
 *
 * `args[2]` - status( 1 - client was started | 2 - client was ended )
 *
 * 0 - invalid arguments
 *
 * 1 - invalid key
 *
 * 2|{cape} - current cape
 *
 * @author _kisman_
 * @since 10:06 of 01.11.2022
 */
object ClientStatusCommand : Command(
    "clientstatus"
) {
    override fun execute(
        line : String,
        args : List<String>
    ) : List<SocketMessage> {
        return if(args.size == 5 || args.size == 3) {
            if (args.size == 3 || AuthCommand.execute("", listOf("auth ${args[1]}, ${args[2]} ${args[3]}"))[0].text == "2") {
                val cape0 = (if(args.size == 3) Capes.Developer else CapeAPIManager.getLatestCape()) ?: return listOf(
                    SocketMessage("0")
                )
                val cape = Cape(cape0, args[if(args.size == 3) 1 else 4])


                if(args[if(args.size == 3) 2 else 5] == "1") {
                    CapeAPIManager.capes.add(cape)
                } else if(args[if(args.size == 3) 2 else 5] == "2") {
                    if(CapeAPIManager.capes.contains(cape)) {
                        CapeAPIManager.capes.remove(cape)
                    } else {
                        listOf(SocketMessage("0"))
                    }
                }

                syncCapes()
                listOf(SocketMessage("2"))
            } else {
                listOf(SocketMessage("1"))
            }
        } else listOf(SocketMessage("0"))
    }
}