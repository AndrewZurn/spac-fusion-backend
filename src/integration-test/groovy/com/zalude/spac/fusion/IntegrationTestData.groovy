package com.zalude.spac.fusion

import com.zalude.spac.fusion.models.domain.Workout
import com.zalude.spac.fusion.models.domain.Exercise
import com.zalude.spac.fusion.models.domain.FusionUser
import com.zalude.spac.fusion.models.domain.UserCompletedScheduledWorkout
import com.zalude.spac.fusion.models.domain.ScheduledWorkout
import org.junit.Before

import java.time.LocalDate

trait IntegrationTestData {

    List<Exercise> testExerciseList
    Workout testWorkout
    UUID testWorkoutId = UUID.fromString("dc4a2ea0-4279-4c23-b6cc-db2bf6577a47")
    FusionUser testFusionUser
    FusionUser otherTestFusionUser
    UserCompletedScheduledWorkout testUserCompletedScheduledWorkout
    ScheduledWorkout testScheduledWorkout

    @Before
    public void setUp() throws Exception {
        println "Setting up test data..."
        this.testExerciseList = [
                new Exercise(UUID.fromString("ffebed48-9272-4c44-85fb-a24dff1e6ae2"), "Push Ups", "50"),
                new Exercise(UUID.fromString("147fb790-7e8c-4776-8b30-9863c45bfa4b"), "Sit Ups", "50"),
                new Exercise(UUID.fromString("4fc3ff86-ef20-4d34-9aed-4415919a8e65"), "Pull Ups", "50")
        ]

        this.testWorkout = new Workout("Traditional Core and Upper Body", "Doing some good ol' push/sit/pull ups", testExerciseList)
        this.testWorkout.id = testWorkoutId
        this.testWorkout.input = "Repetitions"

        this.testExerciseList = this.testExerciseList.each { it -> it.workout = testWorkout } as List
        this.testWorkout.exercises = this.testExerciseList

        this.testScheduledWorkout = new ScheduledWorkout(UUID.fromString("6ded447d-5bb3-4198-9df4-8b62a099faeb"), LocalDate.now(), true, testWorkout)

        this.testFusionUser = new FusionUser(UUID.fromString("e7e0416d-6f2b-4c85-8002-b6b8d55c127d"), "2341242ab32",
                "Andrew", "Zurn", "andrew.zurn", "andrew.zurn@email.com", 24, 6.5, 215.5, "SILVER")
        this.otherTestFusionUser = new FusionUser(UUID.fromString("d7c68598-c897-438c-a577-bef3d89d60f1"), "234af32sdf23",
                "Shannon", "Lane", "shannon.lane", "shannon.lane@email.com", 25, 5.6, 130.0, "GOLD")

        this.testUserCompletedScheduledWorkout = new UserCompletedScheduledWorkout(
                UUID.fromString("7229af23-fc3c-492f-9d67-f5197633f15c"), testFusionUser, testScheduledWorkout, "25")
        this.testFusionUser.setUserCompletedScheduledWorkouts([testUserCompletedScheduledWorkout])
    }
}