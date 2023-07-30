package org.trungdd.virtualfilesystem.utils;

import java.util.ArrayList;
import java.util.List;

public class DirectoryUtils {

    public static void main(String[] args) {
        String curDir = "/home/user";
        String targetDir = "Documents";

        String absolutePath = DirectoryUtils.getAbsolutePath(curDir, targetDir);
        System.out.println("Absolute directory: " + absolutePath);
    }

    public static String getAbsolutePath(String curDir, String targetDir) {
        // If targetDir is an absolute path, return it directly
        if (isAbsolutePath(targetDir)) {
            return targetDir;
        }

        // Resolve the absolute path using the current directory and target directory
        String absolutePath = resolvePath(curDir, targetDir);

        return normalizePath(absolutePath);
    }

    private static boolean isAbsolutePath(String path) {
        return path.startsWith("/");
    }

    private static String normalizePath(String path) {
        String[] components = path.split("/");
        List<String> normalizedComponents = new ArrayList<>();

        for (String component : components) {
            if (component.equals("..")) {
                if (!normalizedComponents.isEmpty()) {
                    normalizedComponents.remove(normalizedComponents.size() - 1);
                }
            } else if (!component.equals(".") && !component.isEmpty()) {
                normalizedComponents.add(component);
            }
        }

        if (normalizedComponents.isEmpty()) {
            return "/";
        } else {
            return "/" + String.join("/", normalizedComponents);
        }
    }

    private static String resolvePath(String curDir, String targetDir) {
        if (curDir.endsWith("/")) {
            return curDir + targetDir;
        } else {
            return curDir + "/" + targetDir;
        }
    }

    // Helper method to get the parent path from a given file path
    public static String getParentPath(String filePath) {
        int lastSlashIndex = filePath.lastIndexOf("/");
        if (lastSlashIndex > 0) {
            return filePath.substring(0, lastSlashIndex);
        } else if (lastSlashIndex == 0) {
            return "/";
        }
        return null;
    }

    // Helper method to get the name from a given file path
    public static String getNameFromPath(String filePath) {
        int lastSlashIndex = filePath.lastIndexOf("/");
        if (lastSlashIndex >= 0) {
            return filePath.substring(lastSlashIndex + 1);
        }
        return null;
    }

    public static String nextDir(String curPath, String absolutePath) {
        if (curPath.equals(absolutePath) || curPath.length() > absolutePath.length()) {
            return curPath;
        }

        int nextSlashIndex = absolutePath.indexOf("/", curPath.length() + 1);
        if (nextSlashIndex == -1) {
            return absolutePath;
        }
        return absolutePath.substring(0, nextSlashIndex);
    }
}

