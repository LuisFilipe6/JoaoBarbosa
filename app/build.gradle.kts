plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "pt.joaobarbosa.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "pt.joaobarbosa.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0-teste"
        // Modo de teste: compra simulada + código TESTE-2026 aceite.
        // Passar a false quando existir pagamento real e validação no servidor.
        buildConfigField("boolean", "TEST_MODE", "true")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Assinado com a chave de debug só para facilitar testes; trocar antes de distribuir.
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { buildConfig = true }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-ktx:1.9.2")
    implementation("androidx.webkit:webkit:1.11.0")
}
