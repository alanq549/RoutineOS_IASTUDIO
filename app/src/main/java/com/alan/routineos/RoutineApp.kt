package com.alan.routineos

import android.app.Application
import com.alan.routineos.data.local.DatabaseSeed
import com.alan.routineos.data.local.dao.*
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
class RoutineApp : Application() {

    @Inject
    lateinit var definitionDao: ActivityDefinitionDao

    @Inject
    lateinit var nodeDao: ActivityNodeDao

    @Inject
    lateinit var ruleDao: ScheduleRuleDao

    @Inject
    lateinit var metaDao: MetadataSchemaDao

    @Inject
    lateinit var systemDao: SystemDao

    @Inject
    lateinit var calendarSyncManager: com.alan.routineos.data.calendar.GoogleCalendarSyncManager

    override fun onCreate() {
        super.onCreate()
        calendarSyncManager.startAutoSync()
    }
}
