package com.kisman.cc.features.catlua.mapping;

import com.kisman.cc.Kisman;
import org.luaj.vm2.LuaValue;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.HashMap;

public class Remapper3000 {
    public final String params = "params.csv", fields = "fields.csv", methods = "methods.csv", lzma = "lzma.txt";
    public final Path paramsPath = Paths.get(Kisman.fileName + Kisman.mappingName + params), fieldsPath = Paths.get(Kisman.fileName + Kisman.mappingName + fields), methodsPath = Paths.get(Kisman.fileName + Kisman.mappingName + methods);

    public final Path mapping = Paths.get(Kisman.fileName + Kisman.mappingName + lzma);//lzma

    public HashMap<String, String> fieldsMapping, methodsMapping, fieldsMappingReverse, methodsMappingReverse;
    public final HashMap<String, Field> fieldsCache = new HashMap<>();
    public final HashMap<String, LuaValue> methodsCache = new HashMap<>();
    public final HashMap<String, Class<?>> classCache = new HashMap<>();

    public String remappingField(String toRemap) {
        return fieldsMapping.getOrDefault(toRemap, toRemap);
    }

    public String remappingMethod(String toRemap) {
        return methodsMapping.getOrDefault(toRemap, toRemap);
    }

    public void init() {
        if(!Kisman.canInitializateCatLua) {
            Kisman.LOGGER.error("Can't init CatLua :<");
            return;
        }

        Kisman.LOGGER.info("[Remapper3000] Start remapping!");


        if(!Files.exists(mapping) || !Files.exists(fieldsPath) || !Files.exists(methodsPath)) Kisman.LOGGER.error("[Remapper3000] You haven't mapping files, if you want to use lua scripts, you should to put mapping files in .minecraft/kisman/cc/Mapping/ and relaunch the client!");
        else {
            fieldsMapping = methodsMapping = fieldsMappingReverse = methodsMappingReverse = new HashMap<>();

            if(!Kisman.canInitializateCatLua) return;

            //fields
            {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(fieldsPath)))) {
                    String inputLine;
                    String[] toOut = new String[] {"", ""};
                    while ((inputLine = br.readLine()) != null) {
                        String[] split = inputLine.split(",");
                        fieldsMapping.put(split[0], split[1]);
                        toOut = new String[] {split[0], split[1]};
                    }
                    System.out.println("key - " + toOut[1] + "\nvalue - " + toOut[0]);
                } catch (IOException e) {
                    Kisman.LOGGER.error("[Remapper3000] Remapper got error with fields mapping!");
                }
            }

            //methods
            {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(methodsPath)))) {
                    String inputLine;
                    String[] toOut = new String[] {"", ""};
                    while ((inputLine = br.readLine()) != null) {
                        String[] split = inputLine.split(",");
                        methodsMapping.put(split[0], split[0]);
                        toOut = new String[] {split[0], split[1]};
                    }
                    System.out.println("key - " + toOut[1] + "\nvalue - " + toOut[0]);
                } catch (IOException e) {
                    Kisman.LOGGER.error("[Remapper3000] Remapper got error with methods mapping!");
                }
            }

            Kisman.instance.forgeMappings = new ForgeMappings();
            try {
                Kisman.instance.forgeMappings.init(new FileInputStream(mapping.toFile()));
            } catch (IOException e) {
                Kisman.LOGGER.error("[ForgeMappings] You have error with lzma.txt mapping file, please, fix the problem and launch the client!(Maybe you will get crash if you dont fix the problem)");
            }
        }

        Kisman.LOGGER.info("[Remapper3000] Remapping has been finished!");
    }
}
