# Delta Spec: Devices Grouping by Group Name

**Change ID:** `devices-grouping`  
**Date:** 2026-04-22  
**Status:** Spec Phase  
**Project:** DrSecurity — Dr Security (Kotlin Multiplatform + Compose Multiplatform)

---

## 1. Requirements

### 1.1 Functional Requirements

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-1 | DevicesScreen MUST display devices grouped by their `groupName` field, matching the web platform behavior | Must Have |
| FR-2 | Each group MUST display a section header showing the group name and device count (e.g., "Grupo A • 5 unidades") | Must Have |
| FR-3 | Devices without a group (null/empty groupName) MUST be displayed in an "Sin Grupo" section | Must Have |
| FR-4 | The existing filter functionality (All, Online, Offline, Critical) MUST work within the grouped display | Must Have |
| FR-5 | The existing search functionality MUST filter devices across all groups, showing matching devices within their original groups | Must Have |
| FR-6 | Empty groups (groups with zero devices after filtering) MUST NOT be displayed | Should Have |
| FR-7 | Group sections MUST be visually distinct with section labels following the AlertsScreen pattern | Must Have |
| FR-8 | Device selection (tapping a device card) MUST continue to work identically to current behavior | Must Have |

### 1.2 Non-Functional Requirements

| ID | Requirement |
|----|-------------|
| NFR-1 | No breaking changes to existing API contracts or repository interfaces |
| NFR-2 | Maintain backward compatibility with cached device data |
| NFR-3 | Performance: Grouped list rendering MUST NOT introduce noticeable lag with 100+ devices |
| NFR-4 | Code MUST follow existing patterns in App.kt for consistency |

---

## 2. Data Model Changes

### 2.1 DeviceSummary Model

**File:** `composeApp/src/commonMain/kotlin/com/drsecuritygps/app/core/model/Models.kt`

**Current:**
```kotlin
@Serializable
data class DeviceSummary(
    val id: String,
    val name: String,
    val onlineStatus: String = "",
    val alarm: String = "",
    val lastUpdate: String = "",
    val timestampSeconds: Long? = null,
    val speedKph: Double = 0.0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val hasValidCoordinates: Boolean = false,
    val address: String = "",
    val batteryLevel: String? = null,
    val ignition: String? = null,
    val course: String? = null,
)
```

**Proposed Change:**
```kotlin
@Serializable
data class DeviceSummary(
    val id: String,
    val name: String,
    val onlineStatus: String = "",
    val alarm: String = "",
    val lastUpdate: String = "",
    val timestampSeconds: Long? = null,
    val speedKph: Double = 0.0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val hasValidCoordinates: Boolean = false,
    val address: String = "",
    val batteryLevel: String? = null,
    val ignition: String? = null,
    val course: String? = null,
    val groupName: String? = null,  // NEW FIELD
)
```

**Rationale:**
- Adds `groupName` field to capture the group information from API response
- Nullable String to handle devices without groups
- Default value `null` maintains backward compatibility with cached data

### 2.2 New Helper Data Class

**File:** `composeApp/src/commonMain/kotlin/com/drsecuritygps/app/core/model/Models.kt`

**Add at end of file:**
```kotlin
/**
 * Represents a group of devices with their shared group name.
 * Used for displaying grouped devices in DevicesScreen.
 */
data class DeviceGroup(
    val name: String,
    val devices: List<DeviceSummary>,
) {
    val count: Int get() = devices.size
}
```

---

## 3. API Layer Changes

### 3.1 DrSecurityApi Interface

**File:** `composeApp/src/commonMain/kotlin/com/drsecuritygps/app/network/DrSecurityApi.kt`

**No changes required.** The interface method signature remains:
```kotlin
suspend fun getDevices(search: String? = null): List<DeviceSummary>
```

### 3.2 KtorDrSecurityApi Implementation

**File:** `composeApp/src/commonMain/kotlin/com/drsecuritygps/app/network/KtorDrSecurityApi.kt`

**Current Implementation (lines 119-127):**
```kotlin
override suspend fun getDevices(search: String?): List<DeviceSummary> {
    val payload = authGet("get_devices") {
        search?.takeIf { it.isNotBlank() }?.let { parameter("s", it) }
    }.body<JsonArray>()
    return payload.flatMap { group ->
        group.jsonObject.array("items").map { parseDevice(it.jsonObject) }
    }
}
```

**Problem:** Current code flattens groups, losing group information.

**Proposed Change:**
```kotlin
override suspend fun getDevices(search: String?): List<DeviceSummary> {
    val payload = authGet("get_devices") {
        search?.takeIf { it.isNotBlank() }?.let { parameter("s", it) }
    }.body<JsonArray>()
    
    return payload.flatMap { group ->
        val groupName = group.jsonObject.string("name")  // Extract group name
        group.jsonObject.array("items").map { itemObj ->
            parseDevice(itemObj.jsonObject).copy(
                groupName = groupName  // Attach group name to each device
            )
        }
    }
}
```

**Key Changes:**
1. Extract `name` field from each group object in the API response
2. Attach `groupName` to each `DeviceSummary` via `.copy(groupName = groupName)`
3. Maintain flat list return type for backward compatibility with repository layer

### 3.3 parseDevice Function

**File:** `composeApp/src/commonMain/kotlin/com/drsecuritygps/app/network/KtorDrSecurityApi.kt`

**Current (lines 318-341):**
```kotlin
private fun parseDevice(obj: JsonObject): DeviceSummary {
    val sensors = obj.arrayOrJsonString("sensors", json)
    val latitude = obj.double("lat")
    val longitude = obj.double("lng")
    val battery = sensors.firstOrNull { sensor ->
        sensor.jsonObject.string("name").orEmpty().contains("battery", ignoreCase = true)
    }?.jsonObject?.string("value")

    return DeviceSummary(
        id = obj["id"]?.jsonPrimitive?.content.orEmpty(),
        name = obj.string("name").orEmpty(),
        onlineStatus = obj.string("online").orEmpty(),
        alarm = obj.string("alarm").orEmpty(),
        lastUpdate = obj.string("time").orEmpty(),
        timestampSeconds = obj.long("timestamp"),
        speedKph = obj.double("speed") ?: 0.0,
        latitude = latitude ?: 0.0,
        longitude = longitude ?: 0.0,
        hasValidCoordinates = latitude != null && longitude != null,
        address = obj.string("address").orEmpty(),
        batteryLevel = battery,
        ignition = obj.string("engine").orEmpty().ifBlank { null },
        course = obj.string("course"),
    )
}
```

**Proposed Change:** Add `groupName = null` parameter (will be overridden by caller):
```kotlin
private fun parseDevice(obj: JsonObject, groupName: String? = null): DeviceSummary {
    val sensors = obj.arrayOrJsonString("sensors", json)
    val latitude = obj.double("lat")
    val longitude = obj.double("lng")
    val battery = sensors.firstOrNull { sensor ->
        sensor.jsonObject.string("name").orEmpty().contains("battery", ignoreCase = true)
    }?.jsonObject?.string("value")

    return DeviceSummary(
        id = obj["id"]?.jsonPrimitive?.content.orEmpty(),
        name = obj.string("name").orEmpty(),
        onlineStatus = obj.string("online").orEmpty(),
        alarm = obj.string("alarm").orEmpty(),
        lastUpdate = obj.string("time").orEmpty(),
        timestampSeconds = obj.long("timestamp"),
        speedKph = obj.double("speed") ?: 0.0,
        latitude = latitude ?: 0.0,
        longitude = longitude ?: 0.0,
        hasValidCoordinates = latitude != null && longitude != null,
        address = obj.string("address").orEmpty(),
        batteryLevel = battery,
        ignition = obj.string("engine").orEmpty().ifBlank { null },
        course = obj.string("course"),
        groupName = groupName,  // NEW: Accept groupName as parameter
    )
}
```

**Update getDevices() call site accordingly:**
```kotlin
override suspend fun getDevices(search: String?): List<DeviceSummary> {
    val payload = authGet("get_devices") {
        search?.takeIf { it.isNotBlank() }?.let { parameter("s", it) }
    }.body<JsonArray>()
    
    return payload.flatMap { group ->
        val groupName = group.jsonObject.string("name")
        group.jsonObject.array("items").map { itemObj ->
            parseDevice(itemObj.jsonObject, groupName)  // Pass groupName directly
        }
    }
}
```

---

## 4. UI Changes

### 4.1 AppController Grouping Logic

**File:** `composeApp/src/commonMain/kotlin/com/drsecuritygps/app/presentation/AppController.kt`

**Add new helper method:**
```kotlin
/**
 * Groups visible devices by their groupName field.
 * Devices without a group are placed in "Sin Grupo" section.
 * Empty groups are filtered out.
 */
fun groupedVisibleDevices(): List<DeviceGroup> {
    val devices = visibleDevices()
    
    // Group by groupName, using "Sin Grupo" for null/empty
    val grouped = devices.groupBy { device ->
        device.groupName?.takeIf { it.isNotBlank() } ?: "Sin Grupo"
    }
    
    // Convert to DeviceGroup list, filtering empty groups
    return grouped
        .filterValues { it.isNotEmpty() }
        .map { (name, devices) -> DeviceGroup(name = name, devices = devices) }
        .sortedBy { group -> 
            if (group.name == "Sin Grupo") Int.MAX_VALUE else 0 
        }
}
```

**Note:** "Sin Grupo" section is sorted to appear last.

### 4.2 DevicesScreen Composable

**File:** `composeApp/src/commonMain/kotlin/com/drsecuritygps/app/App.kt`

**Current Signature (lines 560-571):**
```kotlin
@Composable
private fun DevicesScreen(
    devices: UiState<List<DeviceSummary>>,
    visibleDevices: List<DeviceSummary>,
    search: String,
    filter: DeviceFilter,
    onSearchChange: (String) -> Unit,
    onFilterChange: (DeviceFilter) -> Unit,
    onDeviceSelected: (String) -> Unit,
    onGoMap: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoCommands: () -> Unit,
    onGoProfile: () -> Unit,
)
```

**Proposed Change:** Add `groupedDevices` parameter:
```kotlin
@Composable
private fun DevicesScreen(
    devices: UiState<List<DeviceSummary>>,
    visibleDevices: List<DeviceSummary>,
    groupedDevices: List<DeviceGroup>,  // NEW PARAMETER
    search: String,
    filter: DeviceFilter,
    onSearchChange: (String) -> Unit,
    onFilterChange: (DeviceFilter) -> Unit,
    onDeviceSelected: (String) -> Unit,
    onGoMap: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoCommands: () -> Unit,
    onGoProfile: () -> Unit,
)
```

### 4.3 TabsScreen Update

**File:** `composeApp/src/commonMain/kotlin/com/drsecuritygps/app/App.kt`

**Current (lines 331-342):**
```kotlin
RootTab.Devices -> DevicesScreen(
    devices = state.devices,
    visibleDevices = controller.visibleDevices(),
    search = state.searchQuery,
    filter = state.deviceFilter,
    onSearchChange = controller::updateSearch,
    onFilterChange = controller::updateFilter,
    onDeviceSelected = controller::openDetail,
    onGoMap = { controller.selectTab(RootTab.Map) },
    onGoAlerts = { controller.selectTab(RootTab.Alerts) },
    onGoCommands = { controller.selectTab(RootTab.Commands) },
    onGoProfile = { controller.selectTab(RootTab.Profile) },
)
```

**Proposed Change:**
```kotlin
RootTab.Devices -> DevicesScreen(
    devices = state.devices,
    visibleDevices = controller.visibleDevices(),
    groupedDevices = controller.groupedVisibleDevices(),  // NEW
    search = state.searchQuery,
    filter = state.deviceFilter,
    onSearchChange = controller::updateSearch,
    onFilterChange = controller::updateFilter,
    onDeviceSelected = controller::openDetail,
    onGoMap = { controller.selectTab(RootTab.Map) },
    onGoAlerts = { controller.selectTab(RootTab.Alerts) },
    onGoCommands = { controller.selectTab(RootTab.Commands) },
    onGoProfile = { controller.selectTab(RootTab.Profile) },
)
```

### 4.4 DevicesScreen Content Rendering

**File:** `composeApp/src/commonMain/kotlin/com/drsecuritygps/app/App.kt`

**Current Rendering Logic (lines 588-600):**
```kotlin
is UiState.Success -> {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("DISPOSITIVOS", color = AppPrimary.copy(alpha = 0.75f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("${visibleDevices.size} unidades", color = AppMuted, fontSize = 11.sp)
        }
    }
    Spacer(Modifier.height(10.dp))
    LazyColumn(
        modifier = Modifier.weight(1f),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(visibleDevices, key = { it.id }) { device ->
            DeviceListCard(device, onClick = { onDeviceSelected(device.id) })
        }
    }
}
```

**Proposed Change (Grouped Display following AlertsScreen pattern):**
```kotlin
is UiState.Success -> {
    // Show total count header
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("DISPOSITIVOS", color = AppPrimary.copy(alpha = 0.75f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("${visibleDevices.size} unidades", color = AppMuted, fontSize = 11.sp)
        }
    }
    Spacer(Modifier.height(10.dp))
    
    LazyColumn(
        modifier = Modifier.weight(1f),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Render each group as a section
        items(groupedDevices, key = { it.name }) { group ->
            // Group header (following AlertsScreen SectionLabel pattern)
            Text(
                text = "${group.name} • ${group.count} ${if (group.count == 1) "unidad" else "unidades"}",
                color = AppPrimary.copy(alpha = 0.75f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            )
            
            // Devices in this group
            group.devices.forEach { device ->
                DeviceListCard(device, onClick = { onDeviceSelected(device.id) })
            }
        }
    }
}
```

**Visual Reference (AlertsScreen pattern from lines 1533-1534):**
```kotlin
item { SectionLabel("ÚLTIMAS ALERTAS") }
items(alerts.data, key = { it.id }) { alert -> AlertCard(alert, bright = true) }
```

---

## 5. Edge Cases

### 5.1 Devices Without Groups

**Scenario:** API returns devices with null or empty `groupName`.

**Handling:**
- Display in "Sin Grupo" section
- Section appears LAST in the list (sorted by placing at end)
- Section header: `"Sin Grupo • X unidades"`

### 5.2 Empty Groups After Filtering

**Scenario:** User applies filter (e.g., "Online") and a group has zero matching devices.

**Handling:**
- Group is NOT displayed (filtered out by `.filterValues { it.isNotEmpty() }`)
- No visual indication of empty groups
- Consistent with AlertsScreen behavior (no empty sections)

### 5.3 All Devices Filtered Out

**Scenario:** Search or filter results in zero visible devices.

**Handling:**
- `groupedDevices` returns empty list
- DevicesScreen shows existing `ScreenEmpty("Sin unidades disponibles")` state
- No change to current empty state behavior

### 5.4 Single Device in Group

**Scenario:** Group contains only one device.

**Handling:**
- Section header uses singular: `"Grupo A • 1 unidad"` (not "unidades")
- Implemented via conditional: `if (group.count == 1) "unidad" else "unidades"`

### 5.5 Very Long Group Names

**Scenario:** Group name exceeds available width.

**Handling:**
- Compose Text component automatically truncates with ellipsis
- No special handling required (existing Compose behavior)
- Consider `maxLines = 1, overflow = TextOverflow.Ellipsis` if needed

### 5.6 Cached Data Without groupName

**Scenario:** App loads cached devices from previous version (before this feature).

**Handling:**
- `groupName` defaults to `null` in DeviceSummary
- Devices appear in "Sin Grupo" section
- Next API refresh populates correct group names
- Graceful degradation, no crash

### 5.7 Search Across Groups

**Scenario:** User searches for "truck" and devices match in multiple groups.

**Handling:**
- Search filters `visibleDevices` first (existing behavior)
- `groupedVisibleDevices()` groups the already-filtered list
- Matching devices shown within their original groups
- Non-matching groups are hidden (empty after filter)
- Example: Searching "truck" shows:
  - "Grupo A • 1 unidad" (1 matching truck)
  - "Grupo C • 2 unidades" (2 matching trucks)
  - "Sin Grupo • 1 unidad" (1 matching truck without group)

---

## 6. Acceptance Criteria

### 6.1 Functional Tests

| ID | Criterion | Verification Method |
|----|-----------|---------------------|
| AC-1 | Devices are displayed grouped by `groupName` field | Manual: Launch app, observe DevicesScreen shows section headers with group names |
| AC-2 | Each group header shows group name and device count | Manual: Verify header format "Grupo X • N unidades" |
| AC-3 | Devices without groups appear in "Sin Grupo" section | Manual: Filter/search to show ungrouped devices, verify section appears last |
| AC-4 | Filter "En línea" shows only online devices within their groups | Manual: Apply filter, verify only online devices visible, grouped correctly |
| AC-5 | Filter "Fuera de línea" shows only offline devices within their groups | Manual: Apply filter, verify only offline devices visible, grouped correctly |
| AC-6 | Filter "Alarmas" shows only devices with alarms within their groups | Manual: Apply filter, verify only alarmed devices visible, grouped correctly |
| AC-7 | Search filters devices across all groups | Manual: Search term matching devices in multiple groups, verify all matches shown in respective groups |
| AC-8 | Empty groups (after filtering) are not displayed | Manual: Apply filter that excludes all devices from a group, verify group header not shown |
| AC-9 | Tapping a device card navigates to DeviceDetail | Manual: Tap any device, verify navigation occurs |
| AC-10 | Singular/plural agreement in group headers (1 unidad vs N unidades) | Manual: Find group with 1 device, verify "unidad"; group with 2+ devices, verify "unidades" |

### 6.2 Code Review Checklist

| ID | Criterion | Verification Method |
|----|-----------|---------------------|
| AC-11 | `DeviceSummary` has `groupName: String? = null` field | Code review: Models.kt |
| AC-12 | `DeviceGroup` data class exists with `name`, `devices`, `count` | Code review: Models.kt |
| AC-13 | `KtorDrSecurityApi.getDevices()` extracts and attaches `groupName` | Code review: KtorDrSecurityApi.kt |
| AC-14 | `AppController.groupedVisibleDevices()` method exists | Code review: AppController.kt |
| AC-15 | `DevicesScreen` accepts `groupedDevices: List<DeviceGroup>` parameter | Code review: App.kt |
| AC-16 | `DevicesScreen` renders groups with section headers | Code review: App.kt |
| AC-17 | "Sin Grupo" section sorted to appear last | Code review: AppController.kt sorting logic |
| AC-18 | No breaking changes to `DrSecurityApi` interface | Code review: DrSecurityApi.kt unchanged |
| AC-19 | No changes to `DevicesRepository` interface | Code review: DevicesRepository.kt unchanged |
| AC-20 | Existing `visibleDevices()` method unchanged (backward compatibility) | Code review: AppController.kt |

### 6.3 Integration Tests

| ID | Criterion | Verification Method |
|----|-----------|---------------------|
| AC-21 | API response with groups parses correctly | Unit test: Mock grouped API response, verify `groupName` populated |
| AC-22 | Grouping logic handles null/empty group names | Unit test: Pass devices with null/empty groupName, verify "Sin Grupo" grouping |
| AC-23 | Filter + grouping combination works correctly | Unit test: Apply filter, group results, verify correct devices in correct groups |
| AC-24 | Search + grouping combination works correctly | Unit test: Apply search, group results, verify correct devices in correct groups |
| AC-25 | Empty group filtering works | Unit test: Create group with zero devices after filter, verify not in output |

### 6.4 Visual/UX Verification

| ID | Criterion | Verification Method |
|----|-----------|---------------------|
| AC-26 | Group section headers match AlertsScreen visual style | Manual: Compare section label styling between DevicesScreen and AlertsScreen |
| AC-27 | Device cards within groups maintain existing styling | Manual: Verify DeviceListCard appearance unchanged |
| AC-28 | Scrolling performance acceptable with 100+ devices across 10+ groups | Manual: Load large dataset, verify smooth scrolling |
| AC-29 | Group headers remain visible while scrolling through devices | Manual: Scroll through long group, verify header stays visible (or sticky if implemented) |

---

## 7. Out of Scope

The following are explicitly OUT OF SCOPE for this change:

1. **Map View Grouping:** LiveMapScreen continues to show devices as a flat list. Map view grouping is a future enhancement.
2. **Backend Changes:** Backend already supports groups; no backend changes required.
3. **Group Management:** Creating, editing, or deleting groups is not part of this feature.
4. **Group-level Actions:** No bulk actions on groups (e.g., "select all devices in group").
5. **Collapsible Groups:** Groups are always expanded; no collapse/expand functionality.
6. **Sticky Headers:** Group headers scroll normally; sticky header behavior is a future enhancement.
7. **Group Sorting:** Groups are not sorted alphabetically or by any criteria (future enhancement).
8. **Device Reordering:** Devices within groups maintain API order; no manual reordering.

---

## 8. Implementation Notes

### 8.1 Migration Path

1. **Phase 1 (This Spec):** Add `groupName` field, parse from API, display grouped in DevicesScreen
2. **Phase 2 (Future):** Add grouping to MapView
3. **Phase 3 (Future):** Add group management UI (create/edit/delete groups)

### 8.2 Testing Strategy

- **Unit Tests:** Test `groupedVisibleDevices()` logic with various inputs
- **Integration Tests:** Test API parsing with mock grouped responses
- **Manual Testing:** Verify all acceptance criteria on real device

### 8.3 Rollback Plan

If issues arise:
1. Revert `DeviceSummary` model change (remove `groupName` field)
2. Revert `KtorDrSecurityApi` changes (restore flat parsing)
3. Revert `AppController` and `DevicesScreen` changes
4. App continues to work with flat device list (previous behavior)

---

## 9. Files to Modify

| File | Changes |
|------|---------|
| `Models.kt` | Add `groupName` to `DeviceSummary`, add `DeviceGroup` data class |
| `KtorDrSecurityApi.kt` | Update `getDevices()` to extract and attach `groupName`, update `parseDevice()` signature |
| `AppController.kt` | Add `groupedVisibleDevices()` method |
| `App.kt` | Update `DevicesScreen` signature and rendering logic, update `TabsScreen` call site |

---

## 10. Success Metrics

- ✅ All 29 acceptance criteria pass
- ✅ No regressions in existing device list functionality
- ✅ Visual consistency with AlertsScreen section pattern
- ✅ Performance: <16ms frame time with 100+ devices
- ✅ User feedback: Matches web platform grouping behavior

---

**Next Phase:** Design Document (architecture decisions, implementation approach)
