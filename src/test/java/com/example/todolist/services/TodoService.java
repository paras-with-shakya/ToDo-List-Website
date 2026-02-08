package com.example.todolist.services;

import com.example.todolist.models.Todo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TodoService {
    private final TodoFileStore store;
    private final AtomicLong sequence = new AtomicLong(1);

    public TodoService(TodoFileStore store) {
        this.store = store;
        // initialize sequence based on existing IDs (so IDs keep increasing after
        // restart)
        long max = store.readAll().stream().mapToLong(Todo::getId).max().orElse(0L);
        sequence.set(max + 1);
    }

    public List<Todo> list() {
        return store.readAll();
    }

    public Todo add(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        List<Todo> todos = store.readAll();
        Todo t = new Todo(sequence.getAndIncrement(), title.trim(), false);
        todos.add(t);
        store.writeAll(todos);
        return t;
    }

    public void toggle(long id) {
        List<Todo> todos = store.readAll();
        for (Todo t : todos) {
            if (t.getId() == id) {
                t.setCompleted(!t.isCompleted());
                break;
            }
        }
        store.writeAll(todos);
    }

    public void delete(long id) {
        List<Todo> todos = store.readAll();
        todos.removeIf(t -> t.getId() == id);
        store.writeAll(todos);
    }

    public void clearCompleted() {
        List<Todo> todos = store.readAll();
        todos.removeIf(Todo::isCompleted);
        store.writeAll(todos);
    }
}
