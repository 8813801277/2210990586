package com.AverageCalculator.project.Controller;

import com.AverageCalculator.project.Service.NumberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/numbers")
public class NumberController {

    private final NumberService service = new NumberService();

    @GetMapping("/{id}")
    public ResponseEntity<Map<String,Object>> get(@PathVariable String id){
        return ResponseEntity.ok(service.process_req(id));
    }
}
