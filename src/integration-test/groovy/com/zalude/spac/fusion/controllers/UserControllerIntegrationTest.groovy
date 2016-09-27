package com.zalude.spac.fusion.controllers

import com.zalude.spac.fusion.ControllerTestBase
import com.zalude.spac.fusion.IntegrationTestData
import com.zalude.spac.fusion.models.domain.FusionUser
import com.zalude.spac.fusion.models.request.CreateUserRequest
import com.zalude.spac.fusion.models.request.UpdateUserRequest
import com.zalude.spac.fusion.models.request.UserCompletedScheduledWorkoutRequest
import com.zalude.spac.fusion.models.response.UserCompletedWorkoutResponse
import com.zalude.spac.fusion.repositories.UserRepository
import com.zalude.spac.fusion.repositories.ScheduledWorkoutRepository
import com.zalude.spac.fusion.repositories.WorkoutRepository
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional

import javax.inject.Inject

public class UserControllerIntegrationTest extends ControllerTestBase implements IntegrationTestData {

    @Inject
    private UserRepository userRepository

    @Inject
    private ScheduledWorkoutRepository scheduledWorkoutRepository

    @Inject
    private WorkoutRepository workoutRepository

    String getBasePath() { "/users" }

    private String SCHEDULED_WORKOUTS_PATH = "scheduled-workouts"

    def setup() {
        workoutRepository.save(this.testWorkout)
        scheduledWorkoutRepository.save(this.testScheduledWorkout)
        userRepository.save([testFusionUser, otherTestFusionUser])
    }

    def cleanup() {
        workoutRepository.deleteAllInBatch()
        userRepository.deleteAllInBatch()
        scheduledWorkoutRepository.deleteAllInBatch()
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

    def "find a list of the user's completed workouts"() {
        given:

        when:
        def url = serviceURI("/${testFusionUser.id}/${SCHEDULED_WORKOUTS_PATH}?page=0&pageSize=10")
        def result = restTemplate.getForEntity(serviceURI("/${testFusionUser.id}/${SCHEDULED_WORKOUTS_PATH}?page=0&pageSize=10"), List.class)
        List<UserCompletedWorkoutResponse> completedWorkouts = result.body

        then:
        result.statusCode == HttpStatus.OK
        completedWorkouts.size() == testFusionUser.userCompletedScheduledWorkouts.size()
    }

    def "add a completed workouts"() {
        given:
        def completedWorkoutRequest = new UserCompletedScheduledWorkoutRequest("25")

        when:
        def result = restTemplate.postForEntity(serviceURI("/${otherTestFusionUser.id}/${SCHEDULED_WORKOUTS_PATH}/${this.testScheduledWorkout.id}"),
                completedWorkoutRequest, UserCompletedWorkoutResponse.class)
        def userCompletedWorkout = result.body

        then:
        result.statusCode == HttpStatus.CREATED
        userCompletedWorkout.result == "25"
        userCompletedWorkout.scheduledWorkout == this.testScheduledWorkout
    }

    def "update a completed workouts"() {
        given:
        def newResultValue = "52"
        def completedWorkoutRequest = new UserCompletedScheduledWorkoutRequest("25")
        def createResult = restTemplate.postForEntity(serviceURI("/${otherTestFusionUser.id}/${SCHEDULED_WORKOUTS_PATH}/${this.testScheduledWorkout.id}"),
                completedWorkoutRequest, UserCompletedWorkoutResponse.class)
        def createBody = createResult.body
        def updateRequest = new UserCompletedScheduledWorkoutRequest("25")
        updateRequest.result = newResultValue

        when:
        def updateResult = restTemplate.postForEntity(serviceURI("/${otherTestFusionUser.id}/${SCHEDULED_WORKOUTS_PATH}/${this.testScheduledWorkout.id}"),
                updateRequest, UserCompletedWorkoutResponse.class)
        def updateBody = updateResult.body

        then:
        updateBody.scheduledWorkout == createBody.scheduledWorkout
        updateBody.result == newResultValue
    }

}
