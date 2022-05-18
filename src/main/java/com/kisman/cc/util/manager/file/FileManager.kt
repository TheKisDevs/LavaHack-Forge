package com.kisman.cc.util.manager.file

import com.kisman.cc.Kisman
import com.kisman.cc.util.render.image.ImageUtil
import com.kisman.cc.util.render.image.NameableImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author _kisman_
 * @since 20:32 of 17.05.2022
 */
class FileManager {
    companion object {
        const val image = "image"

        fun getImage() : NameableImage? {
            if(Files.exists(Paths.get(Kisman.fileName + Kisman.imagesName))) {
                var file: File? = null

                if (Files.exists(Paths.get(Kisman.fileName + Kisman.imagesName + image + ".png"))) {
                    file = Paths.get(Kisman.fileName + Kisman.imagesName + image + ".png").toFile()
                }

                if (Files.exists(Paths.get(Kisman.fileName + Kisman.imagesName + image + ".jpg"))) {
                    file = Paths.get(Kisman.fileName + Kisman.imagesName + image + ".jpg").toFile()
                }

                if (Files.exists(Paths.get(Kisman.fileName + Kisman.imagesName + image + ".jpeg"))) {
                    file = Paths.get(Kisman.fileName + Kisman.imagesName + image + ".jpeg").toFile()
                }

                if (file == null) {
                    return null
                }

                val split = file.name.split("\\.")
                val format = split[split.size - 1]
                return NameableImage(
                    file.name,
                    ImageUtil.cacheBufferedImage(
                        ImageUtil.createFlipped(ImageUtil.bufferedImageFromFile(file)),
                        format,
                        file.name
                    )
                )
            }

            return null
        }
    }
}