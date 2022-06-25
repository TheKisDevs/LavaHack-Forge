package com.kisman.cc.features.module.misc.botnet.api.command

import net.minecraft.client.Minecraft

enum class ExecutingType {
    NONE, NAMEnARGS, ARGS, RAW
}

open class BotCommand {

    constructor(name:String, executingType: ExecutingType = ExecutingType.NONE) {
        names.plus(name)
        this.executingType = executingType
    }

    constructor(names: Array<String>, executingType: ExecutingType = ExecutingType.NONE) {
        this.names = names
        this.executingType = executingType
    }

    var executingType: ExecutingType;
    lateinit var names: Array<String>
    protected var mc: Minecraft = Minecraft.getMinecraft()

    //none

    open fun execute() {
    }

    //args

    open fun execute(args: Array<String?>) {
    }

    //name&args

    open fun execute(name: String, args: Array<String?>) {
    }

    //raw

    open fun execute(command: String) {
    }


}