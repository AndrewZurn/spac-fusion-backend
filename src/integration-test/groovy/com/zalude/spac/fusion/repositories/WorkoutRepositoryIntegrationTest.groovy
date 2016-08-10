package com.zalude.spac.fusion.repositories

import com.zalude.spac.fusion.IntegrationTestData
import com.zalude.spac.fusion.RepositoryTestBase
import com.zalude.spac.fusion.models.domain.Workout
import fj.data.Collectors
import org.springframework.transaction.annotation.Transactional

import javax.inject.Inject
import java.time.DayOfWeek
import java.time.LocalDate

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
        //workoutRepository.deleteAllInBatch()
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

    def "find today's workout"() {
        given:
        when:
        def todaysWorkout = workoutRepository.findTodaysWorkout()

        then:
        todaysWorkout.workoutDate == LocalDate.now()
    }

    def "find this week's workouts"() {
        given:
        workoutRepository.deleteAllInBatch()

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate tuesday = LocalDate.now().with(DayOfWeek.TUESDAY);
        LocalDate wednesday = LocalDate.now().with(DayOfWeek.WEDNESDAY);
        LocalDate thursday = LocalDate.now().with(DayOfWeek.THURSDAY);
        LocalDate friday = LocalDate.now().with(DayOfWeek.FRIDAY);
        LocalDate saturday = LocalDate.now().with(DayOfWeek.SATURDAY);
        LocalDate sunday = LocalDate.now().with(DayOfWeek.SUNDAY);
        def daysOfWeek = [monday, tuesday, wednesday, thursday, friday, saturday, sunday]
        def workoutsToSave = daysOfWeek.stream().map { day ->
            Workout clonedWorkout = testWorkout.clone()
            clonedWorkout.setWorkoutDate(day)
            clonedWorkout.setId(UUID.randomUUID())
            clonedWorkout
        }.collect(Collectors.toList());
        def createdWorkouts = workoutRepository.save(workoutsToSave)

        when:
        def foundWeeksWorkouts = workoutRepository.findAllWorkouts(monday, sunday);

        then:
        foundWeeksWorkouts.size() == createdWorkouts.size()
        foundWeeksWorkouts.each { workout ->
            workout.workoutDate in daysOfWeek
        }
    }
}
