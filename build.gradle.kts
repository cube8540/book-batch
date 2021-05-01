import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.31"
    kotlin("plugin.spring") version "1.4.31"
    kotlin("plugin.jpa") version "1.4.31"
}

group = "cube8540.book"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.batch:spring-batch-integration:4.3.2")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-client:5.4.5")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    testImplementation("com.h2database:h2:1.4.200")
    testImplementation("io.mockk:mockk:1.10.6")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("org.springframework.batch:spring-batch-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
