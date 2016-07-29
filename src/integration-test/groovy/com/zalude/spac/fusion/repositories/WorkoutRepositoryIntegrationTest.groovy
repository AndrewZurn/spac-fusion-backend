package com.zalude.spac.fusion.repositories

import com.zalude.spac.fusion.IntegrationTestData
import com.zalude.spac.fusion.RepositoryTestBase
import org.springframework.transaction.annotation.Transactional

import javax.inject.Inject;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
public class WorkoutRepositoryIntegrationTest extends RepositoryTestBase implements IntegrationTestData {

    @Inject
    private WorkoutRepository workoutRepository

    @Inject
    private ExerciseRepository exerciseRepository

    void setup() {
        exerciseRepository.save(testExercise)
        workoutRepository.save(testWorkout)
    }

    void cleanup() {
        workoutRepository.deleteAllInBatch()
    }

    def "find an empty list of workouts"() {
        given:
        workoutRepository.deleteAll()

        when:
        def workouts = workoutRepository.findAll()

        then:
        workouts.size() == 0
    }

    @Transactional
    def "find a list of workouts"() {
        given:
        when:
        def workouts = workoutRepository.findAll()

        then:
        workouts.size() == 1
        workouts.getAt(0) == testWorkout
    }

}
