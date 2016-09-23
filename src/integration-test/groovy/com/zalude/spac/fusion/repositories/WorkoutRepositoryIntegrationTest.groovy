package com.zalude.spac.fusion.repositories

import com.zalude.spac.fusion.IntegrationTestData
import com.zalude.spac.fusion.RepositoryTestBase
import com.zalude.spac.fusion.models.domain.Workout
import org.springframework.transaction.annotation.Transactional

import javax.inject.Inject;

public class WorkoutWithDateRepositoryIntegrationTest extends RepositoryTestBase implements IntegrationTestData {

    @Inject
    WorkoutWithDateRepository exerciseRepository

    def setup() {
        exerciseRepository.save(testExercise)
    }

    def cleanup() {
        exerciseRepository.deleteAllInBatch()
    }

    def "get an empty list of exercises"() {
        given:
        exerciseRepository.deleteAll()

        when:
        Iterable<Workout> exercises = exerciseRepository.findAll()

        then:
        exercises.size() == 0
    }

    @Transactional
    def "get a list of exercises and their exercise options"() {
        given:
        when:
        Iterable<Workout> exercises = exerciseRepository.findAll()

        then:
        exercises.size() == 1
        exercises.getAt(0) == testExercise
    }

    def "get a list of exercises that match by name and not id"() {
        given:
        when:
        Iterable<Workout> emptyExercise = exerciseRepository.findByNameAndIdNot(testExercise.name, testExercise.id)
        Iterable<Workout> matchingExercise = exerciseRepository.findByNameAndIdNot(testExercise.name, UUID.fromString("02fd700b-5f78-4e4a-aed5-c4a38284082e"))

        then:
        emptyExercise.size() == 0
        matchingExercise.size() == 1
        matchingExercise.get(0) == testExercise
    }
}
