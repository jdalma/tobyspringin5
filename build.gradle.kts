plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework:spring-context:5.3.22")
    implementation("org.springframework:spring-jdbc:5.3.22")
    implementation("mysql:mysql-connector-java:8.0.30")
    implementation("org.projectlombok:lombok:1.18.24")
    implementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    implementation("org.assertj:assertj-core:3.18.1")
    implementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    implementation("org.springframework:spring-test:5.3.18")
    implementation("javax.mail:mail:1.4.7")
    implementation("javax.activation:activation:1.1.1")

    annotationProcessor("org.projectlombok:lombok:1.18.24")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
