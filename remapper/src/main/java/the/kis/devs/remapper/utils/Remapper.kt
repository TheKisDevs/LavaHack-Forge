package the.kis.devs.remapper.utils

import org.objectweb.asm.tree.ClassNode

interface Remapper {
    fun remap(
        cn : ClassNode,
        mapping : Mapping
    )
}