# CLAUDE.md - AI Assistant Guide for CarlyDean Library

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Technology Stack](#technology-stack)
4. [Project Structure](#project-structure)
5. [Key Conventions](#key-conventions)
6. [Development Workflow](#development-workflow)
7. [Common Tasks](#common-tasks)
8. [Important Files](#important-files)
9. [Gotchas & Best Practices](#gotchas--best-practices)
10. [Testing](#testing)
11. [Documentation References](#documentation-references)

---

## 📚 Project Overview

**CarlyDean Library** is an Android application for managing and reading a large digital library catalog (40,000+ books). The app downloads book metadata from a JSON catalog, displays books in a browsable list, and provides features for reading PDFs/EPUBs, managing favorites, and tracking reading progress.

### Current State
- **Phase**: Test/Development
- **Status**: Base functionality implemented with catalog download, parsing, and display
- **Primary Language**: Kotlin
- **Min SDK**: API 24 (Android 7.0, covers 94% of devices)
- **Target SDK**: API 33 (Android 13)

### Project Purpose
This is a **test project** to verify:
1. Android development environment is correctly configured
2. JSON catalog parsing works with large datasets (40K+ books)
3. Memory-efficient streaming for large JSON files
4. Base architecture is solid before adding full features

---

## 🏗️ Architecture

### Pattern: MVVM (Model-View-ViewModel)

The app follows Android's recommended MVVM architecture with clear separation of concerns:

```
┌─────────────────────────────────────────────────────┐
│                      UI Layer                        │
│  MainActivity.kt, Adapters, XML Layouts             │
└────────────────┬────────────────────────────────────┘
                 │ observes LiveData
┌────────────────▼────────────────────────────────────┐
│                  ViewModel Layer                     │
│  CatalogViewModel.kt - manages UI state             │
└────────────────┬────────────────────────────────────┘
                 │ calls methods
┌────────────────▼────────────────────────────────────┐
│                 Repository Layer                     │
│  CatalogRepository.kt, BookRepository.kt            │
│  - Business logic, data coordination                │
└─────────┬──────────────────────────┬────────────────┘
          │                          │
┌─────────▼─────────┐      ┌────────▼──────────────┐
│   Network Layer   │      │   Database Layer       │
│ CatalogService.kt │      │  AppDatabase.kt       │
│  (OkHttp, Gson)   │      │  (Room, DAOs)         │
└───────────────────┘      └───────────────────────┘
```

### Layer Responsibilities

#### UI Layer (`app/src/main/java/com/carlydean/test/`)
- **MainActivity.kt**: Main entry point, handles user interactions, observes ViewModels
- **Adapters** (`ui/adapter/`): RecyclerView adapters for displaying lists
- **Layouts** (`res/layout/`): XML view definitions

#### ViewModel Layer (`ui/viewmodel/`)
- **CatalogViewModel.kt**: Manages catalog state, loading, errors
- Uses `LiveData` for reactive UI updates
- Inherits from `AndroidViewModel` for application context access
- Manages coroutines via `viewModelScope`

#### Repository Layer (`data/repository/`)
- **CatalogRepository.kt**: Handles catalog fetching, caching to file system
- **BookRepository.kt**: Manages book-specific operations and Room database interactions
- Coordinates between network and local data sources
- Implements caching strategy using file-based storage (not SharedPreferences for large data)

#### Network Layer (`data/network/`)
- **CatalogService.kt**: Downloads JSON catalog from URLs
- Uses OkHttp for HTTP requests with 30-second timeouts
- **Memory-efficient streaming**: Uses `charStream()` instead of `.string()` to avoid OOM errors on large JSON files

#### Database Layer (`data/database/`)
- **AppDatabase.kt**: Room database singleton with three DAOs
- **Tables**: `books_cache`, `favorites`, `reading_progress`
- **DAOs**: BookDao, FavoriteDao, ReadingProgressDao

#### Data Models (`data/model/`)
- **Models.kt**: Network/API models (Catalog, Book, Category, etc.)
- **Entities.kt**: Room database entities (BookEntity, Favorite, ReadingProgress)

---

## 🛠️ Technology Stack

### Core Android
| Dependency | Version | Purpose |
|------------|---------|---------|
| Android Gradle Plugin | 8.0.2 | Build system |
| Gradle | 8.0 | Build tool |
| Kotlin | 1.8.20 | Primary language |
| compileSdk | 33 | Android 13 |
| targetSdk | 33 | Android 13 |
| minSdk | 24 | Android 7.0 (94% device coverage) |

### AndroidX Libraries
| Library | Version | Purpose |
|---------|---------|---------|
| core-ktx | 1.10.1 | Kotlin extensions |
| appcompat | 1.6.1 | Backward compatibility |
| material | 1.9.0 | Material Design components |
| constraintlayout | 2.1.4 | Layout manager |
| recyclerview | 1.3.0 | List display |

### Architecture Components
| Library | Version | Purpose |
|---------|---------|---------|
| lifecycle-viewmodel-ktx | 2.6.1 | ViewModel support |
| lifecycle-livedata-ktx | 2.6.1 | LiveData support |
| lifecycle-runtime-ktx | 2.6.1 | Lifecycle utilities |
| room-runtime | 2.5.2 | Local database |
| room-ktx | 2.5.2 | Kotlin Room extensions |

### Networking & Data
| Library | Version | Purpose |
|---------|---------|---------|
| okhttp | 4.11.0 | HTTP client |
| logging-interceptor | 4.11.0 | Network logging |
| gson | 2.10.1 | JSON parsing |
| kotlinx-coroutines-android | 1.7.1 | Async operations |

### Media & UI
| Library | Version | Purpose |
|---------|---------|---------|
| coil | 2.4.0 | Image loading |
| android-pdf-viewer | 3.2.0-beta.3 | PDF viewing |
| core-splashscreen | 1.0.1 | Splash screen |

### Testing
| Library | Version | Purpose |
|---------|---------|---------|
| junit | 4.13.2 | Unit testing |
| androidx.test.ext:junit | 1.1.5 | Android testing |
| espresso-core | 3.5.1 | UI testing |

---

## 📁 Project Structure

```
carly-test/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/carlydean/test/
│   │   │   │   ├── MainActivity.kt              # Main UI entry point
│   │   │   │   ├── data/
│   │   │   │   │   ├── database/
│   │   │   │   │   │   └── AppDatabase.kt       # Room database + DAOs
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── Models.kt            # API/Network models
│   │   │   │   │   │   └── Entities.kt          # Room entities
│   │   │   │   │   ├── network/
│   │   │   │   │   │   └── CatalogService.kt    # HTTP client for catalog download
│   │   │   │   │   └── repository/
│   │   │   │   │       ├── CatalogRepository.kt # Catalog data coordination
│   │   │   │   │       └── BookRepository.kt    # Book data coordination
│   │   │   │   └── ui/
│   │   │   │       ├── adapter/
│   │   │   │       │   └── BooksAdapter.kt      # RecyclerView adapter
│   │   │   │       └── viewmodel/
│   │   │   │           └── CatalogViewModel.kt  # UI state management
│   │   │   ├── res/
│   │   │   │   ├── drawable/                    # Vector drawables
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml        # Main screen layout
│   │   │   │   │   └── item_book.xml            # Book list item layout
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml               # Color definitions
│   │   │   │   │   └── themes.xml               # App themes
│   │   │   │   └── mipmap-anydpi-v26/           # Adaptive icons
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   │       └── java/com/carlydean/test/
│   │           ├── BasicUnitTest.kt
│   │           └── CatalogParsingTest.kt
│   └── build.gradle                             # App module configuration
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties            # Gradle wrapper config
├── build.gradle                                 # Root build configuration
├── settings.gradle                              # Project settings
├── gradle.properties                            # Gradle properties
├── gradlew.bat                                  # Windows Gradle wrapper
├── local.properties.template                    # SDK location template
├── .gitignore
│
├── # Documentation (Spanish)
├── README.md                                    # Complete verification guide
├── QUICKSTART.md                                # 3-minute quick start
├── VERSIONS.md                                  # Version lock rationale
├── ISOLATION.md                                 # Project isolation info
├── FIXES.md                                     # Common error solutions
├── STRUCTURE-UPDATE.md                          # Data model documentation
├── PHASE-3A.md                                  # Next development phase
└── CLAUDE.md                                    # This file (AI assistant guide)
```

### Package Structure

```
com.carlydean.test/
├── MainActivity.kt                    # UI entry point
├── data/                              # Data layer
│   ├── database/                      # Local persistence
│   ├── model/                         # Data models
│   ├── network/                       # Remote data
│   └── repository/                    # Data coordination
└── ui/                                # UI layer
    ├── adapter/                       # RecyclerView adapters
    └── viewmodel/                     # ViewModels
```

---

## 🎯 Key Conventions

### Naming Conventions

#### Files
- **Kotlin classes**: PascalCase (e.g., `CatalogViewModel.kt`)
- **XML resources**: snake_case (e.g., `activity_main.xml`, `item_book.xml`)
- **Drawables**: `ic_` prefix for icons (e.g., `ic_favorite.xml`, `ic_book_placeholder.xml`)
- **Layouts**: `activity_` for activities, `item_` for list items, `fragment_` for fragments

#### Code
- **Variables**: camelCase (e.g., `catalogUrl`, `isLoading`)
- **Constants**: SCREAMING_SNAKE_CASE (e.g., `CACHE_FILE_NAME`, `KEY_CATALOG_URL`)
- **LiveData**: prefix with underscore for private mutable, expose immutable
  ```kotlin
  private val _catalog = MutableLiveData<Catalog?>()
  val catalog: LiveData<Catalog?> = _catalog
  ```
- **Functions**: camelCase, descriptive verbs (e.g., `downloadCatalog()`, `getCachedCatalog()`)

### Code Style

#### Kotlin
- **Null safety**: Use `?` for nullable types, provide defaults where possible
  ```kotlin
  val author: String? = null
  val subcategory: String? = null
  ```
- **Data classes**: Use for models and entities
- **Coroutines**: Use `suspend` functions for async operations, run in appropriate dispatchers
  ```kotlin
  suspend fun downloadCatalog(url: String): Result<Catalog> = withContext(Dispatchers.IO) { ... }
  ```
- **ViewModelScope**: Use for coroutines in ViewModels
  ```kotlin
  viewModelScope.launch { ... }
  ```

#### Memory Efficiency
- **Large JSON parsing**: ALWAYS use streaming
  ```kotlin
  // ✅ CORRECT - Memory efficient
  val charStream = responseBody.charStream()
  val catalog = charStream.use { streamReader ->
      gson.fromJson(streamReader, Catalog::class.java)
  }

  // ❌ WRONG - Causes OutOfMemoryError with large files
  val jsonString = response.body?.string()
  val catalog = gson.fromJson(jsonString, Catalog::class.java)
  ```

- **Large data caching**: Use file-based caching, NOT SharedPreferences
  ```kotlin
  // ✅ CORRECT - File-based cache for large data
  val cacheFile = File(context.cacheDir, "catalog_cache.json")
  FileWriter(cacheFile).use { writer ->
      gson.toJson(catalog, writer)
  }

  // ❌ WRONG - SharedPreferences has size limits
  prefs.edit().putString("catalog", gson.toJson(catalog)).apply()
  ```

#### Architecture Patterns
- **Repository Pattern**: Repositories coordinate data sources, no direct Room/Network access from ViewModels
- **Single Source of Truth**: ViewModels expose LiveData, UI observes it
- **Error Handling**: Use `Result<T>` for network operations
  ```kotlin
  suspend fun fetchCatalog(url: String): Result<Catalog>
  ```

### Resource Naming

#### Colors (`res/values/colors.xml`)
- Descriptive names: `colorPrimary`, `colorBackground`, `textColorPrimary`
- Use Material Design naming conventions

#### Strings
- All user-facing strings should be in `strings.xml` (not hardcoded)
- Use descriptive keys: `app_name`, `error_network`, `button_download`

### Comments
- **KDoc**: Use for public classes and functions
  ```kotlin
  /**
   * Repositorio para manejar el catálogo
   * Descarga desde la red y guarda en caché local
   */
  class CatalogRepository(private val context: Context) { ... }
  ```
- **Inline comments**: Use for non-obvious logic, but prefer self-documenting code
- **TODO comments**: Mark incomplete features or temporary solutions
  ```kotlin
  // TODO: Implement pagination for better performance with 40K+ books
  ```

---

## ⚙️ Development Workflow

### Setup
1. Open project in Android Studio
2. Wait for Gradle sync (2-5 minutes first time)
3. Verify SDK location in `File → Project Structure → SDK Location`
4. Create `local.properties` if needed (see template)

### Version Lock Philosophy
**CRITICAL**: This project uses **fixed, stable versions** from 2023. DO NOT update dependencies unless absolutely necessary.

#### Why?
- Maximum compatibility across devices
- Proven stability, no cutting-edge bugs
- Suppressed warnings are intentional and safe to ignore

#### Warnings to IGNORE (they are normal):
- ✅ "SDK processing... version 4 was encountered"
- ✅ "We recommend using a newer Android Gradle plugin"
- ✅ "Update Kotlin"

#### What NOT to do:
- ❌ Update Android Gradle Plugin
- ❌ Update Kotlin version
- ❌ Change compileSdk/targetSdk
- ❌ Update dependencies without testing

See `VERSIONS.md` for complete rationale.

### Making Changes

#### Adding New Features
1. **Data Layer First**: Define models, database entities if needed
2. **Repository**: Add data access methods
3. **ViewModel**: Add business logic and LiveData
4. **UI**: Update layouts and activities
5. **Test**: Write unit tests for new functionality

#### Modifying Models
When changing data models:
1. Update both `Models.kt` (network) and `Entities.kt` (database) if applicable
2. Update `@SerializedName` annotations for JSON fields
3. Provide default values for nullable fields
4. Update database version if Room entities change
5. Test with real JSON data

#### UI Changes
1. Edit XML layouts first
2. Update activity/fragment to bind new views
3. Use ViewBinding (already enabled)
4. Test on both small and large screens

### Git Workflow
- **Branches**: Feature branches use `claude/` prefix
- **Commits**: Clear, descriptive messages in English or Spanish
- **Push**: Always to feature branch, never directly to main
- **Testing**: Run tests before committing
  ```bash
  ./gradlew test
  ```

---

## 🔧 Common Tasks

### Task 1: Run the App
```bash
# Build and run on connected device/emulator
./gradlew installDebug

# Or from Android Studio: Click ▶️ (Run)
```

### Task 2: Run Tests
```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.carlydean.test.CatalogParsingTest"

# View test report
# Open: app/build/reports/tests/testDebugUnitTest/index.html
```

### Task 3: Build APK
```bash
# Debug APK
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk

# Release APK (requires signing)
./gradlew assembleRelease
```

### Task 4: Clean Build
```bash
# Clean all build artifacts
./gradlew clean

# Or use the batch script (Windows)
./clean-all.bat
```

### Task 5: Add a New Dependency
1. Add to `app/build.gradle` dependencies block
2. Sync Gradle: `File → Sync Project with Gradle Files`
3. **IMPORTANT**: Use fixed versions (no `+` wildcards)
4. Test thoroughly before committing

Example:
```gradle
// ✅ CORRECT - Fixed version
implementation 'com.example:library:1.2.3'

// ❌ WRONG - Version wildcard
implementation 'com.example:library:1.2.+'
```

### Task 6: Update Models for New JSON Structure
1. Open `app/src/main/java/com/carlydean/test/data/model/Models.kt`
2. Update data class fields
3. Add `@SerializedName` for JSON field names that differ from Kotlin property names
4. Set nullable types (`Type?`) and defaults for optional fields
5. Test with `CatalogParsingTest`

### Task 7: Add New Database Table
1. Create entity in `Entities.kt` with `@Entity` annotation
2. Create DAO interface in `AppDatabase.kt`
3. Add DAO to abstract methods in `AppDatabase`
4. **Increment database version** in `@Database` annotation
5. Add migration strategy (or use `fallbackToDestructiveMigration()` for dev)
6. Access via repository pattern

### Task 8: Debug Network Issues
1. Check Logcat for HTTP errors
2. Verify URL is accessible (test in browser)
3. Check OkHttp timeout settings in `CatalogService.kt` (currently 30s)
4. Add logging interceptor for detailed network logs:
   ```kotlin
   val loggingInterceptor = HttpLoggingInterceptor().apply {
       level = HttpLoggingInterceptor.Level.BODY
   }
   ```

### Task 9: Profile Memory Usage
1. Run app on device/emulator
2. Open Android Profiler in Android Studio
3. Navigate to Memory tab
4. Trigger catalog download
5. Monitor heap usage (should NOT exceed 100-200 MB for large JSON)
6. Look for leaks with heap dump analysis

### Task 10: Add a New Screen
1. Create XML layout in `res/layout/`
2. Create Activity or Fragment class
3. Register Activity in `AndroidManifest.xml`
4. Create ViewModel if needed
5. Navigate to screen using Intent:
   ```kotlin
   val intent = Intent(this, NewActivity::class.java)
   startActivity(intent)
   ```

---

## 📄 Important Files

### Configuration Files

#### `build.gradle` (root)
- Defines plugin versions: AGP 8.0.2, Kotlin 1.8.20
- Sets SDK versions: compileSdk 33, targetSdk 33, minSdk 24
- **DO NOT modify versions** without reading VERSIONS.md

Location: `/home/user/carly-test/build.gradle`

#### `app/build.gradle`
- App-level configuration
- All dependencies with fixed versions
- ViewBinding enabled
- Java 17 compatibility

Location: `/home/user/carly-test/app/build.gradle`

#### `gradle.properties`
- JVM settings: 2GB heap (`-Xmx2048m`)
- Warning suppressions for version mismatches
- Build optimizations: parallel, caching, on-demand

Location: `/home/user/carly-test/gradle.properties`

#### `settings.gradle`
- Repository configuration: Google, Maven Central, JitPack
- Module inclusion

Location: `/home/user/carly-test/settings.gradle`

#### `.gitignore`
- Excludes: build/, .gradle/, .idea/, *.apk, *.keystore, local.properties
- Includes: gradle-wrapper.jar (needed for wrapper)

Location: `/home/user/carly-test/.gitignore`

### Source Files

#### Entry Point
- **MainActivity.kt**: Main UI, initializes ViewModel, sets up RecyclerView, observes LiveData

Location: `app/src/main/java/com/carlydean/test/MainActivity.kt:1`

#### ViewModels
- **CatalogViewModel.kt**: Manages catalog state, download, caching, errors

Location: `app/src/main/java/com/carlydean/test/ui/viewmodel/CatalogViewModel.kt:1`

#### Repositories
- **CatalogRepository.kt**: Network + file cache coordination, JSON streaming
- **BookRepository.kt**: Room database operations for books

Location: `app/src/main/java/com/carlydean/test/data/repository/CatalogRepository.kt:1`
Location: `app/src/main/java/com/carlydean/test/data/repository/BookRepository.kt:1`

#### Network
- **CatalogService.kt**: OkHttp client, downloads JSON with streaming to avoid OOM

Location: `app/src/main/java/com/carlydean/test/data/network/CatalogService.kt:1`

#### Database
- **AppDatabase.kt**: Room database singleton, DAOs for Books, Favorites, ReadingProgress

Location: `app/src/main/java/com/carlydean/test/data/database/AppDatabase.kt:1`

#### Models
- **Models.kt**: Network models (Catalog, Book, Category, Collection, Tag, etc.)
- **Entities.kt**: Room entities (BookEntity, Favorite, ReadingProgress)

Location: `app/src/main/java/com/carlydean/test/data/model/Models.kt:1`
Location: `app/src/main/java/com/carlydean/test/data/model/Entities.kt:1`

#### Adapters
- **BooksAdapter.kt**: RecyclerView adapter using ListAdapter for efficient diffing

Location: `app/src/main/java/com/carlydean/test/ui/adapter/BooksAdapter.kt:1`

### Layouts
- **activity_main.xml**: Main screen UI (URL input, download button, RecyclerView)
- **item_book.xml**: Book list item layout (thumbnail, title, author, favorite button)

Location: `app/src/main/res/layout/activity_main.xml:1`
Location: `app/src/main/res/layout/item_book.xml:1`

---

## ⚠️ Gotchas & Best Practices

### Memory Management

#### Problem: OutOfMemoryError with Large JSON (40K+ books)
**Symptom**: App crashes when downloading/parsing catalog

**Cause**: Loading entire JSON string into memory before parsing

**Solution**: Use streaming (already implemented in `CatalogService.kt:51`)
```kotlin
// ✅ Memory-efficient streaming
val charStream = responseBody.charStream()
val catalog = charStream.use { streamReader ->
    gson.fromJson(streamReader, Catalog::class.java)
}
```

#### Problem: SharedPreferences Size Limit
**Symptom**: Catalog doesn't save, app becomes slow

**Cause**: SharedPreferences is not designed for large data (>1MB)

**Solution**: Use file-based caching (already implemented in `CatalogRepository.kt:58`)
```kotlin
// ✅ File-based cache for large data
val cacheFile = File(context.cacheDir, "catalog_cache.json")
FileWriter(cacheFile).use { writer ->
    gson.toJson(catalog, writer)
}
```

### Android Lifecycle

#### Problem: ViewModel Survives Configuration Changes
**Behavior**: ViewModels are NOT destroyed on screen rotation

**Best Practice**:
- Store UI state in ViewModel (it survives rotation)
- Observe LiveData in Activity/Fragment
- DO NOT store Activity/Fragment references in ViewModel (causes leaks)

#### Problem: LiveData Updates Before UI is Ready
**Solution**: Use `observe()` in `onCreate()` or `onViewCreated()`, not in `onResume()`

### RecyclerView Performance

#### Problem: Slow Scrolling with 40K+ Items
**Solution**: Use `ListAdapter` with `DiffUtil` (already implemented)
```kotlin
class BooksAdapter : ListAdapter<Book, BookViewHolder>(BookDiffCallback())
```

**Best Practice**: Call `setHasFixedSize(true)` if item size is constant
```kotlin
rvBooks.setHasFixedSize(true)  // In MainActivity.kt:182
```

### Coroutines

#### Rule: Always Use Appropriate Dispatcher
- `Dispatchers.IO`: Network, file I/O, database operations
- `Dispatchers.Main`: UI updates, LiveData
- `Dispatchers.Default`: CPU-intensive work

Example from `CatalogService.kt:28`:
```kotlin
suspend fun downloadCatalog(url: String): Result<Catalog> = withContext(Dispatchers.IO) {
    // Network operation on IO thread
}
```

#### Rule: Use `viewModelScope` in ViewModels
Automatically cancels coroutines when ViewModel is cleared
```kotlin
viewModelScope.launch {
    // Coroutine work
}
```

### JSON Parsing

#### Problem: JSON Field Name Mismatch
**Example**: JSON has `"file_type"`, Kotlin has `fileType`

**Solution**: Use `@SerializedName` annotation (see `Models.kt:99`)
```kotlin
@SerializedName("file_type")
val fileType: String
```

#### Problem: Null Values Cause Crashes
**Solution**: Make fields nullable with default values (see `Models.kt:94`)
```kotlin
val author: String? = null,
val subcategory: String? = null,
val tags: List<String>? = null
```

### Room Database

#### Rule: All Database Operations Must Be Async
**Never** call DAO methods on main thread

```kotlin
// ✅ CORRECT - Suspend function
@Dao
interface BookDao {
    @Query("SELECT * FROM books_cache")
    suspend fun getAllBooks(): List<BookEntity>
}

// ❌ WRONG - Blocking on main thread
@Dao
interface BookDao {
    @Query("SELECT * FROM books_cache")
    fun getAllBooks(): List<BookEntity>  // Will crash
}
```

#### Rule: Increment Version on Schema Changes
When modifying entities, increment database version in `AppDatabase.kt:12`:
```kotlin
@Database(
    entities = [...],
    version = 2,  // Increment this
    exportSchema = false
)
```

### Testing

#### Problem: Context Required for Tests
**Solution**: Use `@RunWith(AndroidJUnit4::class)` for instrumented tests

For unit tests without context, mock dependencies:
```kotlin
@Test
fun testCatalogParsing() {
    val json = """{"version": "1.0", ...}"""
    val catalog = gson.fromJson(json, Catalog::class.java)
    assertEquals("1.0", catalog.version)
}
```

### Warnings to IGNORE

These warnings are **intentional** and **safe to ignore**:

1. ✅ "SDK processing... version 4 was encountered"
   - Cause: Android SDK Tools is newer than AGP
   - Impact: None, compilation works fine

2. ✅ "We recommend using a newer Android Gradle plugin"
   - Cause: Using AGP 8.0.2 with compileSdk 33 (stable combo)
   - Impact: None, suppressed in `gradle.properties:14`

3. ✅ "Update Kotlin"
   - Cause: Kotlin 1.8.20 is older than latest
   - Impact: None, version is stable and compatible

4. ✅ "Gradle sync warnings" (yellow)
   - Yellow warnings are informational, NOT errors
   - Only worry about RED errors

---

## 🧪 Testing

### Unit Tests

Location: `app/src/test/java/com/carlydean/test/`

#### BasicUnitTest.kt
- Verifies basic Kotlin functionality
- Example test structure

#### CatalogParsingTest.kt
- Tests JSON parsing with Gson
- Validates model structure matches real JSON data

### Running Tests

```bash
# All tests
./gradlew test

# Specific test
./gradlew test --tests "com.carlydean.test.CatalogParsingTest"

# With detailed output
./gradlew test --info

# View HTML report
# Open: app/build/reports/tests/testDebugUnitTest/index.html
```

### Writing New Tests

#### Unit Test Template
```kotlin
package com.carlydean.test

import org.junit.Test
import org.junit.Assert.*

class MyFeatureTest {
    @Test
    fun testMyFeature() {
        // Arrange
        val input = "test"

        // Act
        val result = myFunction(input)

        // Assert
        assertEquals("expected", result)
    }
}
```

#### Testing with Coroutines
```kotlin
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RepositoryTest {
    @Test
    fun testAsyncFunction() = runBlocking {
        val result = repository.fetchData()
        assertNotNull(result)
    }
}
```

### Test Coverage

Current coverage focuses on:
- ✅ JSON parsing accuracy
- ✅ Model structure validation
- ⬜ Network layer (TODO)
- ⬜ Repository caching logic (TODO)
- ⬜ ViewModel state management (TODO)

---

## 📚 Documentation References

### Project Documentation (Spanish)
- **README.md**: Complete step-by-step verification guide with troubleshooting
- **QUICKSTART.md**: 3-minute quick start guide
- **VERSIONS.md**: Detailed explanation of version lock strategy and compatibility
- **ISOLATION.md**: Project isolation levels and build environment info
- **FIXES.md**: Common errors and their solutions
- **STRUCTURE-UPDATE.md**: Data model documentation with JSON examples
- **PHASE-3A.md**: Next development phase planning

### External Documentation
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Android Architecture Components](https://developer.android.com/topic/architecture)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [OkHttp](https://square.github.io/okhttp/)
- [Gson User Guide](https://github.com/google/gson/blob/master/UserGuide.md)
- [Material Design](https://m3.material.io/)

### Key Code Locations

When working on specific features, refer to:

- **Catalog download**: `CatalogService.kt:28` (network layer)
- **File caching**: `CatalogRepository.kt:58` (save), `CatalogRepository.kt:87` (load)
- **ViewModel state**: `CatalogViewModel.kt:19` (LiveData definitions)
- **UI observation**: `MainActivity.kt:73` (LiveData observers)
- **RecyclerView setup**: `MainActivity.kt:154` (adapter configuration)
- **Book list adapter**: `BooksAdapter.kt:1` (ListAdapter implementation)
- **Data models**: `Models.kt:8` (Catalog structure)
- **Database schema**: `AppDatabase.kt:10` (Room tables)

---

## 🎯 AI Assistant Guidelines

### When Assisting with This Project

1. **Respect Version Lock**: Do NOT suggest updating dependencies without explicit user request
2. **Memory Awareness**: Always consider memory efficiency for operations on 40K+ items
3. **Spanish Documentation**: User-facing docs are in Spanish, code comments can be Spanish or English
4. **Test Before Commit**: Always suggest running tests after code changes
5. **Architecture Adherence**: Maintain MVVM pattern, don't bypass layers
6. **Streaming First**: For large data operations, use streaming APIs
7. **Coroutines**: All async operations should use coroutines with proper dispatchers
8. **Null Safety**: Kotlin null safety is critical; never ignore warnings

### Common AI Tasks

1. **"Add a new feature"**
   - Start with data model
   - Add repository methods
   - Update ViewModel
   - Modify UI
   - Write tests

2. **"Fix a bug"**
   - Ask for error logs/stack trace
   - Identify layer where bug occurs
   - Check Logcat output
   - Propose fix with explanation
   - Suggest test to prevent regression

3. **"Update JSON structure"**
   - Update `Models.kt`
   - Update test data in `CatalogParsingTest.kt`
   - Test with real JSON
   - Update documentation

4. **"Optimize performance"**
   - Profile first (Android Profiler)
   - Focus on memory, not premature optimization
   - Consider pagination/lazy loading
   - Use `ListAdapter` DiffUtil efficiently

5. **"Add database table"**
   - Create entity in `Entities.kt`
   - Add DAO in `AppDatabase.kt`
   - Increment database version
   - Update repository
   - Add migration if needed

### Red Flags to Watch For

- ❌ Loading large JSON without streaming
- ❌ Storing large data in SharedPreferences
- ❌ Blocking main thread with network/database calls
- ❌ Updating dependencies without version lock consideration
- ❌ Hard-coding strings instead of using resources
- ❌ Leaking context in ViewModels
- ❌ Not using `viewModelScope` for coroutines

---

## 📞 Getting Help

### Troubleshooting Steps
1. Check `FIXES.md` for common errors
2. Clean build: `./gradlew clean`
3. Invalidate caches: `File → Invalidate Caches → Invalidate and Restart`
4. Check Logcat for errors
5. Verify SDK location and internet connection

### Contact Information
Project repository: (check git remote for URL)

---

**Last Updated**: 2025-11-14
**Document Version**: 1.0
**Project Phase**: Test/Development

---

*This document is intended for AI assistants (like Claude) to understand the codebase structure, conventions, and best practices when assisting with development tasks.*
