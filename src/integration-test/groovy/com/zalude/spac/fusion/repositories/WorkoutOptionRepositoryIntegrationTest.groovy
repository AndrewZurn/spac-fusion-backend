package com.zalude.spac.fusion.repositories

import com.zalude.spac.fusion.IntegrationTestData
import com.zalude.spac.fusion.RepositoryTestBase
import com.zalude.spac.fusion.models.domain.Exercise

import javax.inject.Inject

class WorkoutOptionRepositoryIntegrationTest extends RepositoryTestBase implements IntegrationTestData {

    @Inject
    WorkoutWithDateRepository exerciseRepository

    @Inject
    ExerciseRepository exerciseOptionRepository

    def setup() {
        exerciseRepository.deleteAllInBatch()
        exerciseOptionRepository.deleteAllInBatch()
        exerciseRepository.save(testExercise)
    }

    def cleanup() {
        exerciseRepository.deleteAllInBatch()
        exerciseOptionRepository.deleteAllInBatch()
    }

    def "find a list of exercise options"() {
        given:
        when:
        Iterable<Exercise> options = exerciseOptionRepository.findAll()

        then:
        options.size() == testExerciseOptionsList.size()
    }

    def "save a new exercise option to an exercise"() {
        given:
        def newOption = new Exercise(UUID.fromString("2e6d247c-c50e-403d-8a0a-6059000a267c"), "Burpee", "AMRAP")
        newOption.duration = "30 seconds"
        newOption.description = "Some new instructions"
        newOption.workout = testExercise
        testExercise.exercises += testExercise

        when:
        def savedOption = exerciseOptionRepository.save(newOption)

        then:
        savedOption.id != null
        savedOption.name == newOption.name
        savedOption.description == newOption.description
        savedOption.workout.id == testExercise.id
        savedOption.type == newOption.type
        savedOption.amount == newOption.amount
    }

    def "find exercise options by a given exercise"() {
        given:
        when:
        def foundOptions = exerciseOptionRepository.findByWorkoutId(testExercise.id)

        then:
        foundOptions.size() == testExerciseOptionsList.size()
        foundOptions.each { option -> option.id in testExerciseOptionsList.collect { it.id } }
    }

    def "find exercise options by a name and not exercise id"() {
        given:
        def nameToFind = testExerciseOptionsList.get(0).name
        def uuidToNotFind = UUID.fromString("02fd700b-5f78-4e4a-aed5-c4a38284082e")

        when:
        def result = exerciseOptionRepository.findByNameAndWorkoutIdNot(nameToFind, uuidToNotFind).get(0)

        then:
        result.name == nameToFind
        result.id != uuidToNotFind
    }

    def "find exercise options by name"() {
        given:
        when:
        def results = exerciseOptionRepository.findByName(testExerciseOptionsList.get(0).name)

        then:
        results.get(0).name == testExerciseOptionsList.get(0).name
        results.get(0).type == testExerciseOptionsList.get(0).type
        results.get(0).amount == testExerciseOptionsList.get(0).amount
    }
}
