package com.zalude.spac.fusion.controllers

import com.zalude.spac.fusion.ControllerTestBase
import com.zalude.spac.fusion.IntegrationTestData
import com.zalude.spac.fusion.models.domain.FusionUser
import com.zalude.spac.fusion.models.request.CreateUserRequest
import com.zalude.spac.fusion.models.request.UpdateCompletedWorkoutRequest
import com.zalude.spac.fusion.models.request.UpdateUserRequest
import com.zalude.spac.fusion.models.request.UpdateUserStatusRequest
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
        List<FusionUser> userList = result.body

        then:
        result.statusCode == HttpStatus.OK
        userList.size() == 2
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

    def "create a user"() {
        given:
        CreateUserRequest createUserRequest = new CreateUserRequest("William", "Overturn", "w.o", "w.o@email.com", 35, 5.10, 158, 1)

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
        createdUser.programLevel == createUserRequest.programLevel
        createdUser.activeStatus
    }

    def "update a user with only provided values"() {
        given:
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("Andy", "McBeth", null, "andy.mcbeth@email.com", 26, null, 200, 2)

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
        updatedUser.programLevel == updateUserRequest.programLevel
    }

    def "update a user's status"() {
        given:
        when:
        def setInactiveResult = restTemplate.exchange(serviceURI("/${testFusionUser.id}/status"),
                HttpMethod.PUT, new HttpEntity(new UpdateUserStatusRequest(false)), Void.class)
        def setInactiveFindResult = restTemplate.getForEntity(serviceURI("/${testFusionUser.id}"), FusionUser.class)
        def setActiveResult = restTemplate.exchange(serviceURI("/${testFusionUser.id}/status"),
                HttpMethod.PUT, new HttpEntity(new UpdateUserStatusRequest(true)), Void.class)
        def setActiveFindResult = restTemplate.getForEntity(serviceURI("/${testFusionUser.id}"), FusionUser.class)

        then:
        setInactiveResult.statusCode == HttpStatus.NO_CONTENT
        setActiveResult.statusCode == HttpStatus.NO_CONTENT
        setInactiveFindResult.statusCode == HttpStatus.OK
        setActiveFindResult.statusCode == HttpStatus.OK

        // check users
        !setInactiveFindResult.body.activeStatus
        setActiveFindResult.body.activeStatus
    }

    def "find a list of the user's completed workout/exercise option lookups"() {
        given:
        when:
        def result = restTemplate.getForEntity(serviceURI("/${testFusionUser.id}/workouts"), List.class)
        List<UserCompletedWorkoutResponse> completedWorkouts = result.body

        then:
        result.statusCode == HttpStatus.OK
        completedWorkouts.size() == testFusionUser.userExerciseOptionLookups.size()
    }

    def "add a completed workout/exercise option lookups for a user"() {
        given:
        def createCompletedWorkoutRequest = new UserCompletedWorkoutRequest(testWorkout.exercise.exerciseOptions.collect { option ->
            new UserCompletedWorkoutRequest.CompletedExerciseResultRequest(option.id, "25")
        })

        def lookupResponseList = testWorkout.exercise.exerciseOptions.collect { option ->
            new UserCompletedWorkoutResponse.CompletedExerciseOptionResponse(option.id, option.name, option.description, option.type, option.targetAmount, "25")
        }
        def expectedResponse = new UserCompletedWorkoutResponse(testWorkout.id, testWorkout.duration, testWorkout.workoutDate,
                testWorkout.exercise.id, testWorkout.exercise.name, testWorkout.exercise.description, lookupResponseList)

        when:
        def result = restTemplate.postForEntity(serviceURI("/${otherTestFusionUser.id}/workouts/${testWorkout.id}"), createCompletedWorkoutRequest, UserCompletedWorkoutResponse.class)
        def userCompletedWorkout = result.body

        then:
        result.statusCode == HttpStatus.CREATED
        userCompletedWorkout.workoutId == expectedResponse.workoutId
        userCompletedWorkout.duration == expectedResponse.duration
        userCompletedWorkout.completedDate == expectedResponse.completedDate
        userCompletedWorkout.exerciseId == expectedResponse.exerciseId
        userCompletedWorkout.exerciseName == expectedResponse.exerciseName
        userCompletedWorkout.exerciseDescription == expectedResponse.exerciseDescription
        userCompletedWorkout.results.each { completedResult ->
            def expectedResponseResult = expectedResponse.results.find {
                it.exerciseOptionId == completedResult.exerciseOptionId
            }
            completedResult.exerciseOptionId == expectedResponseResult.exerciseOptionId
            completedResult.amountCompleted == expectedResponseResult.amountCompleted
            completedResult.description == expectedResponseResult.description
            completedResult.name == expectedResponseResult.name
            completedResult.targetAmount == expectedResponseResult.targetAmount
            completedResult.type == expectedResponseResult.type
        }
    }

    def "update a completed workout/exercise option lookups for a user"() {
        given:
        def newAmountCompleted = "500"
        def request = new UpdateCompletedWorkoutRequest(newAmountCompleted)
        def updateUrl = serviceURI("/${testFusionUser.id}/workouts/lookup/${testUserExerciseOptionLookup.id}")
        def findUrl = serviceURI("/${testFusionUser.id}/workouts/${testWorkout.id}")

        when:
        def result = restTemplate.postForEntity(updateUrl, request, Void.class)
        def findResult = restTemplate.getForEntity(findUrl, UserCompletedWorkoutResponse.class)
        def userCompletedWorkout = findResult.body

        then:
        result.statusCode == HttpStatus.NO_CONTENT
        findResult.statusCode == HttpStatus.OK
        userCompletedWorkout.results.find {
            it.exerciseOptionId == testUserExerciseOptionLookup.exerciseOption.id
        }.amountCompleted == newAmountCompleted
    }
}
