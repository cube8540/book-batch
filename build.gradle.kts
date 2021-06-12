import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"

    kotlin("jvm") version "1.4.31"
    kotlin("plugin.spring") version "1.4.31"
    kotlin("plugin.jpa") version "1.4.31"
    kotlin("kapt") version "1.4.10"
}

group = "cube8540.book"
version = "0.5.0"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
    kotlin.srcDir("$buildDir/generated/source/kapt/main")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.batch:spring-batch-integration:4.3.2")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-client:5.4.5")

    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.7.1")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.querydsl:querydsl-core:4.4.0")
    implementation("com.querydsl:querydsl-jpa:4.4.0")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.11.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.0")

    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    implementation("io.github.cube8540:validator-core:1.3.0-RELEASE")

    implementation("org.jsoup:jsoup:1.13.1")

    testImplementation("com.h2database:h2:1.4.200")
    testImplementation("io.mockk:mockk:1.10.6")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("org.springframework.batch:spring-batch-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    testImplementation("com.squareup.okhttp3:okhttp:4.9.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.0")

    testImplementation("org.mockito:mockito-inline:3.9.0")

    kapt("com.querydsl:querydsl-apt:4.4.0:jpa")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
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
