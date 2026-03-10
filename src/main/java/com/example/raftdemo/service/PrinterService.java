package com.example.raftdemo.service;

import com.example.raftdemo.model.Printer;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service layer for managing printers.
 * Keeps printer metadata in memory for now.
 */
@Service
public class PrinterService {

    private final Map<String, Printer> printers = new HashMap<>();

    public Printer addPrinter(Printer printer) {
        printers.put(printer.getId(), printer);
        return printer;
    }

    public List<Printer> getAllPrinters() {
        return new ArrayList<>(printers.values());
    }
}