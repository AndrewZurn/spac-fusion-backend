package com.zalude.spac.fusion

import com.zalude.spac.fusion.models.domain.Workout
import com.zalude.spac.fusion.models.domain.Exercise
import com.zalude.spac.fusion.models.domain.FusionUser
import com.zalude.spac.fusion.models.domain.UserCompletedWorkoutLookup
import com.zalude.spac.fusion.models.domain.WorkoutWithDate
import org.junit.Before

import java.time.LocalDate

trait IntegrationTestData {

    List<Exercise> testExerciseOptionsList
    Workout testExercise
    UUID testExerciseId = UUID.fromString("dc4a2ea0-4279-4c23-b6cc-db2bf6577a47")
    FusionUser testFusionUser
    FusionUser otherTestFusionUser
    UserCompletedWorkoutLookup testUserExerciseOptionLookup
    WorkoutWithDate testWorkout

    @Before
    public void setUp() throws Exception {
        println "Setting up test data..."
        this.testExerciseOptionsList = [
                new Exercise(UUID.fromString("ffebed48-9272-4c44-85fb-a24dff1e6ae2"), "Push Ups", "AMRAP"),
                new Exercise(UUID.fromString("147fb790-7e8c-4776-8b30-9863c45bfa4b"), "Sit Ups", "AMRAP"),
                new Exercise(UUID.fromString("4fc3ff86-ef20-4d34-9aed-4415919a8e65"), "Pull Ups", "AMRAP")
        ]
        this.testExerciseOptionsList = this.testExerciseOptionsList.each { it.setDuration("1 minute") }

        this.testExercise = new Workout("Traditional Core and Upper Body", "Doing some good ol' push/sit/pull ups", testExerciseOptionsList)
        this.testExercise.id = testExerciseId

        this.testExerciseOptionsList = this.testExerciseOptionsList.each { it -> it.workout = testExercise } as List
        this.testExercise.exercises = this.testExerciseOptionsList

        this.testWorkout = new WorkoutWithDate(UUID.fromString("6ded447d-5bb3-4198-9df4-8b62a099faeb"), "20 Minutes", LocalDate.now(), testExercise)
        this.testWorkout.previewText = "This one makes fat cry."

        this.testFusionUser = new FusionUser(UUID.fromString("e7e0416d-6f2b-4c85-8002-b6b8d55c127d"), "2341242ab32",
                "Andrew", "Zurn", "andrew.zurn", "andrew.zurn@email.com", 24, 6.5, 215.5, "SILVER")
        this.otherTestFusionUser = new FusionUser(UUID.fromString("d7c68598-c897-438c-a577-bef3d89d60f1"), "234af32sdf23",
                "Shannon", "Lane", "shannon.lane", "shannon.lane@email.com", 25, 5.6, 130.0, "GOLD")

        this.testUserExerciseOptionLookup = new UserCompletedWorkoutLookup(UUID.fromString("7229af23-fc3c-492f-9d67-f5197633f15c"),
                testFusionUser, testExerciseOptionsList.get(0), testWorkout, "25")
        this.testFusionUser.setUserCompletedWorkoutLookups([testUserExerciseOptionLookup])
    }
}