package com.eco.goal.service;

import com.eco.common.exception.BusinessException;
import com.eco.common.exception.NotFoundException;
import com.eco.goal.dto.CreateGoalRequest;
import com.eco.goal.dto.GoalResponse;
import com.eco.goal.dto.UpdateGoalProgressRequest;
import com.eco.goal.dto.UpdateGoalRequest;
import com.eco.goal.model.Goal;
import com.eco.goal.model.GoalStatus;
import com.eco.goal.repository.GoalRepository;
import com.eco.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    private final User user = new User("Usuario Dev", "dev@eco.com", "hash");

    @Test
    void createShouldSaveActiveGoal() {
        CreateGoalRequest request = createGoalRequest("Reserva", "10000.00", "2500.00");

        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GoalResponse response = goalService.create(request, user);

        assertThat(response.getName()).isEqualTo("Reserva");
        assertThat(response.getTargetAmount()).isEqualByComparingTo(new BigDecimal("10000.00"));
        assertThat(response.getCurrentAmount()).isEqualByComparingTo(new BigDecimal("2500.00"));
        assertThat(response.getStatus()).isEqualTo(GoalStatus.ACTIVE);
        assertThat(response.getProgressPercent()).isEqualByComparingTo(new BigDecimal("25.00"));
    }

    @Test
    void updateProgressShouldCompleteGoalWhenCurrentAmountReachesTarget() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal("Reserva", new BigDecimal("10000.00"), new BigDecimal("2500.00"), LocalDate.of(2026, 12, 31), user);
        UpdateGoalProgressRequest request = updateProgressRequest("10000.00");

        when(goalRepository.findByIdAndUserId(goalId, user.getId())).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GoalResponse response = goalService.updateProgress(goalId, request, user);

        assertThat(response.getCurrentAmount()).isEqualByComparingTo(new BigDecimal("10000.00"));
        assertThat(response.getStatus()).isEqualTo(GoalStatus.COMPLETED);
        assertThat(response.getProgressPercent()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void updateProgressShouldRejectArchivedGoal() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal("Reserva", new BigDecimal("10000.00"), new BigDecimal("2500.00"), null, user);
        goal.archive();
        UpdateGoalProgressRequest request = updateProgressRequest("3000.00");

        when(goalRepository.findByIdAndUserId(goalId, user.getId())).thenReturn(Optional.of(goal));

        assertThatThrownBy(() -> goalService.updateProgress(goalId, request, user))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Meta arquivada nao pode receber progresso");
    }

    @Test
    void findAllShouldIgnoreArchivedGoals() {
        Goal activeGoal = new Goal("Reserva", new BigDecimal("10000.00"), new BigDecimal("2500.00"), null, user);

        when(goalRepository.findAllByUserIdAndStatusNotOrderByCreatedAtDesc(user.getId(), GoalStatus.ARCHIVED))
                .thenReturn(List.of(activeGoal));

        List<GoalResponse> response = goalService.findAll(user);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getStatus()).isEqualTo(GoalStatus.ACTIVE);
    }

    @Test
    void findByIdShouldThrowNotFoundWhenGoalBelongsToAnotherUser() {
        UUID goalId = UUID.randomUUID();

        when(goalRepository.findByIdAndUserId(goalId, user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.findById(goalId, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Meta nao encontrada");
    }

    @Test
    void archiveShouldSetGoalAsArchived() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal("Reserva", new BigDecimal("10000.00"), new BigDecimal("2500.00"), null, user);

        when(goalRepository.findByIdAndUserId(goalId, user.getId())).thenReturn(Optional.of(goal));

        goalService.archive(goalId, user);

        assertThat(goal.getStatus()).isEqualTo(GoalStatus.ARCHIVED);
        verify(goalRepository).save(goal);
    }

    @Test
    void updateShouldEditGoal() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal("Reserva", new BigDecimal("10000.00"), new BigDecimal("2500.00"), null, user);
        UpdateGoalRequest request = updateGoalRequest("Reserva nova", "12000.00", "3000.00", GoalStatus.ACTIVE);

        when(goalRepository.findByIdAndUserId(goalId, user.getId())).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GoalResponse response = goalService.update(goalId, request, user);

        assertThat(response.getName()).isEqualTo("Reserva nova");
        assertThat(response.getTargetAmount()).isEqualByComparingTo(new BigDecimal("12000.00"));
        assertThat(response.getCurrentAmount()).isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(response.getStatus()).isEqualTo(GoalStatus.ACTIVE);
    }

    private CreateGoalRequest createGoalRequest(String name, String targetAmount, String currentAmount) {
        CreateGoalRequest request = new CreateGoalRequest();
        ReflectionTestUtils.setField(request, "name", name);
        ReflectionTestUtils.setField(request, "targetAmount", new BigDecimal(targetAmount));
        ReflectionTestUtils.setField(request, "currentAmount", new BigDecimal(currentAmount));
        ReflectionTestUtils.setField(request, "targetDate", LocalDate.of(2026, 12, 31));
        return request;
    }

    private UpdateGoalRequest updateGoalRequest(String name, String targetAmount, String currentAmount, GoalStatus status) {
        UpdateGoalRequest request = new UpdateGoalRequest();
        ReflectionTestUtils.setField(request, "name", name);
        ReflectionTestUtils.setField(request, "targetAmount", new BigDecimal(targetAmount));
        ReflectionTestUtils.setField(request, "currentAmount", new BigDecimal(currentAmount));
        ReflectionTestUtils.setField(request, "targetDate", LocalDate.of(2026, 12, 31));
        ReflectionTestUtils.setField(request, "status", status);
        return request;
    }

    private UpdateGoalProgressRequest updateProgressRequest(String currentAmount) {
        UpdateGoalProgressRequest request = new UpdateGoalProgressRequest();
        ReflectionTestUtils.setField(request, "currentAmount", new BigDecimal(currentAmount));
        return request;
    }
}

