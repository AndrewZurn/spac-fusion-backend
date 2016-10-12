package com.zalude.spac.fusion.controllers

import com.zalude.spac.fusion.IntegrationTestData;
import com.zalude.spac.fusion.ControllerBase
import com.zalude.spac.fusion.models.domain.Workout
import com.zalude.spac.fusion.models.request.CreateOrUpdateExerciseRequest
import com.zalude.spac.fusion.models.request.CreateOrUpdateWorkoutRequest
import com.zalude.spac.fusion.repositories.WorkoutRepository
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity

import javax.inject.Inject
import java.util.stream.Collectors

class WorkoutControllerIntegrationTest extends ControllerBase implements IntegrationTestData {

    String getBasePath() { "/workouts" }

    @Inject
    private WorkoutRepository workoutRepository

    private CreateOrUpdateWorkoutRequest createWorkoutRequestBody
    private List<CreateOrUpdateExerciseRequest> createExercises

    def setup() {
        this.testWorkout = workoutRepository.save(this.testWorkout)

        createExercises = this.testExerciseList.stream()
                .map { exercise -> new CreateOrUpdateExerciseRequest(UUID.randomUUID(), exercise.name, "50", null, null) }
                .collect(Collectors.toList())

        createWorkoutRequestBody = new CreateOrUpdateWorkoutRequest("New Pushup Routine", "Now get on the ground!",
                "2 minutes", "AMRAP", createExercises)
    }

    def cleanup() {
        workoutRepository.deleteAllInBatch()
    }

    def "get an empty list of workouts"() {
        given:
        workoutRepository.deleteAll()

        when:
        List<Workout> result = restTemplate.getForObject(serviceURI(), List.class)

        then:
        result.size() == 0
    }

    def "get a list of workouts"() {
        given:
        when:
        ResponseEntity<Workout[]> result = restTemplate.getForEntity(serviceURI(), Workout[].class)
        Workout actual = result.body[0]

        then:
        result.statusCode.value() == 200
        actual == this.testWorkout
    }

    def "create a new workout"() {
        given:
        workoutRepository.deleteAll()
        def createExerciseRequest = new HttpEntity<CreateOrUpdateWorkoutRequest>(createWorkoutRequestBody, headers)

        def createExerciseOptionRequestNames = createExercises.stream()
                .map { option -> option.name }
                .collect(Collectors.toList())

        when:
        def createWorkoutResult = restTemplate.postForEntity(serviceURI(), createExerciseRequest, Workout.class)

        then:
        createWorkoutResult.body.name == createWorkoutRequestBody.name
        createWorkoutResult.body.instructions == createWorkoutRequestBody.instructions
        createWorkoutResult.body.exercises.size() == createExercises.size()
        createWorkoutResult.body.exercises.forEach { exercise ->
            exercise.name in createExerciseOptionRequestNames
            exercise.amount == "50"
        }
    }

    def "find the created workout and exercises"() {
        given:
        when:
        List<Workout> result = restTemplate.getForObject(serviceURI(), List.class)

        then:
        result.size() == 1
        result.get(0).exercises.size() == createExercises.size()
    }
}
