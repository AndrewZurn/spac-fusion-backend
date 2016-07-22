package com.zalude.spac.fusion

import com.zalude.spac.fusion.models.domain.Exercise
import com.zalude.spac.fusion.models.domain.ExerciseOption
import com.zalude.spac.fusion.models.domain.FusionUser
import com.zalude.spac.fusion.models.domain.UserExerciseOptionLookup
import com.zalude.spac.fusion.models.domain.Workout
import groovy.transform.CompileStatic
import org.junit.Before

import java.time.LocalDateTime

trait IntegrationTestData {

    List<ExerciseOption> testExerciseOptionsList
    Exercise testExercise
    UUID testExerciseId = UUID.fromString("dc4a2ea0-4279-4c23-b6cc-db2bf6577a47")
    FusionUser testFusionUser
    UserExerciseOptionLookup testUserExerciseOptionLookup
    Workout testWorkout

    @Before
    public void setUp() throws Exception {
        println "Setting up test data..."
        this.testExerciseOptionsList = [
                new ExerciseOption("Push Ups", "Repetitions", "50"),
                new ExerciseOption("Sit Ups", "Repetitions", "50"),
                new ExerciseOption("Pull Ups", "Repetitions", "10")
        ]

        this.testExercise = new Exercise("Traditional Core and Upper Body", "Doing some good ol' push/sit/pull ups", testExerciseOptionsList)
        this.testExercise.id = testExerciseId

        this.testExerciseOptionsList = this.testExerciseOptionsList.each { it -> it.exercise = testExercise }
        this.testExercise.exerciseOptions = this.testExerciseOptionsList

        this.testFusionUser = new FusionUser(UUID.fromString("e7e0416d-6f2b-4c85-8002-b6b8d55c127d"), "Andrew", "Zurn",
                "andrew.zurn", "andrew.zurn@email.com", "some-password", 24, 6.5, 1, true)

        this.testUserExerciseOptionLookup = new UserExerciseOptionLookup(UUID.fromString("7229af23-fc3c-492f-9d67-f5197633f15c"),
                testFusionUser, testExerciseOptionsList.get(0), "25")
        this.testFusionUser.setUserExerciseOptionLookups([testUserExerciseOptionLookup])

        this.testWorkout = new Workout(UUID.fromString("6ded447d-5bb3-4198-9df4-8b62a099faeb"), "20 Minutes", LocalDateTime.now(), testExercise)
    }
}