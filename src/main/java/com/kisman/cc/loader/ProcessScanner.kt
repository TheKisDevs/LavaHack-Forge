package com.kisman.cc.loader

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @author _kisman_
 * @since 1:15 of 31.08.2022
 */

val illegalProcesses = listOf(
    "HTTPDebuggerSvc.exe",//HTTPDebugger
    "HTTPDebuggerUI.exe",//HTTPDebugger
    "Extreme Injector v3.exe",//ExtremeInjector
//    "vmware-authd",//VMWare
//    "vmware-usbarbitrator64",//VMWare
    "binaryninja.exe"//BinaryNigga
)

fun runScanner() {
    Thread {
        try {
            val process = Runtime.getRuntime().exec("tasklist")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line : String? = null

            while (reader.readLine().also { line = it } != null) {
                val processName = line!!.split(" ")[0]

                if (illegalProcesses.contains(processName)) {
                    Utility.unsafeCrash()
                }
            }
        } catch (e : Exception) {
            println("Error Code: 0x1")
        }
    } .start()
}