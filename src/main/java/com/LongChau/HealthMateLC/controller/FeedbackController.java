package com.LongChau.HealthMateLC.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.LongChau.HealthMateLC.dto.FeedbackRequest;
import com.LongChau.HealthMateLC.model.Feedback;
import com.LongChau.HealthMateLC.service.FeedbackService;

@RestController
@RequestMapping("/api/survey-feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Feedback> submitFeedback(@RequestBody FeedbackRequest request) {
        Feedback created = feedbackService.createFeedback(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
