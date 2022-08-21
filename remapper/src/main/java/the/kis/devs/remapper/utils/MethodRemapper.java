package the.kis.devs.remapper.utils;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import java.util.ArrayList;
import java.util.List;

public class MethodRemapper implements Remapper
{
    @Override
    public void remap(ClassNode cn, Mapping mapping)
    {
        for (MethodNode mn : cn.methods)
        {
            mn.desc = MappingUtil.mapDescription(mn.desc, mapping);
            mn.name = MappingUtil.map(null, mn.name, mn.desc, mapping);

            if(mn.name.equals("<init>")) {
                continue;
            }

//            System.out.println("Method signature is " + mn.signature);

            System.out.println("Remapping method " + mn.name + mn.desc);

            mn.signature = MappingUtil.mapSignature(mn.name + mn.desc, mapping);

            /*if (mn.signature != null)
            {

                mn.signature = MappingUtil.mapSignature(mn.signature, mapping);
            }*/ /*else {
                System.out.println("Cant remap method");
            }*/

            if (mn.tryCatchBlocks != null)
            {
                for (TryCatchBlockNode t : mn.tryCatchBlocks)
                {
                    t.type = mapping.getClasses().getOrDefault(t.type, t.type);
                }
            }

            if (mn.exceptions != null && !mn.exceptions.isEmpty())
            {
                List<String> exceptions = new ArrayList<>(mn.exceptions.size());
                for (String e : mn.exceptions)
                {
                    exceptions.add(mapping.getClasses().getOrDefault(e, e));
                }

                mn.exceptions = exceptions;
            }

            if (mn.localVariables != null)
            {
                for (LocalVariableNode l : mn.localVariables)
                {
                    l.desc = MappingUtil.mapDescription(l.desc, mapping);

                    System.out.println("Remapping local variable " + l.name + " " + l.desc);

                    l.signature = l.desc;

                    /*if (l.signature != null)
                    {
                        l.signature =
                                MappingUtil.mapSignature(l.signature, mapping);
                    }*/
                }
            }
        }
    }

}