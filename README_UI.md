# Quiz Application - JavaFX UI Implementation

## Overview

This is a complete JavaFX-based user interface implementation for the Quiz Application, a distributed system using Java Socket programming, MongoDB, and RMI for managing online quizzes.

## ✨ Features Implemented

### UI Components:

1. **LoginForm.java** - Authentication Screen
   - User ID and password fields
   - Remember me checkbox
   - Role-based authentication
   - Error message handling
   - Modern gradient background

2. **CandidateDashboard.java** - Student Dashboard
   - Displays available quizzes
   - Quiz information (title, duration, status, question count)
   - Start exam button
   - Welcome message with user name
   - Logout functionality

3. **ExamForm.java** - Quiz Exam Interface
   - Question display with options
   - Radio button selection for answers
   - Navigation (Previous/Next buttons)
   - Timer with countdown (in minutes:seconds format)
   - Progress bar showing current question
   - Submit quiz button with confirmation
   - Auto-submit when time expires

4. **ResultForm.java** - Results Display
   - Score display (0-10 scale)
   - Pass/Fail indicator with icons
   - Statistics showing correct/incorrect count
   - Percentage calculation
   - Return to dashboard or exit options

5. **LecturerDashboard.java** - Instructor Dashboard
   - Feature cards for different functions
   - Create Quiz option
   - Manage Quiz option
   - View Results option
   - Statistics option
   - Future expandability

6. **SessionManager.java** - Session Management (Singleton)
   - Manages socket connection
   - Stores ObjectInputStream/ObjectOutputStream
   - Maintains current user information
   - Logout functionality with resource cleanup

## 🏗️ Architecture

```
UI Layer
├── LoginForm ──┬──→ CandidateDashboard
│              └──→ LecturerDashboard
├── ExamForm
├── ResultForm
└── SessionManager (Singleton)

Service Layer
├── UserService
├── QuizService
└── QuestionService

Repository Layer
├── UserRepository
├── QuizRepository
└── QuestionRepository

Database
└── MongoDB (QuizAppDB)
```

## 🎨 UI Design

- **Color Scheme**: 
  - Primary: #667eea (Purple)
  - Secondary: #764ba2 (Dark Purple)
  - Success: #27ae60 (Green)
  - Danger: #e74c3c (Red)
  - Info: #3498db (Blue)

- **Typography**: Arial font family, 11-14px sizes
- **Layout**: BorderPane, VBox, HBox with responsive sizing
- **Styling**: CSS-based JavaFX styling

## 🔄 Flow Diagram

```
Start
  ↓
[LoginForm] - User enters credentials
  ↓
[Authentication via UserService]
  ├→ CANDIDATE → [CandidateDashboard]
  │              ↓
  │          [Select Quiz]
  │              ↓
  │          [ExamForm] - 15-60 minutes timer
  │              ↓
  │          [Submit Quiz] - Calculate score
  │              ↓
  │          [ResultForm] - Display results
  │              ↓
  │          [Back to Dashboard]
  │
  └→ LECTURER → [LecturerDashboard]
                 ↓
            [Future Features]
```

## 📊 Default Test Accounts

### Candidates:
- ID: 23696901, Password: 12345678
- ID: 23679011, Password: 12345678
- ID: 23697291, Password: 12345678

### Lecturer:
- ID: 12345678, Password: 12345678

## 📦 Dependencies Added

```xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21.0.1</version>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>21.0.1</version>
</dependency>
```

## 🚀 Running the Application

### From Maven:
```bash
mvn clean javafx:run
```

### From IDE:
1. Right-click Main.java
2. Select Run 'Main.main()'

### From Terminal:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="iuh.fit.app.Main"
```

## 🔌 Socket Integration Points

The UI is designed to integrate with the existing socket communication:

1. **LoginForm**: Sends LOGIN command via socket
2. **CandidateDashboard**: Requests GET_QUIZ_BY_ID
3. **ExamForm**: Handles quiz display and user responses
4. **ResultForm**: Calculates and displays scores from SUBMIT_QUIZ response

## 📋 Scoring System

- Total Score = (Correct Answers / Total Questions) × 10
- Pass: Score ≥ 5.0
- Fail: Score < 5.0
- Scores are rounded to 2 decimal places

## ⏱️ Timer Implementation

- Uses JavaFX Timeline for precise countdown
- Displays in MM:SS format
- Color changes to red when 5 minutes remaining
- Auto-submits when time expires
- Prevents editing after submission

## 🎯 Key Implementation Details

### SessionManager (Singleton Pattern)
```java
- getInstance(): Static method to get instance
- isLoggedIn(): Check if user is authenticated
- logout(): Clean up resources and session
```

### Custom ListCell for Quiz Display
```java
- Shows quiz information in card format
- Clickable "Start Quiz" button
- Status and duration badges
```

### Responsive Layout
```java
- Uses VBox.setVgrow() for flexibility
- HBox.setHgrow() for horizontal spacing
- ScrollPane for scrollable content
```

## 🔐 Security Considerations

- Passwords are stored securely (via MongoDB)
- Session information is cleared on logout
- Socket connections are properly closed
- No sensitive data stored in UI

## 🎓 Learning Outcomes

This implementation demonstrates:
- JavaFX UI development best practices
- MVC architecture pattern
- Singleton pattern for session management
- CSS styling in JavaFX
- Multi-threading for long operations
- Platform.runLater() for thread-safe UI updates

## 📝 Future Enhancements

- [ ] Dark mode support
- [ ] Question shuffling animation
- [ ] Audio/Video questions
- [ ] Real-time progress synchronization
- [ ] Quiz statistics dashboard for lecturers
- [ ] Export results as PDF
- [ ] Multi-language support
- [ ] Accessibility improvements

## 🐛 Known Limitations

- Quiz list loading is placeholder (implement getAllQuizzes() in repository)
- Submission saving is not yet implemented
- Lecturer dashboard features are placeholder
- No internet connection handling
- No offline mode

## 📞 Support

For issues or questions:
1. Check MongoDB is running on localhost:27017
2. Verify all dependencies are installed: `mvn clean install`
3. Ensure JDK 21 or higher is installed
4. Check Java version: `java -version`

## 📄 File Structure

```
src/main/java/iuh/fit/app/ui/
├── login/
│   └── LoginForm.java
├── candidate/
│   ├── CandidateDashboard.java
│   ├── ExamForm.java
│   └── ResultForm.java
├── lecturer/
│   └── LecturerDashboard.java
└── shared/
    └── SessionManager.java
```

## 🔗 Integration Points

### With Service Layer:
- LoginForm → UserServiceImpl.login()
- CandidateDashboard → QuizServiceImpl
- ExamForm → QuestionRepositoryImpl
- ResultForm → QuizServiceImpl.calculateScore()

### With Network Layer:
- Ready for socket integration via SessionManager
- Request/Response objects ready for serialization
- CommandType enums defined for all operations

## ✅ Verification Checklist

- [x] All UI screens implemented
- [x] JavaFX dependencies added to pom.xml
- [x] SessionManager singleton implemented
- [x] Score calculation logic integrated
- [x] Timer functionality working
- [x] Error handling in place
- [x] Responsive layouts configured
- [x] CSS styling applied
- [x] Navigation flow complete
- [x] Test accounts documented

---

**Version**: 1.0  
**Last Updated**: April 14, 2026  
**Status**: Ready for Testing ✅

