# 🏨 Hotel Room Booking Console App

A polished, animated, menu-driven **Java console application** that simulates a real hotel's room booking system — search rooms, create bookings, check guests in/out, and generate professional bills, all from the terminal.

> **Developer:** Sasank
> **Tech Stack:** Core Java 17+, OOP, Collections, File Handling, java.time, BigDecimal
> **Status:** ✅ Complete, tested, and ready to run

---

## ✨ Highlights

- 🙏 Elegant ASCII-art welcome banner on startup
- 🏨 Premium animated title screen with developer credit
- ⏳ Startup loading bar + spinner animations
- 📅 Live current date & time display
- 🎨 Full ANSI color theming (green = success, red = error, yellow = warning, cyan = info)
- 📋 Beautiful box-drawn tables for room lists, search results, and booking history
- 🧾 Professional, itemized receipts/bills with tax breakdown
- 💾 Bookings persist to disk between runs (`data/bookings.txt`)
- 🧾 Every booking also saves a plain-text receipt to `outputs/`
- ✅ Overlapping-date protection so a room can never be double-booked
- 🛡️ Full input validation (dates, phone, email, capacity, numeric ranges)

---

## 1️⃣ Project Overview

### Simple Explanation
This app works like the front desk of a small hotel. You can look at rooms, book one for a guest, check them in when they arrive, check them out when they leave (which prints the final bill), or cancel a booking — all through a simple numbered menu.

### Technical Explanation
It's a layered Java console application built with clean **separation of concerns**:

```
User Input → Main (UI/Menu) → HotelService (business logic) → FileManager (persistence)
                                        ↓
                              Room / Guest / Booking (domain model)
```

### Workflow
```
Main Menu
   │
   ├─► Search Available Rooms
   │
   ├─► Select Room ─► Enter Guest Details ─► Create Booking
   │
   ├─► Check-in ─► Check-out ─► Calculate & Print Bill
   │
   └─► Cancel Booking / View History
```

---

## 2️⃣ Industry Relevance

Room inventory, availability checks, guest management, and billing are the backbone of every property-management system (PMS) used by hotels, resorts, hostels, and travel booking platforms. This project mirrors that exact workflow, making it a strong, explainable proof-of-work for **Java Developer, Backend Developer, and Software Engineer** roles — even in an AI-driven job market, reliable backend logic like this is still what powers real booking platforms behind the scenes.

---

## 3️⃣ Java Concepts Demonstrated

| Concept | Where it's used |
|---|---|
| Classes & Objects | `Room`, `Guest`, `Booking`, `HotelService` |
| Encapsulation | Private fields with getters/setters across all model classes |
| Enums | `RoomType`, `RoomStatus`, `BookingStatus` |
| Collections (`Map`, `List`) | Room inventory & booking storage in `HotelService` |
| Exception Handling | `IllegalArgumentException`, `IllegalStateException`, `NoSuchElementException` for business rule violations |
| File Handling | `FileManager` reads/writes `data/bookings.txt` and receipt files |
| Date/Time API | `LocalDate`, `LocalDateTime`, `DateTimeFormatter` for stays and timestamps |
| BigDecimal | Precise money math (no floating-point rounding errors) |
| Input Validation | `InputReader` validates numbers, dates, phone, email before accepting them |
| Switch Expressions | Java 17 `switch ->` syntax in the menu router |

---

## 4️⃣ Folder Structure

```
Hotel-Room-Booking-Console-App/
│
├── src/
│   ├── model/          → Room, Guest, Booking, RoomType, RoomStatus, BookingStatus
│   ├── service/         → HotelService (business logic, booking rules, billing)
│   ├── repository/      → FileManager (save/load bookings, write receipts)
│   ├── utility/         → ConsoleUI, ConsoleColors, InputReader (all presentation logic)
│   └── main/             → Main.java (entry point & menu loop)
│
├── data/                 → bookings.txt (persisted booking records, auto-created)
├── outputs/              → auto-generated text receipts per booking
├── screenshots/          → place your submission screenshots here
├── docs/                 → interview prep, testing strategy, GitHub & proof plan
├── README.md
├── .gitignore
└── pom.xml               → optional Maven descriptor (plain javac also works)
```

---

## 5️⃣ Features

**Mandatory**
- Main menu, list rooms, search by type & dates, book a room, enter guest details, calculate bill, generate booking ID, view booking, cancel booking, exit safely

**Recommended (implemented)**
- 4 room types (Single, Double, Deluxe, Suite) with distinct pricing
- Full booking history with status tracking
- Date validation & overlap prevention
- Max guest capacity per room type enforced
- Room status auto-updates (Vacant ↔ Occupied)
- File-based persistence across runs

**Optional / Future scope** — see [Future Improvements](#8-limitations--future-improvements)

---

## 6️⃣ How to Run

### Option A — Command Line (javac/java)
```bash
cd Hotel-Room-Booking-Console-App
javac -d bin -encoding UTF-8 src/model/*.java src/utility/*.java src/repository/*.java src/service/*.java src/main/*.java
java -cp bin main.Main
```

### Option B — IntelliJ IDEA
1. Open the `Hotel-Room-Booking-Console-App` folder as a project
2. Mark `src` as **Sources Root** (right-click → Mark Directory as)
3. Run `main/Main.java`

### Option C — Eclipse
1. File → New → Java Project → uncheck "use default location", point to this folder
2. Ensure `src` is on the build path
3. Run `Main.java`

> ⚠️ Run the app from the project's **root folder** so the relative `data/` and `outputs/` paths resolve correctly.

---

## 7️⃣ Sample Menu

```
╔══════════════════════════════════════════════════════════════════════╗
║                              MAIN MENU                                ║
╚══════════════════════════════════════════════════════════════════════╝
 1. List all rooms
 2. Search available rooms
 3. Book a room
 4. View booking details
 5. Check-in a guest
 6. Check-out and print bill
 7. Cancel a booking
 8. Booking history
 9. Exit
```

### Sample Bill Output
```
╔══════════════════════════════════════════════════════════════════════╗
║                          SASANK GRAND HOTEL                            ║
║                       Official Booking Receipt                        ║
╟──────────────────────────────────────────────────────────────────────╢
║ Booking ID                                                    BK1001 ║
║ Guest Name                                              Rahul Sharma ║
║ Room Number                                                      101 ║
║ Nights                                                              2 ║
╟──────────────────────────────────────────────────────────────────────╢
║ Nightly Rate                                                ₹1500.00 ║
║ Room Charge                                                 ₹3000.00 ║
║ Tax (10%)                                                    ₹300.00 ║
╟──────────────────────────────────────────────────────────────────────╢
║ TOTAL AMOUNT                                                ₹3300.00 ║
╚══════════════════════════════════════════════════════════════════════╝
```

---

## 8️⃣ Limitations & Future Improvements

**Current limitations**
- In-memory + flat-file storage (no database)
- Single-user, single-terminal session (no concurrent access)
- No authentication (no admin/customer login)

**Future improvements**
- JDBC + MySQL integration for real persistence
- Admin and customer login roles
- Payment gateway simulation with discount codes
- GUI version using JavaFX or Swing
- Email booking confirmations
- REST API + web front-end

---

## 9️⃣ Learning Outcomes

Building this project reinforced: layered application design, defensive input validation, precise monetary calculations with `BigDecimal`, date-range overlap algorithms, file-based persistence, and building a genuinely pleasant console UX with ANSI colors and animation — skills directly transferable to backend and full-stack development roles.

---

## 🔟 Author

**Sasank**
B.Tech Student, Raghu Engineering College
Built as a hands-on Java OOP project — proof of work for GitHub and placement preparation.

---

## 📎 More Documentation

See the [`docs/`](./docs) folder for:
- [`INTERVIEW_QA.md`](./docs/INTERVIEW_QA.md) — 10 predicted interview questions & strong answers
- [`TESTING.md`](./docs/TESTING.md) — manual test cases & expected results
- [`GITHUB_GUIDE.md`](./docs/GITHUB_GUIDE.md) — repo naming, commit strategy, `.gitignore` tips
- [`PROOF_PLAN.md`](./docs/PROOF_PLAN.md) — day-wise commit & screenshot plan
