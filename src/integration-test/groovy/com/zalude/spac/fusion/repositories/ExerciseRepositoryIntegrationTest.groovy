package com.zalude.spac.fusion.repositories

import com.zalude.spac.fusion.IntegrationTestData
import com.zalude.spac.fusion.RepositoryTestBase
import com.zalude.spac.fusion.models.domain.Exercise

import javax.inject.Inject

class ExerciseRepositoryIntegrationTest extends RepositoryTestBase implements IntegrationTestData {

    @Inject
    WorkoutRepository workoutRepository

    @Inject
    ExerciseRepository exerciseRepository

    def setup() {
        workoutRepository.deleteAllInBatch()
        exerciseRepository.deleteAllInBatch()
        workoutRepository.save(this.testWorkout)
    }

    def cleanup() {
        workoutRepository.deleteAllInBatch()
        exerciseRepository.deleteAllInBatch()
    }

    def "find a list of exercise options"() {
        given:
        when:
        Iterable<Exercise> exercises = exerciseRepository.findAll()

        then:
        exercises.size() == this.testExerciseList.size()
    }

    def "save a new exercise to an workout"() {
        given:
        def newExercise = new Exercise(UUID.fromString("2e6d247c-c50e-403d-8a0a-6059000a267c"), "Burpee", "50")
        newExercise.workout = this.testWorkout
        this.testWorkout.exercises += this.testWorkout

        when:
        def savedExercise = exerciseRepository.save(newExercise)

        then:
        savedExercise.id != null
        savedExercise.name == newExercise.name
        savedExercise.workout.id == this.testWorkout.id
        savedExercise.amount == newExercise.amount
    }

    def "find an exercise options by a given exercise"() {
        given:
        when:
        def foundExercises = exerciseRepository.findByWorkoutId(this.testWorkout.id)

        then:
        foundExercises.size() == this.testExerciseList.size()
        foundExercises.each { option -> option.id in this.testExerciseList.collect { it.id } }
    }

    def "find an exercise by a name and not exercise id"() {
        given:
        def nameToFind = this.testExerciseList.get(0).name
        def uuidToNotFind = UUID.fromString("02fd700b-5f78-4e4a-aed5-c4a38284082e")

        when:
        def result = exerciseRepository.findByNameAndWorkoutIdNot(nameToFind, uuidToNotFind).get(0)

        then:
        result.name == nameToFind
        result.id != uuidToNotFind
    }

    def "find exercise by name"() {
        given:
        when:
        def results = exerciseRepository.findByName(this.testExerciseList.get(0).name)

        then:
        results.get(0).name == this.testExerciseList.get(0).name
        results.get(0).amount == this.testExerciseList.get(0).amount
    }
}
