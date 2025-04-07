package com.example;

import java.io.Serializable;

// Make MethodInfo serializable to support saving/loading user methods
class MethodInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String name;
    private final String description;
    private final String parameters;
    private final String example;
    private final String useCases;
    
    public MethodInfo(String name, String description, String parameters, String example, String useCases) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
        this.example = example;
        this.useCases = useCases;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getParameters() { return parameters; }
    public String getExample() { return example; }
    public String getUseCases() { return useCases; }

}