package com.guidovezzoni.sfta.domain.model

// The DISCHARGING was present in the JSON sample. All the others have been guessed.
enum class ChargingState {
    CHARGING,
    DISCHARGING,
    FULL,
    UNKNOWN,
}
