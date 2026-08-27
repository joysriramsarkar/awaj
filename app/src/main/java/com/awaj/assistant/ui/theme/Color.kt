package com.awaj.assistant.ui.theme

import androidx.compose.ui.graphics.Color

val BrandPrimary = Color(0xFF6366F1)       // Indigo Glow
val BrandPrimaryDark = Color(0xFF4F46E5)
val BrandSecondary = Color(0xFF14B8A6)     // Teal
val BrandSuccess = Color(0xFF10B981)       // Emerald
val BrandDanger = Color(0xFFF43F5E)        // Rose Red
val BrandWarning = Color(0xFFF59E0B)       // Amber

// Dark Theme Palette
val DarkBackground = Color(0xFF0B0F19)     // Sleek Deep Obsidian
val DarkSurface = Color(0xFF131B2E)        // Midnight Card Surface
val DarkSurfaceCard = Color(0xFF1E293B)    // Slate Card Surface
val DarkSurfaceGlass = Color(0xCC1E293B)

// Light Theme Palette
val LightBackground = Color(0xFFF8FAFC)    // Clean Soft Slate
val LightSurface = Color(0xFFFFFFFF)       // Crisp White
val LightSurfaceCard = Color(0xFFF1F5F9)   // Light Card Surface
val LightSurfaceGlass = Color(0xCCFFFFFF)

// Typography Palette
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF475569)
val LightTextMuted = Color(0xFF94A3B8)

val GlowPurple = Color(0xFF8B5CF6)
val GlowTeal = Color(0xFF06B6D4)

enum class AppThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}
