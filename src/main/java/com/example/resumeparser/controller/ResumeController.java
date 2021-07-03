package com.example.resumeparser.controller;

import com.example.resumeparser.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/resume")
public class ResumeController {

    private ResumeService service;

    @Autowired
    public ResumeController(ResumeService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity checkResumeById(@PathVariable String id) throws IOException {
        boolean isWork = service.checkID(id);
        return isWork == true ? ResponseEntity.ok(service.getResumeById(id)) : ResponseEntity.ok(service.incorrectURL());
    }

    @GetMapping("/{id}/{format}")
    public ResponseEntity returnResumeById(@PathVariable String id, @PathVariable String format) {
        boolean withName = service.withName(format);

        return withName == true ? ResponseEntity.ok(service.getFullResume(id)) :
                ResponseEntity.ok(service.getFullResumeWithoutName(id));
    }
}
