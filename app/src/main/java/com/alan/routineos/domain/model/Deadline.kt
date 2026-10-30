package com.alan.routineos.domain.model

data class Deadline(
    val id: String,
    val dueAt: Long,
    val definitionId: String? = null,
    val backlogId: String? = null,
    val instanceId: String? = null
)

data class DeadlineUiModel(
    val id: String,
    val dueAtEpochMilli: Long,
    val displayText: String,
    val isOverdue: Boolean = false,
    val isDueToday: Boolean = false
)

fun formatDeadlineText(
    dueAtMillis: Long,
    now: java.time.ZonedDateTime = java.time.ZonedDateTime.now()
): DeadlineUiModel {
    val dueDate = java.time.Instant.ofEpochMilli(dueAtMillis).atZone(now.zone)
    val today = now.toLocalDate()
    val dueLocalDate = dueDate.toLocalDate()
    val daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, dueLocalDate)
    val hasTime = (dueDate.hour != 0 || dueDate.minute != 0)
    val timeStr = String.format(java.util.Locale.ROOT, "%02d:%02d", dueDate.hour, dueDate.minute)
    val suffix = if (hasTime) " $timeStr" else ""

    val (text, isOverdue, isDueToday) = when {
        now.isAfter(dueDate) && daysUntil < 0 -> Triple("Vencido (${-daysUntil}d)", true, false)
        now.isAfter(dueDate) && daysUntil == 0L && hasTime -> Triple("Vencido hoy$suffix", true, true)
        daysUntil == 0L -> Triple("Vence hoy$suffix", false, true)
        daysUntil == 1L -> Triple("Vence mañana$suffix", false, false)
        daysUntil > 1L -> Triple("Vence en $daysUntil días", false, false)
        else -> Triple("Vencido", true, false)
    }

    return DeadlineUiModel(
        id = dueAtMillis.toString(),
        dueAtEpochMilli = dueAtMillis,
        displayText = text,
        isOverdue = isOverdue,
        isDueToday = isDueToday
    )
}

