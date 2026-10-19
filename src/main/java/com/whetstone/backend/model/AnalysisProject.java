package com.whetstone.backend.model;

import java.util.Map;

public class AnalysisProject {
    private final String projectName;
    private final Map<String, String> fileContents;

    public AnalysisProject(String projectName, Map<String, String> fileContents) {
        this.projectName = projectName;
        this.fileContents = fileContents;
    }

    public String getProjectName() {
        return projectName;
    }

    public int getFileCount() {
        return fileContents.size();
    }

    public int getJavaFileCount() {
        return (int) fileContents.keySet().stream().filter(f -> f.endsWith(".java")).count();
    }

    public Map<String, String> getFileContents() {
        return fileContents;
    }

}
