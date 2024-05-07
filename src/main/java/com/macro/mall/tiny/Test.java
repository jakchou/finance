package com.macro.mall.tiny;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhouzz
 */
public class Test {

    public static void main(String[] args) throws IOException {


        List<String> list =new ArrayList<>();
        List<String> a=list.stream().filter(e->"a".equals(e)).collect(Collectors.toList());
        a.addAll(new ArrayList<>());
    }

    private static List<File> findFiles(String directoryPath) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith("pom.xml"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
    }

    private static void addCommentToFile(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()));


        if (file.getPath().contains("pom.xml")){
            System.out.println(file.getPath());
        }

        // 添加注释到原有内容之前
        //content = comment + content;

        /*try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }*/
    }

}
