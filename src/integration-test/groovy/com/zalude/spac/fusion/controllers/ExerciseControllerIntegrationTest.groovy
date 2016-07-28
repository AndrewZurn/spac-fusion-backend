package com.zalude.spac.fusion.controllers

import com.zalude.spac.fusion.IntegrationTestData;
import com.zalude.spac.fusion.ControllerIntegrationTestBase
import com.zalude.spac.fusion.models.domain.Exercise
import com.zalude.spac.fusion.models.request.CreateOrUpdateExerciseOptionRequest
import com.zalude.spac.fusion.models.request.CreateOrUpdateExerciseRequest
import com.zalude.spac.fusion.repositories.ExerciseRepository
import groovy.transform.CompileStatic
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity

import javax.inject.Inject
import java.util.stream.Collectors

class ExerciseControllerIntegrationTest extends ControllerIntegrationTestBase implements IntegrationTestData {

    String getBasePath() { "/exercises" }

    @Inject
    private ExerciseRepository exerciseRepository

    private CreateOrUpdateExerciseRequest createExerciseRequestBody
    private List<CreateOrUpdateExerciseOptionRequest> createExerciseOptions

    def setup() {
        exerciseRepository.save(testExercise)

        createExerciseOptions = testExerciseOptionsList.stream().map { option ->
            def alternativeOption = new CreateOrUpdateExerciseOptionRequest(null, "Modified: ${option.name}", "Modified ${option.description}", option.type, option.targetAmount, null)
            new CreateOrUpdateExerciseOptionRequest(null, option.name, option.description, option.type, option.targetAmount, alternativeOption)
        }.collect(Collectors.toList())

        createExerciseRequestBody = new CreateOrUpdateExerciseRequest("New Pushup Routine", "Now get on the ground!", createExerciseOptions)
    }

    def cleanup() {
        exerciseRepository.deleteAllInBatch()
    }

    def "get an empty list of workouts"() {
        given:
        exerciseRepository.deleteAll()

        when:
        List<Exercise> result = restTemplate.getForObject(serviceURI(), List.class)

        then:
        result.size() == 0
    }

    def "get a list of workouts"() {
        given:
        when:
        ResponseEntity<Exercise[]> result = restTemplate.getForEntity(serviceURI(), Exercise[].class)
        Exercise actual = result.body[0]

        then:
        result.statusCode.value() == 200
        actual == testExercise
    }

    def "create a new exercise with a set of exercise options"() {
        given:
        exerciseRepository.deleteAll()
        def createExerciseRequest = new HttpEntity<CreateOrUpdateExerciseRequest>(createExerciseRequestBody, headers)

        def createExerciseOptionRequestNames = createExerciseOptions.stream()
                .map { option -> option.name }
                .collect(Collectors.toList())

        when:
        def createExerciseResult = restTemplate.postForEntity(serviceURI(), createExerciseRequest, Exercise.class)

        then:
        createExerciseResult.body.name == createExerciseRequestBody.name
        createExerciseResult.body.description == createExerciseRequestBody.description
        createExerciseResult.body.exerciseOptions.size() == createExerciseOptions.size()
        createExerciseResult.body.exerciseOptions.forEach { option ->
            option.name in createExerciseOptionRequestNames
            option.alternativeForExerciseOption.name == "Modified: ${option.name}"
            option.alternativeForExerciseOption.description == "Modified: ${option.description}"
        }
    }

    def "find the created exercise and options set"() {
        given:
        when:
        List<Exercise> result = restTemplate.getForObject(serviceURI(), List.class)

        then:
        result.size() == 1
        result.get(0).exerciseOptions.size() == createExerciseOptions.size()
    }
}
