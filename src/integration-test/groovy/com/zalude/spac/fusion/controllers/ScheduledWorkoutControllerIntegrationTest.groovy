package com.zalude.spac.fusion.controllers

import com.zalude.spac.fusion.ControllerBase
import com.zalude.spac.fusion.IntegrationTestData
import com.zalude.spac.fusion.models.domain.ScheduledWorkout
import com.zalude.spac.fusion.models.request.CreateOrUpdateScheduledWorkoutRequest
import com.zalude.spac.fusion.repositories.ScheduledWorkoutRepository
import com.zalude.spac.fusion.repositories.WorkoutRepository
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus

import javax.inject.Inject
import java.time.LocalDate;

public class ScheduledWorkoutControllerIntegrationTest extends ControllerBase implements IntegrationTestData {

    String getBasePath() { "/scheduled-workouts" }

    @Inject
    private WorkoutRepository workoutRepository

    @Inject
    private ScheduledWorkoutRepository scheduledWorkoutRepository

    CreateOrUpdateScheduledWorkoutRequest createScheduledWorkoutRequestBody
    HttpEntity<CreateOrUpdateScheduledWorkoutRequest> createScheduledWorkoutRequest

    def setup() {
        this.testWorkout = workoutRepository.save(this.testWorkout)
        this.testScheduledWorkout.workout = this.testWorkout
        this.createScheduledWorkoutRequestBody = new CreateOrUpdateScheduledWorkoutRequest(this.testWorkout.id, LocalDate.now())
        this.createScheduledWorkoutRequest = new HttpEntity<CreateOrUpdateScheduledWorkoutRequest>(createScheduledWorkoutRequestBody, headers)
    }

    def cleanup() {
        workoutRepository.deleteAllInBatch()
        scheduledWorkoutRepository.deleteAllInBatch()
    }

    def "save a workout for a given day"() {
        given:
        when:
        def result = restTemplate.postForEntity(serviceURI(), createScheduledWorkoutRequest, ScheduledWorkout.class)

        then:
        result.statusCode == HttpStatus.CREATED
        result.body.active
        result.body.workoutDate == LocalDate.now()
        result.body.workout.id == this.testWorkout.id
    }

    def "not save a workout for a day where a workout already exists"() {
        given:
        this.testScheduledWorkout.workoutDate = LocalDate.now()
        this.scheduledWorkoutRepository.save(this.testScheduledWorkout)

        when:
        def result = restTemplate.postForEntity(serviceURI(), createScheduledWorkoutRequest, ScheduledWorkout.class)

        then:
        result.statusCode == HttpStatus.BAD_REQUEST
    }

    def "deactivate a workout"() {
        given:
        def savedWorkout = this.scheduledWorkoutRepository.save(this.testScheduledWorkout)

        when:
        def result = restTemplate.postForEntity(serviceURI("/${testScheduledWorkout.id}/update-status"), new HttpEntity(null), null)

        then:
        result.statusCode == HttpStatus.NO_CONTENT
    }

    def "not active a workout where a workout is already active for that date"() {
        given:
        this.scheduledWorkoutRepository.save(this.testScheduledWorkout)
        def newWorkout = this.scheduledWorkoutRepository.save(
                new ScheduledWorkout(UUID.randomUUID(), this.testScheduledWorkout.workoutDate,
                        false, this.testScheduledWorkout.workout))

        when:
        def result = restTemplate.postForEntity(serviceURI("/${newWorkout.id}/update-status"), null, null)

        then:
        result.statusCode == HttpStatus.BAD_REQUEST
    }
}
