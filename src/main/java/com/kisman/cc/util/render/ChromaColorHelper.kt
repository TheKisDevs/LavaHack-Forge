package com.kisman.cc.util.render

import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.ChromaColorTypes
import com.kisman.cc.util.enums.ChromaColorTypes.*
import com.kisman.cc.util.math.lerp
import kotlin.math.abs
import com.kisman.cc.util.math.coerceIn

/**
 * @author _kisman_
 * @since 14:40 of 26.07.2022
 */
object ChromaColorHelper {
    /**
     * For pulsive
     */
    fun getColor(pulsiveColors : Array<Colour>, hOffsetRaw : Int, hOffsetModifier : Int, sat : Float, bright : Float, speed : Double) : Colour {
        return getColor(null, pulsiveColors, ChromaColorTypes.Pulsive, hOffsetRaw, hOffsetModifier, sat, bright, speed)
    }

    /**
     * For pulsive
     */
    fun getColor(pulsiveColors : Array<Colour>, hOffsetRaw : Int, hOffsetModifier : Int, speed : Double) : Colour {
        return getColor(null, pulsiveColors, ChromaColorTypes.Pulsive, hOffsetRaw, hOffsetModifier, 100f, 50f, speed)
    }

    /**
     * For pulsive
     */
    fun getColor(pulsiveColors : Array<Colour>, hOffsetRaw : Int, hOffsetModifier : Int) : Colour {
        return getColor(null, pulsiveColors, ChromaColorTypes.Pulsive, hOffsetRaw, hOffsetModifier, 100f, 50f, 2.0)
    }

    /**
     * For rainbow/astolfo
     */
    fun getColor(type : ChromaColorTypes, hOffsetRaw : Int, hOffsetModifier : Int, sat : Float, bright : Float, speed : Double) : Colour {
        return getColor(null, emptyArray(), type, hOffsetRaw, hOffsetModifier, sat, bright, speed)
    }

    /**
     * For rainbow/astolfo
     */
    fun getColor(type : ChromaColorTypes, hOffsetRaw : Int, hOffsetModifier : Int, speed : Double) : Colour {
        return getColor(type, hOffsetRaw, hOffsetModifier, 100f, 50f, speed)
    }

    /**
     * For rainbow/astolfo
     */
    fun getColor(type : ChromaColorTypes, hOffsetRaw : Int, hOffsetModifier : Int) : Colour {
        return getColor(type, hOffsetRaw, hOffsetModifier, 100f, 50f, 2.0)
    }
    
    fun getColor(staticColor : Colour?, pulsiveColors : Array<Colour>, type : ChromaColorTypes, hOffsetRaw : Int, hOffsetModifier : Int) : Colour {
        return getColor(staticColor, pulsiveColors, type, hOffsetRaw, hOffsetModifier, 100f, 50f, 2.0)
    }

    fun getColor(staticColor : Colour?, pulsiveColors : Array<Colour>, type : ChromaColorTypes, hOffsetRaw : Int, hOffsetModifier : Int, sat : Float, bright : Float, speed : Double) : Colour {
        val hOffset = hOffsetRaw * hOffsetModifier
        return when(type) {
            Rainbow -> rainbow(hOffset, sat, bright, speed)
            Astolfo -> astolfo(hOffset, sat, bright, speed)
            Pulsive -> if(pulsiveColors.size == 2) pulsive(pulsiveColors[0], pulsiveColors[1], hOffset, sat, bright, speed) else Colour(255, 255, 255, 255)
            Static -> staticColor ?: Colour(255, 255, 255, 255)
            Fade -> if(staticColor != null) fade(staticColor, hOffset, sat, bright, speed) else Colour(255, 255, 255, 255)
        }
    }

    private fun fade(color : Colour, hOffset : Int, sat : Float, minBright : Float, speed : Double) : Colour {
        val mod : Double = 11529L / speed
        val div = mod / 360

        return Colour.fromHSB(
            color.hue,
            abs(System.currentTimeMillis() % mod / div + hOffset).coerceIn(minBright.toDouble(), 1.0).toFloat(),
            sat
        )
    }

    //public static Color TwoColoreffect(Color cl1, Color cl2, double speed) {
    //double thing = speed / 4.0 % 1.0;
    //float val = MathHelper.clamp((float) Math.sin(Math.PI * 6 * thing) / 2.0f + 0.5f, 0.0f, 1.0f);
    //return new Color(lerp((float) cl1.getRed() / 255.0f, (float) cl2.getRed() / 255.0f, val),
    //lerp((float) cl1.getGreen() / 255.0f, (float) cl2.getGreen() / 255.0f, val),
    //lerp((float) cl1.getBlue() / 255.0f, (float) cl2.getBlue() / 255.0f, val));
    //}

    private fun pulsive(color1 : Colour, color2 : Colour, hOffset : Int, sat : Float, bright : Float, speed : Double) : Colour {
        val mod : Double = 11529L / speed
        val div = mod / 360
        var delay = (System.currentTimeMillis() % mod / div).toInt() + hOffset
        
        if(delay > 1) {
            val n = delay % 1
            delay = (if(delay % 2 == 0) n else (1 - n))
        }
//        val modifier = MathHelper.clamp(sin(PI * 6 * ))
        
        return Colour(
            lerp(color1.r, color2.r, delay),
            lerp(color1.g, color2.g, delay),
            lerp(color1.b, color2.b, delay),
            lerp(color1.a, color2.a, delay)
//            (color1.r * n2 + color2.r * delay),
//            (color1.g * n2 + color2.g * delay),
//            (color1.b * n2 + color2.b * delay),
//            (color1.a * n2 + color2.a * delay)
        ).setSaturation(sat).setBrightness(bright)
    }

    private fun astolfo(hOffset : Int, sat : Float, bright : Float, speed : Double) : Colour {
        var hue : Double = (System.currentTimeMillis() % speed).toFloat() + (1000 - hOffset) * 9.0
        while (hue > speed) hue -= speed
        hue /= speed
        if (hue > 0.5) hue = 0.5f - (hue - 0.5f)
        hue += 0.5f
        return Colour.fromHSB(hue.toFloat(), sat, bright)
    }

    private fun rainbow(hOffset : Int, sat : Float, bright : Float, speed : Double) : Colour {
        val mod : Double = 11529L / speed
        val div = mod / 360
        var hue : Int = (System.currentTimeMillis() % mod / div).toInt() + hOffset
        if (hue > 360) hue -= 360
        return Colour.fromHSB(hue / 360f, sat / 100, bright / 100)
    }
}