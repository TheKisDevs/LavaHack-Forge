package com.kisman.cc.catlua.mapping;

import com.kisman.cc.Kisman;
import com.kisman.cc.catlua.parser.MappingParser;
import org.luaj.vm2.LuaValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class Remapper3000 {
    public final String params = "params.csv", fields = "fields.csv", methods = "methods.csv";
    public final Path paramsPath = Paths.get(Kisman.fileName + Kisman.mappingName + params), fieldsPath = Paths.get(Kisman.fileName + Kisman.mappingName + fields), methodsPath = Paths.get(Kisman.fileName + Kisman.mappingName + methods);

    public HashMap<String, String> fieldsMapping, methodsMapping, fieldsMappingReverse;
    public HashMap<String, Field> fieldsCache;
    public HashMap<String, LuaValue> methodsCache;

    public MappingParser parser;

    public String remappingField(String toRemap) {
        if(fieldsMapping.containsKey(toRemap)) return fieldsMapping.get(toRemap);
        return toRemap;
    }

    public String remappingMethod(String toRemap) {
        if(methodsMapping.containsKey(toRemap)) return methodsMapping.get(toRemap);
        return toRemap;
    }

    public void init() {
        Kisman.LOGGER.info("[Remapper3000] Start remapping!");
        parser = new MappingParser();
        parser.reset();

        fieldsCache = new HashMap<>();
        methodsCache = new HashMap<>();
        if(!Files.exists(fieldsPath) || !Files.exists(methodsPath)) Kisman.LOGGER.error("[Remapper3000] You haven't mapping files, if you want to use lua scripts, you should to put mapping files in .minecraft/kisman/cc/Mapping/ and relaunch the client!");
        else {
            fieldsMapping = methodsMapping = fieldsMappingReverse = new HashMap<>();

            //fields
            {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(fieldsPath)))) {
                    String inputLine;
                    String[] toOut = new String[] {"", ""};
                    while ((inputLine = br.readLine()) != null) {
                        String[] split = inputLine.split(",");
                        fieldsMapping.put(split[1], split[0]);
                        fieldsMappingReverse.put(split[0], split[1]);
                        parser.parse(split[0], split[1], MappingParser.ForParsing.FIELD);
                        toOut = new String[] {split[0], split[1]};
                    }
                    System.out.println("key - " + toOut[1] + "\nvalue - " + toOut[0]);
                } catch (IOException e) {
                    Kisman.LOGGER.error("[Remapper3000] Remapper got error with fields mapping!");
                }
            }

            //methods
            {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(fieldsPath)))) {
                    String inputLine;
                    String[] toOut = new String[] {"", ""};
                    while ((inputLine = br.readLine()) != null) {
                        String[] split = inputLine.split(",");
                        methodsMapping.put(split[1], split[0]);
                        parser.parse(split[0], split[1], MappingParser.ForParsing.METHOD);
                        toOut = new String[] {split[0], split[1]};
                    }
                    System.out.println("key - " + toOut[1] + "\nvalue - " + toOut[0]);
                } catch (IOException e) {
                    Kisman.LOGGER.error("[Remapper3000] Remapper got error with methods mapping!");
                }
            }
        }
    }

    public Class getClassByName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public Method getMethodFromClassByName(Class clazz, String name) {
        try {
            return clazz.getMethod(name);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public Method getMethodFromClassByName(String pathToClazz, String name) {
        try {
            return Class.forName(pathToClazz).getMethod(name);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            return null;
        }
    }
}
