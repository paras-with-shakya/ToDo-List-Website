package com.example.todolist.controllers;

import com.example.todolist.models.Todo;
import com.example.todolist.services.TodoService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TodoController {
    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    // HTML page
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("todos", service.list());
        return "index"; // templates/index.html
    }

    @PostMapping("/add")
    public String add(@RequestParam String title, Model model) {
        service.add(title);
        model.addAttribute("todos", service.list());
        return "index"; // renders the updated index.html directly
    }

    @PostMapping("/toggle/{id}")
    public String toggle(@PathVariable long id) {
        service.toggle(id);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable long id) {
        service.delete(id);
        return "redirect:/";
    }

    @PostMapping("/clear-completed")
    public String clearCompleted() {
        service.clearCompleted();
        return "redirect:/";
    }

    // --- Minimal REST API (optional) ---
    @GetMapping(value = "/api/todos", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Todo> apiList() {
        return service.list();
    }

    @PostMapping(value = "/api/todos", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Todo apiAdd(@RequestParam String title) {
        return service.add(title);
    }

    @DeleteMapping("/api/todos/{id}")
    @ResponseBody
    public void apiDelete(@PathVariable long id) {
        service.delete(id);
    }
}
