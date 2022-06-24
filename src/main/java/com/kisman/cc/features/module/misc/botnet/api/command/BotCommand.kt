package com.kisman.cc.features.module.misc.botnet.api.command

import net.minecraft.client.Minecraft

open class BotCommand {

    constructor(name:String, args_needed: Boolean = false) {
        names.add(name)
        this.args_needed = args_needed
    }

    constructor(names: ArrayList<String>, args: Boolean = false) {
        this.names = names
        this.args_needed = args_needed
    }

    lateinit var names: ArrayList<String>
    protected var mc: Minecraft = Minecraft.getMinecraft()
    protected var args_needed: Boolean = false

    fun execute(args: Array<String?>) {
        if(args_needed) perform(args)
        else perform()
    }

    open fun perform(args: Array<String?>) {
    }

    open fun perform() {
    }

}