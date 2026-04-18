# 🔧 Решение распространенных ошибок

## ✅ Исправлено: Non-ASCII characters in path

**Проблема:** Путь содержит кириллицу (русские буквы)
**Решение:** Добавлена строка `android.overridePathCheck=true` в gradle.properties

---

## 🛠️ Другие возможные ошибки и решения

### 1. SDK not found
**Ошибка:**
```
SDK location not found. Define location with an ANDROID_SDK_ROOT environment variable
```

**Решение:**
1. File → Project Structure → SDK Location
2. Укажите путь к Android SDK (обычно: C:\Users\Yarik\AppData\Local\Android\Sdk)

---

### 2. Gradle version mismatch
**Ошибка:**
```
Minimum supported Gradle version is X.X
```

**Решение:**
1. File → Settings → Build → Gradle
2. Выберите "Use Gradle from: 'gradle-wrapper.properties'"

---

### 3. Kotlin version conflict
**Ошибка:**
```
Kotlin version mismatch
```

**Решение:**
1. Обновите версию Kotlin в build.gradle.kts
2. Sync Project

---

### 4. Room schema export error
**Ошибка:**
```
Cannot find the schema export directory
```

**Решение:**
Это предупреждение, можно игнорировать. Или добавьте в app/build.gradle.kts:
```kotlin
room {
    schemaDirectory("$projectDir/schemas")
}
```

---

### 5. Compose compiler version
**Ошибка:**
```
Compose compiler version mismatch
```

**Решение:**
Уже настроено правильно в проекте (kotlinCompilerExtensionVersion = "1.5.4")

---

### 6. Duplicate class found
**Ошибка:**
```
Duplicate class found in modules
```

**Решение:**
1. Build → Clean Project
2. Build → Rebuild Project

---

### 7. Out of memory
**Ошибка:**
```
OutOfMemoryError: Java heap space
```

**Решение:**
Уже настроено в gradle.properties:
```
org.gradle.jvmargs=-Xmx2048m
```

---

## 📝 Если ошибка не из списка

1. Скопируйте полный текст ошибки
2. Отправьте мне
3. Я быстро найду решение!

---

**Время создания:** 21:31
**Статус:** Готов к помощи! 🚀
