package com.kisman.cc.command;

import java.util.*;

public abstract class Command {
    public String name;
    public String desc;
    public String syntax;

    public List<String> cmds = new ArrayList<String>();

    public Command(String name, String desc, String syntax, String[] cmds) {
        this.name = name;
        this.desc = desc;
        this.syntax = syntax;
        this.cmds = Arrays.asList(cmds);
    }

    public abstract void onCommand(String[] args, String command);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    public List<String> getCmds() {
        return cmds;
    }

    public void setCmds(List<String> cmds) {
        this.cmds = cmds;
    }
}
