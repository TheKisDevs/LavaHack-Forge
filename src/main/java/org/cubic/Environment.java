package org.cubic;

import java.net.URL;

/**
 * Useless? Idk - Cubic
 * @author Cubic
 * @since 19.07.2022
 */
public class Environment {

    /**
     * The jar environment:
     * The ClassLoader is loading the classes from
     * a jar file. Thus, a jar is used.
     */
    public static final String JAR = "jar";

    public static final Environment JAR_ENV = new Environment(JAR);

    /**
     * The class environment:
     * The ClassLoader is loading classes from local
     * class files located in a directory.
     */
    public static final String CLASS = "class";

    public static final Environment CLASS_ENV = new Environment(CLASS);

    /**
     * The intellij environment:
     * This environment indicates that the current
     * java application is executed using intellj.
     *
     * NOTE: Intellij creates localized class files and the
     * ClassLoader will load the class from there. Intellij
     * does not create a jar file when hitting the "run" button.
     */
    public static final String INTELLIJ = "intellij";

    public static final Environment INTELLIJ_ENV = new Environment(INTELLIJ);



    private final String environment;

    public Environment(String environment){
        this.environment = environment;
    }

    public String getEnvironment(){
        return environment;
    }

    public boolean isJarEnv(){
        return environment.equals(JAR);
    }

    public boolean isClassEnv(){
        return environment.equals(CLASS);
    }

    public boolean isIntellijEnv(){
        return environment.equals(INTELLIJ);
    }

    /**
     * Resolves the current environment
     * @return the current environment
     */
    public static Environment currentEnvironment(){
        URL url = Thread.currentThread().getContextClassLoader().getResource("com/kisman/cc");
        if(url == null)
            // in case this happened, someone probably renamed a package
            // make sure the package actually exists
            throw new Error("Impossible state reached");
        if(url.getProtocol().equals("file"))
            return CLASS_ENV;
        if(url.getProtocol().equals("jar"))
            return JAR_ENV;
        if(System.getProperty("java.class.path").contains("idea_rt.jar"))
            return INTELLIJ_ENV;

        // if this happens something isn't going right at all
        throw new RuntimeException("Could not resolve the current environment");
    }
}
