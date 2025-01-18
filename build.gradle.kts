// plugin 이란 미리 구성해놓은 task 들의 모음이며, 특정 빌드과정에 필요한 기본정보를 포함함
plugins {
    java // 테스트, 번들링 기능과 함께 Java 컴파일을 추가해주며, 다른 JVM 언어 플러그인의 기반이 됨
    id("org.springframework.boot") version "3.4.1" // 실행가능한 jar 또는 war로 패키징하여 애플리케이션 실행이 가능하도록 하며, spring-boot-dependencies 기반의 의존성 관리를 사용함
    id("io.spring.dependency-management") version "1.1.7" // 자동으로 spring-boot-dependencies bom을 끌어와서 버전 관리를 해줌
}

group = "com.mangkyu.template"
version = "0.0.1-SNAPSHOT"



// 현재의 root 프로젝트와 앞으로 추가될 서브 모듈에 대한 설정
allprojects {

    // 라이브러리들을 받아올 원격 저장소들을 설정함
    repositories {
        mavenCentral()
    }
}


// 루트 제외한 전체 서브 모듈에 해당되는 설정
// settings.gradle에 include된 전체 프로젝트에 대한 공통 사항을 명시함
// root 프로젝트까지 적용하고 싶다면 allprojects에서 사용해야 함
subprojects {

    // subprojects 블록 안에서는 plugins 블록을 사용할 수 없으므로 apply plugin을 사용해야 함
    apply(plugin = "idea")
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    // 모든 서브 모듈에서 사용될 공통 의존성들을 추가함
    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-data-redis")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.boot:spring-boot-starter-web")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    // 모든 서브 모듈에서 Junit을 사용하기 위한 설정
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
