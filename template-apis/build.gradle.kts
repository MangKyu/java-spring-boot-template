import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks {
    // 실행 가능한 JAR 설정: main 없는 라이브러리에서는 false로 설정
    withType<BootJar> { enabled = true }

    // 외부에서 의존성 jar 생성 여부
    withType<Jar> { enabled = false }
}

dependencies {
    implementation (project(":template-core"))

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")


}