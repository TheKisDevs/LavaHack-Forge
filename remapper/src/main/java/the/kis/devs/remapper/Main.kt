package the.kis.devs.remapper

import the.kis.devs.remapper.utils.Srg2NotchService
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author _kisman_
 * @since 14:33 of 14.08.2022
 */

fun main(args : Array<String>) {
    if(args.size < 2) {
        println("Have no arguments :/")
        return
    }

    val input = args[0]
    val output = args[1]

    val inputPath = Paths.get(input)
    val outputPath = Paths.get(output)

    val inputFile = File(input)
    val outputFile = File(output)

    if(!inputFile.exists()) {
        println("Input file dont exists!")
        return
    }

    if(outputFile.exists()) {
        println("WARNING: Output file will be overwritten!")
        try {
            outputFile.delete()
        } catch(e : IOException) {
            println("Cant delete output file!")
            e.printStackTrace()
        }
    }

    Files.createFile(outputPath)

    val remapper = Srg2NotchService()

    remapper.remap(
        inputFile,
        outputFile
    )
}