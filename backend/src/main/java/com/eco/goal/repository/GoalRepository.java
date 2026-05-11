package com.eco.goal.repository;

import com.eco.goal.model.Goal;
import com.eco.goal.model.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {

    List<Goal> findAllByUserIdAndStatusNotOrderByCreatedAtDesc(UUID userId, GoalStatus status);

    Optional<Goal> findByIdAndUserId(UUID id, UUID userId);
}

