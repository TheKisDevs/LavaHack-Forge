package the.kis.devs.remapper.utils;

import org.objectweb.asm.tree.ClassNode;

public class ASMRemapper
{
    private final Remapper[] reMappers;

    public ASMRemapper()
    {
        reMappers    = new Remapper[5];
        reMappers[0] = new ClassRemapper();
        reMappers[1] = new FieldRemapper();
        reMappers[2] = new MethodRemapper();
        reMappers[3] = new InstructionRemapper();
        reMappers[4] = new AnnotationRemapper();
    }

    public byte[] transform(byte[] clazz, Mapping mapping) {
        ClassNode cn;
        try {
            cn = AsmUtil.read(clazz);
        } catch (IllegalArgumentException e) {
            System.out.println("Bad class");
            return clazz;
        }

        for (Remapper remapper : reMappers) {
            System.out.println("Remapping with using " + remapper.getClass().getSimpleName());

            remapper.remap(cn, mapping);
        }

        return AsmUtil.write(cn);
    }

}