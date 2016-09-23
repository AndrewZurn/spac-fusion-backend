package com.zalude.spac.fusion.controllers

import com.zalude.spac.fusion.IntegrationTestData;
import com.zalude.spac.fusion.ControllerTestBase
import com.zalude.spac.fusion.models.domain.Workout
import com.zalude.spac.fusion.models.request.CreateOrUpdateExerciseRequest
import com.zalude.spac.fusion.models.request.CreateOrUpdateWorkoutRequest
import com.zalude.spac.fusion.models.response.ExerciseResponse
import com.zalude.spac.fusion.repositories.WorkoutWithDateRepository
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity

import javax.inject.Inject
import java.util.stream.Collectors

class WorkoutByDateControllerIntegrationTest extends ControllerTestBase implements IntegrationTestData {

    String getBasePath() { "/exercises" }

    @Inject
    private WorkoutWithDateRepository exerciseRepository

    private CreateOrUpdateWorkoutRequest createExerciseRequestBody
    private List<CreateOrUpdateExerciseRequest> createExerciseOptions

    def setup() {
        this.testExercise = exerciseRepository.save(testExercise)

        createExerciseOptions = testExerciseOptionsList.stream().map { option ->
            def alternativeOption = new CreateOrUpdateExerciseRequest(UUID.randomUUID(),
                    "Modified: ${option.name}", "Modified ${option.description}", option.type,
                    option.amount, option.duration, null)
            new CreateOrUpdateExerciseRequest(UUID.randomUUID(), option.name, option.description,
                    option.type, option.amount, option.duration, alternativeOption)
        }.collect(Collectors.toList())

        createExerciseRequestBody = new CreateOrUpdateWorkoutRequest("New Pushup Routine", "Now get on the ground!", createExerciseOptions)
    }

    def cleanup() {
        exerciseRepository.deleteAllInBatch()
    }

    def "get an empty list of workouts"() {
        given:
        exerciseRepository.deleteAll()

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
        actual == testExercise
    }

    def "create a new exercise with a set of exercise options"() {
        given:
        exerciseRepository.deleteAll()
        def createExerciseRequest = new HttpEntity<CreateOrUpdateWorkoutRequest>(createExerciseRequestBody, headers)

        def createExerciseOptionRequestNames = createExerciseOptions.stream()
                .map { option -> option.name }
                .collect(Collectors.toList())

        when:
        def createExerciseResult = restTemplate.postForEntity(serviceURI(), createExerciseRequest, ExerciseResponse.class)

        then:
        createExerciseResult.body.name == createExerciseRequestBody.name
        createExerciseResult.body.instructions == createExerciseRequestBody.instructions
        createExerciseResult.body.exerciseOptions.size() == createExerciseOptions.size()
        createExerciseResult.body.exerciseOptions.forEach { option ->
            option.name in createExerciseOptionRequestNames
            option.inputType == 'number'
            option.alternativeExerciseOption.name == "Modified: ${option.name}"
            option.alternativeExerciseOption.description == "Modified: ${option.description}"
            option.alternativeExerciseOption.inputType == 'number'
        }
    }

    def "find the created exercise and options set"() {
        given:
        when:
        List<Workout> result = restTemplate.getForObject(serviceURI(), List.class)

        then:
        result.size() == 1
        result.get(0).exercises.size() == createExerciseOptions.size()
    }
}
