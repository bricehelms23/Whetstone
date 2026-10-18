package com.whetstone.backend.model;

import java.util.List;

public class AnalysisProject {
    private String projectName;
    private int fileCount;
    private int javaFileCount;
    private List<String> files;

    public AnalysisProject(String projectName, int fileCount, int javaFileCount, List<String> files) {
        this.projectName = projectName;
        this.fileCount = fileCount;
        this.javaFileCount = javaFileCount;
        this.files = files;
    }

    public String getProjectName() {
        return projectName;
    }

    public int getFileCount() {
        return fileCount;
    }

    public int getJavaFileCount() {
        return javaFileCount;
    }

    public List<String> getFiles() {
        return files;
    }

}
