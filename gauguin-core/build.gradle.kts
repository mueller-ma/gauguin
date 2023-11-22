plugins {
    java
    id("org.jetbrains.kotlin.jvm")
    jacoco
    `jvm-test-suite`
}

tasks {
    test {
        useJUnitPlatform()
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
    }
}

dependencies {
    api(libs.androidx.annotation)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    implementation(libs.koin.core)
    testImplementation(libs.test.mockk)

    api("io.github.oshai:kotlin-logging-jvm:5.1.0")
    testApi("io.github.oshai:kotlin-logging-jvm:5.1.0")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    //implementation("ch.qos.logback:logback-core:1.4.11")

    testImplementation(platform("io.kotest:kotest-bom:5.8.0"))
    testImplementation("io.kotest:kotest-runner-junit5")
    testImplementation("io.kotest:kotest-assertions-core")
    testImplementation("io.kotest:kotest-framework-datatest")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    jvmToolchain(11)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        register<JvmTestSuite>("integrationTest") {
            dependencies {
                implementation(project())

                implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")

                implementation(platform("io.kotest:kotest-bom:5.8.0"))
                implementation("io.kotest:kotest-runner-junit5")
                implementation("io.kotest:kotest-assertions-core")
                implementation("io.kotest:kotest-framework-datatest")
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("integrationTest"))
}
