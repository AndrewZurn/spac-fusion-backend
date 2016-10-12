package com.zalude.spac.fusion.repositories

import com.zalude.spac.fusion.IntegrationTestData
import com.zalude.spac.fusion.RepositoryBase
import org.springframework.transaction.annotation.Transactional

import javax.inject.Inject

public class UserRepositoryIntegrationTest extends RepositoryBase implements IntegrationTestData {

    @Inject
    private UserRepository userRepository

    @Inject
    private ScheduledWorkoutRepository scheduledWorkoutRepository

    @Inject
    private UserCompletedScheduledWorkoutRepository userCompletedWorkoutLookupRepository

    void setup() {
        scheduledWorkoutRepository.save(this.testScheduledWorkout)
        userRepository.save(testFusionUser)
    }

    void cleanup() {
        userRepository.deleteAllInBatch()
        scheduledWorkoutRepository.deleteAllInBatch()
    }

    def "find an empty list of user"() {
        given:
        userRepository.delete(testFusionUser)

        when:
        def users = userRepository.findAll()

        then:
        users.size() == 0
    }

    @Transactional
    def "find a list of users"() {
        given:
        when:
        def users = userRepository.findAll()
        def user = users.getAt(0)

        then:
        users.size() == 1
        user == testFusionUser
        user.userCompletedScheduledWorkouts == testFusionUser.userCompletedScheduledWorkouts
    }

    @Transactional
    def "get an empty list of user completed workouts"() {
        given:
        userRepository.deleteAllInBatch()
        userCompletedWorkoutLookupRepository.deleteAllInBatch()

        when:
        def lookups = userCompletedWorkoutLookupRepository.findAll()

        then:
        lookups.size() == 0
    }
}
