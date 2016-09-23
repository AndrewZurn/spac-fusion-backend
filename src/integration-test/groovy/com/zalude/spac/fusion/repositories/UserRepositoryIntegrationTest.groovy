package com.zalude.spac.fusion.repositories

import com.zalude.spac.fusion.IntegrationTestData
import com.zalude.spac.fusion.RepositoryTestBase
import org.springframework.transaction.annotation.Transactional

import javax.inject.Inject

public class UserRepositoryIntegrationTest extends RepositoryTestBase implements IntegrationTestData {

    @Inject
    private UserRepository userRepository

    @Inject
    private ExerciseRepository exerciseOptionRepository

    @Inject
    private WorkoutWithDateRepository exerciseRepository

    @Inject
    private UserCompletedWorkoutLookupRepository userExerciseOptionLookupRepository

    void setup() {
        def result = exerciseRepository.save(testExercise)

        // set the options that are in our user's list to the newly saved options
        testFusionUser.userCompletedWorkoutLookups.each { option ->
            option.exerciseOption = result.exercises.find { resultOption -> resultOption.name == option.exerciseOption.name }
        }
        userRepository.save(testFusionUser)
    }

    void cleanup() {
        userRepository.deleteAllInBatch()
        exerciseRepository.deleteAllInBatch()
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
        user.userCompletedWorkoutLookups == testFusionUser.userCompletedWorkoutLookups
    }

    @Transactional
    def "get an empty list of user exercise option lookups"() {
        given:
        userRepository.deleteAllInBatch()
        userExerciseOptionLookupRepository.deleteAllInBatch()

        when:
        def lookups = userExerciseOptionLookupRepository.findAll()

        then:
        lookups.size() == 0
    }
}
