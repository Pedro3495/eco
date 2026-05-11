package com.eco.goal.service;

import com.eco.common.exception.NotFoundException;
import com.eco.goal.dto.CreateGoalRequest;
import com.eco.goal.dto.GoalResponse;
import com.eco.goal.dto.UpdateGoalProgressRequest;
import com.eco.goal.dto.UpdateGoalRequest;
import com.eco.goal.model.Goal;
import com.eco.goal.model.GoalStatus;
import com.eco.goal.repository.GoalRepository;
import com.eco.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GoalService {

    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> findAll(User user) {
        return goalRepository.findAllByUserIdAndStatusNotOrderByCreatedAtDesc(user.getId(), GoalStatus.ARCHIVED)
                .stream()
                .map(GoalResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public GoalResponse findById(UUID id, User user) {
        Goal goal = findGoal(id, user);

        return GoalResponse.fromEntity(goal);
    }

    @Transactional
    public GoalResponse create(CreateGoalRequest request, User user) {
        Goal goal = new Goal(
                request.getName(),
                request.getTargetAmount(),
                request.getCurrentAmount(),
                request.getTargetDate(),
                user
        );

        return GoalResponse.fromEntity(goalRepository.save(goal));
    }

    @Transactional
    public GoalResponse update(UUID id, UpdateGoalRequest request, User user) {
        Goal goal = findGoal(id, user);
        goal.update(
                request.getName(),
                request.getTargetAmount(),
                request.getCurrentAmount(),
                request.getTargetDate(),
                request.getStatus()
        );

        return GoalResponse.fromEntity(goalRepository.save(goal));
    }

    @Transactional
    public GoalResponse updateProgress(UUID id, UpdateGoalProgressRequest request, User user) {
        Goal goal = findGoal(id, user);
        goal.updateProgress(request.getCurrentAmount());

        return GoalResponse.fromEntity(goalRepository.save(goal));
    }

    @Transactional
    public void archive(UUID id, User user) {
        Goal goal = findGoal(id, user);
        goal.archive();
        goalRepository.save(goal);
    }

    private Goal findGoal(UUID id, User user) {
        return goalRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Meta nao encontrada"));
    }
}
