package the.kis.devs.remapper.utils;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class FieldRemapper implements Remapper
{
    @Override
    public void remap(ClassNode cn, Mapping mapping)
    {
        for (FieldNode fn : cn.fields)
        {
            fn.desc = MappingUtil.mapDescription(fn.desc, mapping);

            System.out.println("Remapping field " + fn.name + " " + fn.desc);

            if (fn.signature != null)
            {
                System.out.println("Remapping field " + fn.desc);

                fn.signature = MappingUtil.mapSignature(fn.signature, mapping);
            }

            if (hasShadowAnnotation(fn))
            {
                fn.name = mapping.getFields().getOrDefault(fn.name, fn.name);
            }
        }
    }

    private boolean hasShadowAnnotation(FieldNode fn)
    {
        if (fn.visibleAnnotations != null)
        {
            for (AnnotationNode an : fn.visibleAnnotations)
            {
                if (an != null
                        && "Lorg/spongepowered/asm/mixin/Shadow;"
                        .equals(an.desc))
                {
                    return true;
                }
            }
        }

        if (fn.invisibleAnnotations != null)
        {
            for (AnnotationNode an : fn.invisibleAnnotations)
            {
                if (an != null
                        && "Lme/earth/earthhack/installer/srg2notch/RemapFieldName;"
                        .equals(an.desc))
                {
                    return true;
                }
            }
        }

        return false;
    }

}