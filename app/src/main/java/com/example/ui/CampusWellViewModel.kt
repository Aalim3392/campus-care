package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.gemini.GeminiService
import com.example.data.local.AppDatabase
import com.example.data.supabase.SupabaseService
import com.example.model.AlertType
import com.example.model.ChatMessage
import com.example.model.ChatSender
import com.example.model.CheckInRecord
import com.example.model.DepartmentStudent
import com.example.model.StressAlert
import com.example.model.StressStatus
import com.example.model.SupabaseConfig
import com.example.model.SupportRequestRecord
import com.example.model.UserProfile
import com.example.model.UserRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class CampusWellViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val checkInDao = db.checkInDao()
    private val supabaseService = SupabaseService(application)
    private val geminiService = GeminiService()

    // Current User Profile
    val userProfile = MutableStateFlow(
        UserProfile(
            name = "Aarav Sharma",
            email = "aarav.sharma@kkwagh.edu.in",
            department = "Computer Engineering Dept",
            section = "Comp Eng · Div A / Year 3",
            role = UserRole.STUDENT,
            isLoggedIn = true
        )
    )

    // Login Form State
    val loginEmail = MutableStateFlow("aarav.sharma@kkwagh.edu.in")
    val loginPassword = MutableStateFlow("password123")
    val loginRole = MutableStateFlow(UserRole.STUDENT)
    val loginError = MutableStateFlow<String?>(null)

    // All check-in records from Room
    val allCheckIns: StateFlow<List<CheckInRecord>> = checkInDao.getAllCheckIns()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's Check-in Record or Active Form
    val selectedMood = MutableStateFlow("Good")
    val sleepHours = MutableStateFlow(7.0f)
    val waterGlasses = MutableStateFlow(6)
    val studyHours = MutableStateFlow(4.0f)
    val stressLevel = MutableStateFlow(2)

    // AI Stress Alerts
    val activeAlerts = MutableStateFlow<List<StressAlert>>(emptyList())
    val latestStudentAdvice = MutableStateFlow<String?>(null)
    val facultyAlertMessage = MutableStateFlow<String?>(null)

    // Faculty & HOD Department Students Cohort
    val departmentFilter = MutableStateFlow("All") // "All", "Div A", "Div B", "Attention Needed"
    val departmentStudents = MutableStateFlow<List<DepartmentStudent>>(
        listOf(
            DepartmentStudent(
                id = "std_01",
                name = "Aarav Sharma",
                rollNo = "CE-301",
                email = "aarav.sharma@kkwagh.edu.in",
                division = "Div A",
                lastMood = "Good",
                sleepHours = 7.5f,
                waterGlasses = 6,
                stressLevel = 2,
                stressStatus = StressStatus.NORMAL,
                aiAdvice = "Optimal study balance. Pacing within safe threshold."
            ),
            DepartmentStudent(
                id = "std_02",
                name = "Rohan Kulkarni",
                rollNo = "CE-314",
                email = "rohan.kulkarni@kkwagh.edu.in",
                division = "Div A",
                lastMood = "Stressed",
                sleepHours = 4.0f,
                waterGlasses = 3,
                stressLevel = 5,
                stressStatus = StressStatus.CRITICAL_FACULTY_ESCALATION,
                aiAdvice = "Severe sleep deficit with acute project pressure. Escalated to Faculty Care Coordinator.",
                facultyNotified = true
            ),
            DepartmentStudent(
                id = "std_03",
                name = "Priya Deshmukh",
                rollNo = "CE-322",
                email = "priya.d@kkwagh.edu.in",
                division = "Div A",
                lastMood = "Low",
                sleepHours = 5.0f,
                waterGlasses = 4,
                stressLevel = 4,
                stressStatus = StressStatus.WATCHLIST_WARNING,
                aiAdvice = "Early stress warning sent to student. 15-min restorative pause suggested."
            ),
            DepartmentStudent(
                id = "std_04",
                name = "Sneha Patil",
                rollNo = "CE-335",
                email = "sneha.p@kkwagh.edu.in",
                division = "Div B",
                lastMood = "Great",
                sleepHours = 8.0f,
                waterGlasses = 7,
                stressLevel = 1,
                stressStatus = StressStatus.NORMAL,
                aiAdvice = "Excellent restorative rhythm."
            ),
            DepartmentStudent(
                id = "std_05",
                name = "Vikram Joshi",
                rollNo = "CE-342",
                email = "vikram.j@kkwagh.edu.in",
                division = "Div B",
                lastMood = "Stressed",
                sleepHours = 4.5f,
                waterGlasses = 4,
                stressLevel = 5,
                stressStatus = StressStatus.CRITICAL_FACULTY_ESCALATION,
                aiAdvice = "Consecutive acute stress flags. Counseling desk notified.",
                facultyNotified = true
            ),
            DepartmentStudent(
                id = "std_06",
                name = "Ananya Iyer",
                rollNo = "CE-351",
                email = "ananya.i@kkwagh.edu.in",
                division = "Div B",
                lastMood = "Okay",
                sleepHours = 6.5f,
                waterGlasses = 6,
                stressLevel = 3,
                stressStatus = StressStatus.WATCHLIST_WARNING,
                aiAdvice = "Moderate midterm tension. Gentle breathing tips sent."
            )
        )
    )

    // Toast and feedback event
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    // Support Request form
    val supportTopic = MutableStateFlow("Study Pressure & Deadlines")
    val supportMessage = MutableStateFlow("")
    val supportSubmitted = MutableStateFlow(false)

    // Supabase State
    val supabaseConfig = MutableStateFlow(SupabaseConfig())
    val isTestingSupabase = MutableStateFlow(false)
    val supabaseStatusMessage = MutableStateFlow("Checking connection...")

    // AI Counselor Chat
    val chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "welcome",
                sender = ChatSender.ASSISTANT,
                text = "Namaste Aarav! I'm your KK Wagh CampusWell AI Counselor. How are your Computer Engineering lab submissions, lectures, and daily pacing going today?"
            )
        )
    )
    val isAiThinking = MutableStateFlow(false)

    // AI Image Generator
    val imageGenPrompt = MutableStateFlow("Serene minimalist misty lake at sunrise with calm pastel mountains, peaceful reflection, high aesthetics")
    val imageGenSize = MutableStateFlow("1K") // 1K, 2K, 4K
    val isGeneratingImage = MutableStateFlow(false)
    val generatedBitmap = MutableStateFlow<Bitmap?>(null)
    val imageGenError = MutableStateFlow<String?>(null)

    // AI Insight on Dashboard
    val dashboardAiInsight = MutableStateFlow("Logging daily helps detect workload triggers before exam periods. You have maintained a steady 4-day streak!")

    init {
        // Observe Supabase config
        viewModelScope.launch {
            supabaseService.configFlow.collect { (url, key) ->
                supabaseConfig.value = SupabaseConfig(
                    url = url,
                    anonKey = key,
                    isConnected = url.isNotBlank() && key.isNotBlank()
                )
                if (url.isNotBlank() && key.isNotBlank()) {
                    supabaseStatusMessage.value = "Connected to Supabase Cloud"
                } else {
                    supabaseStatusMessage.value = "Local encrypted storage active (Supabase not configured)"
                }
            }
        }
    }

    /**
     * Enforce official college domain: @kkwagh.edu.in
     */
    fun login(email: String, role: UserRole) {
        val cleanEmail = email.trim().lowercase()
        if (!cleanEmail.endsWith("@kkwagh.edu.in")) {
            loginError.value = "Access Restricted: You must use your official college email ending with @kkwagh.edu.in"
            return
        }

        loginError.value = null
        val username = cleanEmail.substringBefore("@")
            .replace(".", " ")
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }

        val (dept, sec) = when (role) {
            UserRole.STUDENT -> Pair("Computer Engineering Dept", "Comp Eng · Div A / Year 3")
            UserRole.FACULTY -> Pair("Computer Engineering Dept", "Faculty Mentor & Counseling Desk")
            UserRole.HOD -> Pair("Computer Engineering Dept", "Head of Department (HOD)")
        }

        userProfile.value = UserProfile(
            name = username.ifBlank { if (role == UserRole.STUDENT) "Aarav Sharma" else "Prof. Dr. P. B. Sharma" },
            email = cleanEmail,
            department = dept,
            section = sec,
            role = role,
            isLoggedIn = true
        )

        viewModelScope.launch {
            _toastEvent.emit("Logged in successfully as ${role.name}: $cleanEmail")
        }
    }

    fun logout() {
        userProfile.value = userProfile.value.copy(isLoggedIn = false)
        loginError.value = null
    }

    fun adjustWater(delta: Int) {
        val current = waterGlasses.value
        val updated = (current + delta).coerceIn(1, 20)
        waterGlasses.value = updated
    }

    fun quickAddWater(glasses: Int) {
        val updated = (waterGlasses.value + glasses).coerceIn(1, 24)
        waterGlasses.value = updated
        viewModelScope.launch {
            _toastEvent.emit("Added ${glasses * 250} ml of water! Total: ${(updated * 0.25f)} L")
            saveCheckIn(silent = true)
        }
    }

    /**
     * Save daily health check-in to Room and Supabase.
     * Evaluates stress level:
     * - Levels 3-4: Proactively informs student with early coping advice.
     * - Level 5: Automatically informs faculty responsible for mental health & stress management.
     */
    fun saveCheckIn(silent: Boolean = false) {
        viewModelScope.launch {
            val profile = userProfile.value
            val currentStress = stressLevel.value
            val currentMood = selectedMood.value
            val currentSleep = sleepHours.value
            val currentWater = waterGlasses.value
            val currentStudy = studyHours.value

            val record = CheckInRecord(
                id = "today_record",
                date = "2025-04-28",
                dayOfWeek = "Mon",
                mood = currentMood,
                sleepHours = currentSleep,
                waterGlasses = currentWater,
                studyHours = currentStudy,
                stressLevel = currentStress,
                userEmail = profile.email,
                userName = profile.name,
                userRole = profile.role.name,
                timestamp = System.currentTimeMillis()
            )

            checkInDao.insertCheckIn(record)

            // Save to Supabase Cloud
            val syncResult = supabaseService.syncCheckIn(record)
            if (syncResult.isSuccess) {
                checkInDao.markCheckInSynced(record.id)
            }

            // Proactive AI Stress Analysis
            evaluateStressAndNotify(record)

            if (!silent) {
                val supabaseNotice = if (syncResult.isSuccess) " (Synced to Supabase Cloud)" else ""
                _toastEvent.emit("Check-in saved securely!$supabaseNotice")
            }

            // Update AI Insight
            generateAiInsight(record)
        }
    }

    private fun evaluateStressAndNotify(record: CheckInRecord) {
        viewModelScope.launch {
            val profile = userProfile.value
            val sLevel = record.stressLevel

            if (sLevel >= 5) {
                // Critical Stress: Inform student AND automatically escalate to Department Mental Health Faculty
                val facultyMsg = "High Acute Stress (Level 5) logged by ${profile.name} (${profile.email}). Critical intervention recommended."
                val alert = StressAlert(
                    id = UUID.randomUUID().toString(),
                    studentName = profile.name,
                    studentEmail = profile.email,
                    stressLevel = sLevel,
                    alertType = AlertType.FACULTY_CRISIS_ESCALATION,
                    aiMessage = "AI Care Notice: You are reporting acute fatigue/stress. We have confidentially informed Prof. Dr. Sharma (Head of Department Counseling & Wellness Desk). You are not alone—please rest and take time away from coding labs.",
                    facultyInformedName = "Prof. Dr. P. B. Sharma (Mental Health & Counseling Lead)"
                )
                activeAlerts.value = listOf(alert) + activeAlerts.value
                latestStudentAdvice.value = alert.aiMessage
                facultyAlertMessage.value = facultyMsg

                // Sync alert to Supabase
                supabaseService.syncStressAlert(alert)
                _toastEvent.emit("Notice: Confidentially alerted Department Faculty Counselor for gentle pacing support.")

            } else if (sLevel in 3..4) {
                // Early Warning: Inform student before it escalates to high stress!
                val earlyWarningMsg = "Early Stress Warning: Workload strain detected (${sLevel}/5) with ${record.sleepHours}h sleep. Recommendation: Take a 15-minute nature walk on Saraswati campus block, do 3 minutes of box breathing, and stagger pending lab deadlines."
                val alert = StressAlert(
                    id = UUID.randomUUID().toString(),
                    studentName = profile.name,
                    studentEmail = profile.email,
                    stressLevel = sLevel,
                    alertType = AlertType.EARLY_WARNING_STUDENT,
                    aiMessage = earlyWarningMsg
                )
                activeAlerts.value = listOf(alert) + activeAlerts.value
                latestStudentAdvice.value = earlyWarningMsg

                // Sync alert to Supabase
                supabaseService.syncStressAlert(alert)
            } else {
                latestStudentAdvice.value = null
            }
        }
    }

    fun dismissStudentAdvice() {
        latestStudentAdvice.value = null
    }

    private fun generateAiInsight(record: CheckInRecord) {
        viewModelScope.launch {
            val result = geminiService.generateMetricInsight(
                sleep = record.sleepHours,
                water = record.waterGlasses,
                study = record.studyHours,
                mood = record.mood,
                stress = record.stressLevel
            )
            result.onSuccess { insight ->
                dashboardAiInsight.value = insight
            }
        }
    }

    fun submitSupportRequest() {
        val topic = supportTopic.value
        val msg = supportMessage.value
        if (topic.isBlank()) return

        viewModelScope.launch {
            val profile = userProfile.value
            val req = SupportRequestRecord(
                id = UUID.randomUUID().toString(),
                topic = topic,
                message = msg,
                date = "Mon, 28 Apr 2025",
                userEmail = profile.email,
                userName = profile.name
            )
            checkInDao.insertSupportRequest(req)
            supabaseService.syncSupportRequest(req)
            supportSubmitted.value = true
            _toastEvent.emit("Request submitted privately to Student Care Desk.")
        }
    }

    fun resetSupportForm() {
        supportMessage.value = ""
        supportSubmitted.value = false
    }

    fun sendChatMessage(messageText: String) {
        if (messageText.isBlank()) return
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = ChatSender.USER,
            text = messageText
        )
        chatMessages.value = chatMessages.value + userMsg
        isAiThinking.value = true

        viewModelScope.launch {
            val history = chatMessages.value.map {
                (if (it.sender == ChatSender.USER) "user" else "model") to it.text
            }
            val result = geminiService.chatWithCounselor(history, messageText)
            isAiThinking.value = false
            result.onSuccess { reply ->
                val assistantMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = ChatSender.ASSISTANT,
                    text = reply
                )
                chatMessages.value = chatMessages.value + assistantMsg
            }.onFailure { err ->
                val assistantMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = ChatSender.ASSISTANT,
                    text = "I'm right here with you! (${err.message ?: "Network pause"}). Remember: take a steady breath, drink water, and pause for 5 minutes."
                )
                chatMessages.value = chatMessages.value + assistantMsg
            }
        }
    }

    fun generateZenImage() {
        val prompt = imageGenPrompt.value
        if (prompt.isBlank()) return
        isGeneratingImage.value = true
        imageGenError.value = null
        generatedBitmap.value = null

        viewModelScope.launch {
            val result = geminiService.generateHighQualityImage(
                prompt = prompt,
                imageSize = imageGenSize.value,
                aspectRatio = "16:9"
            )
            isGeneratingImage.value = false
            result.onSuccess { bitmap ->
                generatedBitmap.value = bitmap
                _toastEvent.emit("Calm meditation scene generated in ${imageGenSize.value} resolution!")
            }.onFailure { err ->
                imageGenError.value = err.message ?: "Generation error"
                _toastEvent.emit("Notice: ${err.message}")
            }
        }
    }

    fun testSupabaseConnection(url: String, anonKey: String) {
        viewModelScope.launch {
            isTestingSupabase.value = true
            supabaseService.saveConfig(url, anonKey)
            val result = supabaseService.testConnection()
            isTestingSupabase.value = false
            result.onSuccess { msg ->
                supabaseStatusMessage.value = "Success: $msg"
                _toastEvent.emit("Supabase connected successfully!")
                syncAllPendingToSupabase()
            }.onFailure { err ->
                supabaseStatusMessage.value = "Error: ${err.message}"
                _toastEvent.emit("Supabase test failed: ${err.message}")
            }
        }
    }

    private fun syncAllPendingToSupabase() {
        viewModelScope.launch {
            val pending = checkInDao.getUnsyncedCheckIns()
            for (item in pending) {
                val res = supabaseService.syncCheckIn(item)
                if (res.isSuccess) {
                    checkInDao.markCheckInSynced(item.id)
                }
            }
            val pendingReqs = checkInDao.getUnsyncedSupportRequests()
            for (r in pendingReqs) {
                val res = supabaseService.syncSupportRequest(r)
                if (res.isSuccess) {
                    checkInDao.markSupportRequestSynced(r.id)
                }
            }
            if (pending.isNotEmpty() || pendingReqs.isNotEmpty()) {
                _toastEvent.emit("Synced ${pending.size + pendingReqs.size} records to Supabase!")
            }
        }
    }

    // Faculty Actions
    fun sendFacultyIntervention(studentId: String) {
        viewModelScope.launch {
            val list = departmentStudents.value.toMutableList()
            val index = list.indexOfFirst { it.id == studentId }
            if (index != -1) {
                val student = list[index]
                list[index] = student.copy(facultyNotified = true)
                departmentStudents.value = list
                _toastEvent.emit("Supportive consultation invite sent to ${student.name} (${student.email})")
            }
        }
    }
}
