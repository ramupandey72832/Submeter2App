plugins {
    id("java-library")
}


java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}




dependencies {

    // RUNTIME DATABASE DRIVERS
    implementation("org.sqldroid:sqldroid:1.0.3")
// Keep active FOR ANDROID
// implementation 'org.xerial:sqlite-jdbc:3.45.1.0' // COMMENTED OUT: Use this for DESKTOP instead of SQLDroid
// SQL FRAMEWORK (JDBI)
//    implementation("org.jdbi:jdbi3-core:3.45.1")
//    implementation("org.jdbi:jdbi3-sqlobject:3.45.1")

    implementation(project(":rrAllUtilityLib"))

    // Lombok for main code
    compileOnly("org.projectlombok:lombok:1.18.48")
    annotationProcessor("org.projectlombok:lombok:1.18.48")

    testCompileOnly("org.projectlombok:lombok:1.18.48")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.48")
}