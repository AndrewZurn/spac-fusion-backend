package com.zalude.spac.fusion.repositories

import com.zalude.spac.fusion.IntegrationTestData
import com.zalude.spac.fusion.RepositoryTestBase
import com.zalude.spac.fusion.models.domain.Workout
import org.springframework.transaction.annotation.Transactional

import javax.inject.Inject;

public class WorkoutRepositoryIntegrationTest extends RepositoryTestBase implements IntegrationTestData {

    @Inject
    WorkoutRepository workoutRepository

    def setup() {
        workoutRepository.save(this.testWorkout)
    }

    def cleanup() {
        workoutRepository.deleteAllInBatch()
    }

    def "get an empty list of workouts"() {
        given:
        workoutRepository.deleteAll()

        when:
        Iterable<Workout> exercises = workoutRepository.findAll()

        then:
        exercises.size() == 0
    }

    @Transactional
    def "get a list of workouts and their exercises"() {
        given:
        when:
        Iterable<Workout> exercises = workoutRepository.findAll()

        then:
        exercises.size() == 1
        exercises.getAt(0) == this.testWorkout
    }

    def "get a list of workouts that match by name and not id"() {
        given:
        when:
        Iterable<Workout> emptyExercise = workoutRepository.findByNameAndIdNot(this.testWorkout.name, this.testWorkout.id)
        Iterable<Workout> matchingExercise = workoutRepository.findByNameAndIdNot(this.testWorkout.name, UUID.fromString("02fd700b-5f78-4e4a-aed5-c4a38284082e"))

        then:
        emptyExercise.size() == 0
        matchingExercise.size() == 1
        matchingExercise.get(0) == this.testWorkout
    }
}
