import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks {
    // 실행 가능한 JAR 설정: main 없는 라이브러리에서는 false로 설정
    withType<BootJar> { enabled = false }

    // 외부에서 의존성 jar 생성 여부
    withType<Jar> { enabled = true }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-json")
    implementation("com.github.ben-manes.caffeine:caffeine")

    implementation("org.springframework.boot:spring-boot-testcontainers")
    implementation("org.testcontainers:testcontainers")

    runtimeOnly("com.h2database:h2")
}