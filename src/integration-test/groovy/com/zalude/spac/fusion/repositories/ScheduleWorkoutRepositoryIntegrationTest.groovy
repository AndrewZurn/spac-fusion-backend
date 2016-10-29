package com.zalude.spac.fusion.repositories

import com.zalude.spac.fusion.IntegrationTestData
import com.zalude.spac.fusion.RepositoryBase
import com.zalude.spac.fusion.models.domain.ScheduledWorkout
import fj.data.Collectors
import org.springframework.transaction.annotation.Transactional

import javax.inject.Inject
import java.time.DayOfWeek
import java.time.LocalDate

public class ScheduleWorkoutRepositoryIntegrationTest extends RepositoryBase implements IntegrationTestData {

    @Inject
    private ScheduledWorkoutRepository scheduledWorkoutRepository

    @Inject
    private WorkoutRepository workoutRepository

    void setup() {
        this.testWorkout = workoutRepository.save(this.testWorkout)
        this.testScheduledWorkout.workout = this.testWorkout
        scheduledWorkoutRepository.save(this.testScheduledWorkout)
    }

    void cleanup() {
        scheduledWorkoutRepository.deleteAllInBatch()
    }

    def "find an empty list of workouts"() {
        given:
        scheduledWorkoutRepository.deleteAll()

        when:
        def workouts = scheduledWorkoutRepository.findAll()

        then:
        workouts.size() == 0
    }

    @Transactional
    def "find a list of workouts"() {
        given:
        when:
        def workouts = scheduledWorkoutRepository.findAll()

        then:
        workouts.size() == 1
        workouts.getAt(0) == this.testScheduledWorkout
    }

    def "find today's workout"() {
        given:
        when:
        def todaysWorkout = scheduledWorkoutRepository.findTodaysWorkout()

        then:
        todaysWorkout.workoutDate == LocalDate.now()
    }

    def "find this week's workouts"() {
        given:
        scheduledWorkoutRepository.deleteAllInBatch()

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate tuesday = LocalDate.now().with(DayOfWeek.TUESDAY);
        LocalDate wednesday = LocalDate.now().with(DayOfWeek.WEDNESDAY);
        LocalDate thursday = LocalDate.now().with(DayOfWeek.THURSDAY);
        LocalDate friday = LocalDate.now().with(DayOfWeek.FRIDAY);
        LocalDate saturday = LocalDate.now().with(DayOfWeek.SATURDAY);
        LocalDate sunday = LocalDate.now().with(DayOfWeek.SUNDAY);
        def daysOfWeek = [monday, tuesday, wednesday, thursday, friday, saturday, sunday]
        def workoutsToSave = daysOfWeek.stream().map { day ->
            def clonedWorkout = this.testScheduledWorkout.clone() as ScheduledWorkout
            clonedWorkout.setWorkoutDate(day)
            clonedWorkout.setId(UUID.randomUUID())
            clonedWorkout
        }.collect(Collectors.toList());
        def createdWorkouts = scheduledWorkoutRepository.save(workoutsToSave)

        when:
        def foundWeeksWorkouts = scheduledWorkoutRepository.findAllWorkouts(monday, sunday);

        then:
        foundWeeksWorkouts.size() == createdWorkouts.size()
        foundWeeksWorkouts.each { workout ->
            workout.workoutDate in daysOfWeek
        }
    }
}
