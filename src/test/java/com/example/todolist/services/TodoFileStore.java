package com.example.todolist.services;

import com.example.todolist.models.Todo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class TodoFileStore {
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private Path dataDir;
    private Path dataFile;
    private final Object lock = new Object(); // simple thread-safety

    @PostConstruct
    public void init() throws IOException {
        // store JSON in ./data/todos.json next to your project root (safe for dev)
        dataDir = Paths.get("data");
        if (!Files.exists(dataDir))
            Files.createDirectories(dataDir);

        dataFile = dataDir.resolve("todos.json");
        if (!Files.exists(dataFile)) {
            Files.createFile(dataFile);
            mapper.writerWithDefaultPrettyPrinter().writeValue(dataFile.toFile(), new ArrayList<Todo>());
        }
    }

    public List<Todo> readAll() {
        synchronized (lock) {
            try {
                byte[] json = Files.readAllBytes(dataFile);
                if (json.length == 0)
                    return new ArrayList<>();
                List<Todo> list = mapper.readValue(json, new TypeReference<List<Todo>>() {
                });
                return list != null ? list : new ArrayList<>();
            } catch (IOException e) {
                // for dev: return empty if corrupted; in real app, handle better
                return new ArrayList<>();
            }
        }
    }

    public void writeAll(List<Todo> todos) {
        synchronized (lock) {
            try {
                mapper.writerWithDefaultPrettyPrinter().writeValue(dataFile.toFile(), todos);
            } catch (IOException e) {
                throw new RuntimeException("Failed writing todos.json", e);
            }
        }
    }
}
