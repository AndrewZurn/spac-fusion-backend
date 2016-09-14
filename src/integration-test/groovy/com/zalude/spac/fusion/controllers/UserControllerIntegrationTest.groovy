package com.zalude.spac.fusion.controllers

import com.zalude.spac.fusion.ControllerTestBase
import com.zalude.spac.fusion.IntegrationTestData
import com.zalude.spac.fusion.models.domain.FusionUser
import com.zalude.spac.fusion.models.request.CreateUserRequest
import com.zalude.spac.fusion.models.request.UpdateUserRequest
import com.zalude.spac.fusion.models.request.UserCompletedWorkoutRequest
import com.zalude.spac.fusion.models.response.UserCompletedWorkoutResponse
import com.zalude.spac.fusion.repositories.ExerciseRepository
import com.zalude.spac.fusion.repositories.UserRepository
import com.zalude.spac.fusion.repositories.WorkoutRepository
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

import javax.inject.Inject;

public class UserControllerIntegrationTest extends ControllerTestBase implements IntegrationTestData {

    @Inject
    private UserRepository userRepository

    @Inject
    private WorkoutRepository workoutRepository

    @Inject
    private ExerciseRepository exerciseRepository

    String getBasePath() { "/users" }

    def setup() {
        def result = exerciseRepository.save(testExercise)

        // set the options that are in our user's list to the newly saved options
        testFusionUser.userExerciseOptionLookups.each { option ->
            option.exerciseOption = result.exerciseOptions.find { resultOption -> resultOption.name == option.exerciseOption.name }
        }

        // remove seeded users
        userRepository.deleteAllInBatch()

        workoutRepository.save(testWorkout)
        userRepository.save(testFusionUser)
        userRepository.save(otherTestFusionUser)
    }

    def cleanup() {
        exerciseRepository.deleteAllInBatch()
        userRepository.deleteAllInBatch()
        workoutRepository.deleteAllInBatch()
    }

    def "get a list of users"() {
        given:
        when:
        def result = restTemplate.getForEntity(serviceURI(), List.class)

        then:
        result.statusCode == HttpStatus.OK
        result.body.size() == 2
    }

    def "find a user"() {
        given:
        when:
        def result = restTemplate.getForEntity(serviceURI("/${testFusionUser.id}"), FusionUser.class)
        def user = result.body

        then:
        result.statusCode == HttpStatus.OK
        user == testFusionUser
    }

    def "find a user by their auth0Id"() {
        given:
        when:
        def result = restTemplate.getForEntity(serviceURI("/auth0/${testFusionUser.auth0Id}"), FusionUser.class)
        def user = result.body

        then:
        result.statusCode == HttpStatus.OK
        user == testFusionUser
    }

    def "create a user"() {
        given:
        CreateUserRequest createUserRequest = new CreateUserRequest("123sdfa", "Andrew", "Zurn", "w.o", "w.o@email.com", "BRONZE")

        when:
        def result = restTemplate.postForEntity(serviceURI(), createUserRequest, FusionUser.class)
        def createdUser = result.body

        then:
        result.statusCode == HttpStatus.CREATED
        createdUser.id != null
        createdUser.firstName == createUserRequest.firstName
        createdUser.lastName == createUserRequest.lastName
        createdUser.username == createUserRequest.username
        createdUser.email == createUserRequest.email
        createdUser.age == createUserRequest.age
        createdUser.weight == createUserRequest.weight
        createdUser.height == createUserRequest.height
        createdUser.programLevel == FusionUser.ProgramLevel.BRONZE.name
    }

    def "update a user with only provided values"() {
        given:
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("as234124", "Andy", "McBeth", null, "andy.mcbeth@email.com", 26, null, 200, "SILVER")

        when:
        def result = restTemplate.exchange(serviceURI("/${testFusionUser.id}"), HttpMethod.PUT, new HttpEntity(updateUserRequest), FusionUser.class)
        def updatedUser = result.body

        then:
        result.statusCode == HttpStatus.OK
        updatedUser.id == testFusionUser.id
        updatedUser.firstName == updateUserRequest.firstName
        updatedUser.lastName == updateUserRequest.lastName
        updatedUser.username == testFusionUser.username
        updatedUser.email == updateUserRequest.email
        updatedUser.age == updateUserRequest.age
        updatedUser.height == testFusionUser.height
        updatedUser.weight == updateUserRequest.weight
        updatedUser.programLevel == FusionUser.ProgramLevel.SILVER.name
    }

    def "find a list of the user's completed workout/exercise option lookups"() {
        given:
        when:
        def result = restTemplate.getForEntity(serviceURI("/${testFusionUser.id}/workouts?page=0&pageSize=10"), List.class)
        List<UserCompletedWorkoutResponse> completedWorkouts = result.body

        then:
        result.statusCode == HttpStatus.OK
        completedWorkouts.size() == testFusionUser.userExerciseOptionLookups.size()
    }

    def "add a completed workout/exercise option lookups for a user"() {
        given:
        def completedWorkoutRequest = createCompletedWorkoutRequest()

        def lookupResponseList = lookupResponses()
        def expectedExerciseResponse = exerciseResponse(lookupResponseList)
        def expectedResponse = workoutResponse(expectedExerciseResponse)

        when:
        def result = restTemplate.postForEntity(serviceURI("/${otherTestFusionUser.id}/workouts/${testWorkout.id}"),
                completedWorkoutRequest, UserCompletedWorkoutResponse.class)
        def userCompletedWorkout = result.body

        then:
        result.statusCode == HttpStatus.CREATED
        userCompletedWorkout.workoutId == expectedResponse.workoutId
        userCompletedWorkout.duration == expectedResponse.duration
        userCompletedWorkout.completedDate == expectedResponse.completedDate
        userCompletedWorkout.exercise.id == expectedResponse.exercise.id
        userCompletedWorkout.exercise.name == expectedResponse.exercise.name
        userCompletedWorkout.exercise.instructions == expectedResponse.exercise.instructions
        userCompletedWorkout.exercise.results.each { completedResult ->
            def expectedResponseResult = expectedResponse.exercise.results.find {
                it.exerciseOptionId == completedResult.exerciseOptionId
            }
            completedResult.lookupId != null
            completedResult.exerciseOptionId == expectedResponseResult.exerciseOptionId
            completedResult.result == expectedResponseResult.result
            completedResult.description == expectedResponseResult.description
            completedResult.name == expectedResponseResult.name
            completedResult.targetAmount == expectedResponseResult.targetAmount
            completedResult.type == expectedResponseResult.type
        }
    }

    def "update a completed workout/exercise option lookups for a user"() {
        given:
        def newResultValue = "52"
        def completedWorkoutRequest = createCompletedWorkoutRequest()
        def createResult = restTemplate.postForEntity(serviceURI("/${otherTestFusionUser.id}/workouts/${testWorkout.id}"),
                completedWorkoutRequest, UserCompletedWorkoutResponse.class)
        def createBody = createResult.body
        def updateRequest = createCompletedWorkoutRequest()
        updateRequest.results = updateRequest.results.each { result ->
            result.lookupId = createBody.exercise.results.find { it.exerciseOptionId == result.exerciseOptionId }.lookupId
            result.result = newResultValue
        }

        when:
        def updateResult = restTemplate.postForEntity(serviceURI("/${otherTestFusionUser.id}/workouts/${testWorkout.id}"),
                updateRequest, UserCompletedWorkoutResponse.class)
        def updateBody = updateResult.body

        then:
        updateBody.workoutId == createBody.workoutId
        updateBody.exercise.results.size() == 3
        updateBody.exercise.results.lookupId == createBody.exercise.results.lookupId
        updateBody.exercise.results.each { it.result == newResultValue }
        updateBody.exercise.results.result != createBody.exercise.results.result
    }

    private UserCompletedWorkoutResponse workoutResponse(UserCompletedWorkoutResponse.CompletedExerciseResponse expectedExerciseResponse) {
        new UserCompletedWorkoutResponse(testWorkout.id, testWorkout.duration,
                testWorkout.workoutDate, expectedExerciseResponse, testWorkout.previewText)
    }

    private UserCompletedWorkoutResponse.CompletedExerciseResponse exerciseResponse(List<UserCompletedWorkoutResponse.CompletedExerciseOptionResponse> lookupResponseList) {
        new UserCompletedWorkoutResponse.CompletedExerciseResponse(testWorkout.exercise.id,
                testWorkout.exercise.name, testWorkout.exercise.instructions, lookupResponseList)
    }

    private List<UserCompletedWorkoutResponse.CompletedExerciseOptionResponse> lookupResponses() {
        testWorkout.exercise.exerciseOptions.collect { option ->
            new UserCompletedWorkoutResponse.CompletedExerciseOptionResponse(UUID.randomUUID(), option.id, option.name,
                    option.type, "25", option.description, option.targetAmount, option.duration)
        }
    }

    private UserCompletedWorkoutRequest createCompletedWorkoutRequest() {
        new UserCompletedWorkoutRequest(testWorkout.exercise.exerciseOptions.collect { option ->
            new UserCompletedWorkoutRequest.CompletedExerciseResultRequest(null, option.id, "25")
        })
    }
}
