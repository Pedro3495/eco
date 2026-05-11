package com.eco.goal.controller;

import com.eco.goal.dto.CreateGoalRequest;
import com.eco.goal.dto.GoalResponse;
import com.eco.goal.dto.UpdateGoalProgressRequest;
import com.eco.goal.dto.UpdateGoalRequest;
import com.eco.goal.service.GoalService;
import com.eco.user.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    public List<GoalResponse> findAll(@AuthenticationPrincipal User user) {
        return goalService.findAll(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalResponse create(@RequestBody @Valid CreateGoalRequest request, @AuthenticationPrincipal User user) {
        return goalService.create(request, user);
    }

    @GetMapping("/{id}")
    public GoalResponse findById(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        return goalService.findById(id, user);
    }

    @PutMapping("/{id}")
    public GoalResponse update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateGoalRequest request,
            @AuthenticationPrincipal User user
    ) {
        return goalService.update(id, request, user);
    }

    @PatchMapping("/{id}/progress")
    public GoalResponse updateProgress(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateGoalProgressRequest request,
            @AuthenticationPrincipal User user
    ) {
        return goalService.updateProgress(id, request, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        goalService.archive(id, user);
    }
}

