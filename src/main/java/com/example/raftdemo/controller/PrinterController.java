package com.example.raftdemo.controller;

import com.example.raftdemo.model.Printer;
import com.example.raftdemo.service.PrinterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing printers.
 * Exposes endpoints to register and list printers.
 */
@RestController
@RequestMapping("/printers")
public class PrinterController {

    private final PrinterService printerService;

    public PrinterController(PrinterService printerService) {
        this.printerService = printerService;
    }

    /**
     * Register a new printer.
     * Example: POST /printers with JSON body {"id":"p1","specs":"MakerBot"}
     */
    @PostMapping
    public Printer registerPrinter(@RequestBody Printer printer) {
        return printerService.addPrinter(printer);
    }

    /**
     * List all registered printers.
     * Example: GET /printers
     */
    @GetMapping
    public List<Printer> listPrinters() {
        return printerService.getAllPrinters();
    }
}