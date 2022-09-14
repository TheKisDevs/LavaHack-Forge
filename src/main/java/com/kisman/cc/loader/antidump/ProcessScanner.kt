package com.kisman.cc.loader.antidump

import com.kisman.cc.loader.*
import com.kisman.cc.loader.sockets.client.SocketClient
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
    "binaryninja.exe",//BinaryNigga
    "tcpview.exe",
    "autoruns.exe",
    "autorunsc.exe",
    "filemon.exe",
    "procmon.exe",
    "procexp.exe",
    "idaq.exe",
    "idaq64.exe",
    "ida.exe",
    "ida64.exe",
    "ImmunityDebugger.exe",
    "Wireshark.exe",
    "dumpcap.exe",
    "HookExplorer.exe",
    "ImportREC.exe",
    "PETools.exe",
    "LordPE.exe",
    "tcpview.exe",
    "SysInspector.exe",
    "proc_analyzer.exe",
    "sysAnalyzer.exe",
    "sniff_hit.exe",
    "windbq.exe",
    "joeboxcontrol.exe",
    "joeboxserver.exe",
    "fiddler.exe",
    "tv_w32.exe",
    "tv_x64.exe",
    "Charles.exe",
    "netFilterService.exe",
    "HTTPAnalyzerStdV7.exe"
)

fun runScanner() {
    Thread {
        try {
            if(Utility.runningOnWindows()) {
                val process = Runtime.getRuntime().exec("tasklist")
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                var line: String? = null

                while (reader.readLine().also { line = it } != null) {
                    val processName = line!!.split(" ")[0]

                    if (illegalProcesses.contains(processName)) {
                        Utility.unsafeCrash()
                    }
                }
            }
        } catch (e : Exception) {
            val client = SocketClient(
                address,
                port
            )
            setupSocketClient(client)
            client.writeMessage { text = "sendmessage got error code 0x1" }
            LavaHackLoaderCoreMod.LOGGER.info("Error Code: 0x1")
        }
    } .start()
}