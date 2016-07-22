package com.zalude.spac.fusion.repositories

import com.zalude.spac.fusion.IntegrationTestData
import com.zalude.spac.fusion.RepositoryIntegrationTestBase
import com.zalude.spac.fusion.models.domain.Exercise
import org.springframework.transaction.annotation.Transactional

import javax.inject.Inject;

public class ExerciseRepositoryIntegrationTest extends RepositoryIntegrationTestBase implements IntegrationTestData {

    @Inject
    ExerciseRepository exerciseRepository

    def setup() {
        exerciseRepository.save(testExercise)
    }

    def cleanup() {
        exerciseRepository.deleteAll()
    }

    def "get an empty list of exercises"() {
        given:
        exerciseRepository.deleteAll()

        when:
        Iterable<Exercise> exercises = exerciseRepository.findAll()

        then:
        exercises.size() == 0
    }

    @Transactional
    def "get a list of exercises and their exercise options"() {
        given:
        when:
        Iterable<Exercise> exercises = exerciseRepository.findAll()

        then:
        exercises.size() == 1
        exercises.getAt(0) == testExercise
    }
}
