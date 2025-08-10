App Overview: Developed an Expense Tracking app that enables users to manage and record their daily expenses efficiently. Users can also view and analyze expenses categorized by different types such as Staff, Travel, Food, and Utility. The app is built using Jetpack Compose, LiveData, Room database, and follows the MVVM architecture for a clean and responsive user experience.
AI Usage Summary: I used ChatGPT and Cursor extensively throughout the development process. They helped generate UI layout ideas, especially for the category section and theme selection. AI tools assisted in creating data model classes, implementing Hilt for dependency injection, understanding project requirements clearly, generate the queries for room database and optimizing the code for better performance and readability.
Prompt Logs :
1. Suggest a clean and user-friendly Compose UI layout for an Expense Tracker app with inputs for title, amount, category selection chips, notes, and image upload section.
2. Give me ideas to improve the design and to make it use friendly  the ExpenseEntryScreen in Jetpack Compose
3. Suggest a clean and user-friendly Compose UI layout for EntryListScreen to show toggle for catogey and time an also please explain how we need to implement these category and time toggle section
4. what theme colour can we use for expense tracker app
5. Help me structure my Expense Tracker app using MVVM in Kotlin with Hilt for dependency injection and Room for local database.
6. Generate a ViewModel class that handles input validation for amount that should be greater than 0 and title fiend not to be empty and data insertion for expenses, using MutableState and coroutine for database operations.
7. Analyze the following Compose screen for UX improvements in error handling, input feedback, and button states.
8. Suggest how to show error messages inline for invalid inputs in Compose TextFields.
9. How can I improve my prompts to get more accurate Kotlin code from AI for Android app development?
10. Rewrite this prompt to get a ViewModel with proper coroutine usage for database insertions and error handling
screenshots --
11.
![Screenshot 2025-08-10 092921.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20092921.png)
![Screenshot 2025-08-10 085914.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20085914.png)
![Screenshot 2025-08-10 085922.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20085922.png)
![Screenshot 2025-08-10 085944.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20085944.png)
![Screenshot 2025-08-10 085954.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20085954.png)
![Screenshot 2025-08-10 090011.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20090011.png)
![Screenshot 2025-08-10 090107.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20090107.png)
![Screenshot 2025-08-10 090207.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20090207.png)
![Screenshot 2025-08-10 090231.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20090231.png)
![Screenshot 2025-08-10 091250.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20091250.png)
![Screenshot 2025-08-10 091415.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20091415.png)
![Screenshot 2025-08-10 091633.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20091633.png)
![Screenshot 2025-08-10 092152.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20092152.png)
![Screenshot 2025-08-10 092305.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20092305.png)
![Screenshot 2025-08-10 092439.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20092439.png)
![Screenshot 2025-08-10 092636.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20092636.png)
![Screenshot 2025-08-10 092726.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20092726.png)
![Screenshot 2025-08-10 092800.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20092800.png)
![Screenshot 2025-08-10 092813.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20092813.png)
![Screenshot 2025-08-10 092822.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%202025-08-10%20092822.png)

CheckList of features implemented--

1. Expense Entry Screen
   Input fields:
   Title (text)
   Amount (₹)
   Category (mocked list: Staff, Travel, Food, Utility)
   Optional Notes (max 100 chars)
   Optional Receipt Image (upload or mock)
   Jetpack Compose (preferred) or XML-based
   Submit Button: Adds expense, shows Toast, animates entry
   Show real-time “Total Spent Today” at top
2.  Expense List Screen
    View expenses for:
    Today (default)
    Previous dates via calendar or filter
    Group by category or time (toggle)
    Show: Total count, total amount, empty state
3. . Expense Report Screen
   Mock report for last 7 days:
   Daily totals
   Bar or line chart (mocked)
4. State Management & Data Layer

   ViewModel + StateFlow (or LiveData)
   Room 
   Handle screen transitions via Navigation
5.Persist data locally (Room)
   Animation on add
   Duplicate detection
   Validation (amount > 0, title non-empty)
   Reusable UI components


apk link -- https://docs.google.com/uc?export=download&id=1pUxgLlBJzACf6-I2nWEQz3y7i5Nsu4hE

    
