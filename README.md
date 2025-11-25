# Конвертер Валута

Мобилна Android апликација за конверзију валута са интеграцијом API-ја за тренутне девизне курсеве.

## Функционалности

-  **Реална конверзија валута** - Подржава преко 8 светских валута (USD, EUR, GBP, JPY, CAD, AUD, CHF, CNY)
-  **Историја конверзија** - Аутоматски чување историје конверзија у локалној бази
-  **Интуитивни интерфејс** - Једноставна и читљива дизајна апликације
-  **Shake детектор** - Освежи девизне курсеве потресањем телефона
-  **Оптимизирана за мобилне** - Подржава различите величине екрана

## Технолошка стања

### Архитектура
- **Архитектурни образац**: MVVM (Model-View-ViewModel)
- **Језик**: Java и Kotlin

### Библиотеке
- **UI**: Android AppCompat, Material Design, RecyclerView
- **Мрежа**: Retrofit 2 са Gson конвертором
- **База**: Room ORM за локално чување
- **Сензори**: SensorManager за детекцију потреса
- **Животни циклус**: AndroidX Lifecycle, LiveData, ViewModel

## Захтеви

- Android 7.0 (API 24) или новије
- Java 11+ за компајлирање

## Структура пројекта

```
app/src/main/
├── java/com/example/currencyconverter/
│   ├── activities/          # Android активности
│   ├── adapters/            # RecyclerView адаптери
│   ├── database/            # Room база податке
│   ├── models/              # Модели податке
│   ├── network/             # Retrofit конфигурација
│   ├── sensors/             # Сензор интеграција
│   └── viewmodels/          # ViewModel класе
└── res/
    ├── layout/              # XML распореди
    ├── values/              # Строкови и боје
    └── drawable/            # Слике и дизајн
```

## Како покренути

### Предуслови
1. Инсталирајте Android Studio (верзија 2024.1 или новија)
2. Препоручује се JDK 11 или новији

### Кораци

1. **Клонирајте репозиторијум**
   ```bash
   git clone https://github.com/MimicsLife/RMA-Projekat.git
   cd RMA-Projekat
   ```

2. **Отворите пројекат у Android Studio**
   - Биће аутоматски преузети сви Gradle зависности

3. **Подесите Java верзију** (ако је потребно)
   - Идите на `gradle.properties`
   - Убедите се да `org.gradle.java.home` показује на JDK 11+

4. **Покренете апликацију**
   - Кликните на `Run` у Android Studio или притисните `Shift + F10`

## Конфигурација

### API Конфигурација
Апликација користи следећи API за девизне курсеве:
- Базни URL: `https://api.exchangerate-api.com/v4/latest/`

### Локална база
- Апликација аутоматски чува историју конверзија
- Максимално 50 аспирантских записа
- Старији записи се аутоматски бришу

## Развој

### Изградити апликацију

```bash
./gradlew assembleDebug      # За debug верзију
./gradlew assembleRelease    # За release верзију
```

### Покренути тестове

```bash
./gradlew test               # Unit тестови
./gradlew connectedAndroidTest # Instrumented тестови
```

## Архитектура компонената

### MainActivity
Главна активност која садржи интерфејс корисника са полјима за унос износа и избором валута.

### CurrencyViewModel
ViewModel која управља логиком конверзије и комуникацијом са API-јем.

### AppDatabase
Room база која чува историју конверзија са DAO за приступ.

### CurrencyApi
Retrofit интерфејс за комуникацију са API-јем девизних курсева.

### ShakeDetector
Сензор послушивач који детектује потресање уређаја за освежавање курсева.

## Технички детаљи

- **API**: Retrofit 2 за асинкрене HTTP захтеве
- **JSON**: Gson за серијализацију/десеријализацију
- **Локална база**: Room ORM систем
- **Конкурентност**: LiveData за реактивни програмски ток
- **Сензори**: Android SensorManager за детекцију покрета

## Решавање проблема

### Апликација се не покреће
- Проверите да ли је JDK 11+ активан
- Обришите `build/` фасциклу и поново градите пројекат
- Синхронизујте Gradle зависности: `./gradlew --refresh-dependencies`

### API захтеви не раде
- Проверите конекцију на интернет
- Убедите се да је API доступан: `https://api.exchangerate-api.com/v4/latest/USD`

### База не чува податке
- Проверите дозволе апликације у подешавањима
- Обришите и поново инсталирајте апликацију

## Додатне ресурсе

- [Android Developer Документација](https://developer.android.com)
- [Retrofit Документација](https://square.github.io/retrofit/)
- [Room Документација](https://developer.android.com/training/data-storage/room)
- [Exchange Rate API](https://www.exchangerate-api.com)
