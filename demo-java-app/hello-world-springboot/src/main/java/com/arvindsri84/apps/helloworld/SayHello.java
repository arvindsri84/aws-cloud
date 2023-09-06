package com.arvindsri84.apps.helloworld;

import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/unsecure")
public class SayHello {

    @Value("${env.name}")
    private String envName;

    @GetMapping(path = "/hello")
    public String Hello(String name) {
        if (name == null || name.trim().length() == 0) {
            return "Hello from the app in " + envName + " environment!";
        }
        return "Hello, " + name + "!";
    }

    @GetMapping(path = "/server/ls")
    public List<String> ls(String dir)  {
        var decodedDir = new String(new Base64().decode(dir.getBytes(StandardCharsets.UTF_8)));
        try {
            return listFiles(decodedDir);
        } catch (Exception e) {
            return List.of("Unable to list files of " + decodedDir, "" + e.getMessage());
        }
    }

    @GetMapping(path = "/server/cat")
    public String cat(String file)  {
        var decodedFilePath = new String(new Base64().decode(file.getBytes(StandardCharsets.UTF_8)));
        try {
            return getContent(decodedFilePath);
        } catch (Exception e) {
            return "Error occurred while reading the file " + decodedFilePath + ". Error: " + e.getMessage();
        }
    }

    private String getContent(String path) throws IOException {
        return String.join("\n", Files.readAllLines(Path.of(path)));
    }

    public List<String> listFiles(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }
}
