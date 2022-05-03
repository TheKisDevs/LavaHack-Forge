package com.kisman.cc.gui.csgo

class Utils {
    companion object {
        fun formatTime(l: Long): String? {
            var l = l
            val minutes = l / 1000 / 60
            l -= minutes * 1000 * 60
            val seconds = l / 1000
            l -= seconds * 1000
            val sb = StringBuilder()
            if (minutes != 0L) sb.append(minutes).append("min ")
            if (seconds != 0L) sb.append(seconds).append("s ")
            if (l != 0L || minutes == 0L && seconds == 0L) sb.append(l).append("ms ")
            return sb.substring(0, sb.length - 1)
        }
    }
}