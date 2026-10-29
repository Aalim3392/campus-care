# Campus Care — The Departmental Wellbeing Portal

A responsive, mobile-first student and faculty wellbeing portal built for **K. K. Wagh College of Engineering**, upgraded to match the problem statement **“The Departmental Wellbeing Portal.”**

🌐 **Live Demo:** [https://public-kappa-nine-28.vercel.app](https://public-kappa-nine-28.vercel.app)  
📦 **GitHub Repository:** [https://github.com/Aalim3392/campus-care.git](https://github.com/Aalim3392/campus-care.git)

---

## 🏛️ 1. Prototype Scope

- **Department:** **Computer Engineering — K. K. Wagh College** (`comp_eng`).
- Designed cleanly so additional departments (IT, Mechanical, Civil, Electrical) can be onboarded in future phases without architectural changes.
- **Five Sample Demo Accounts** with pre-seeded data ready for 1-click evaluation:
  1. 👨‍🎓 **Aarav Sharma** (`aarav.sharma@kkwagh.edu.in`) — Student 1 (High activity, 8,500+ steps)
  2. 👨‍🎓 **Aalim Sayyed** (`aasayyed370125@kkwagh.edu.in`) — Student 2 (Optimal sleep & hydration)
  3. 👩‍🎓 **Neha Patil** (`neha.patil@kkwagh.edu.in`) — Student 3 (Elevated stress / sleep deficit)
  4. 👨‍🏫 **Dr. V. K. Wagh** (`hod.comp@kkwagh.edu.in`) — Faculty (Head of Department)
  5. 👨‍🏫 **Prof. P. R. Rao** (`mentor.comp@kkwagh.edu.in`) — Faculty (Wellness Coordinator & Mentor)

---

## 🚀 2. Core Upgrades & Problem Statement Deliverables

### A. Role-Based Authentication
- Separate **Student** and **Faculty** entry options.
- User profile contains: `userId`, `role` (`STUDENT` / `FACULTY`), `departmentId` (`comp_eng`), `email`, and `authenticated session`.
- Domain restriction strictly enforced: only official `@kkwagh.edu.in` accounts permitted.
- Fast 1-click demo persona switcher ribbon available during demonstrations.

### B. Student Daily Health Check-in
- Form inputs:
  - **Date** (defaults to today, editable)
  - **Hours of sleep** (with baseline indicators)
  - **Water intake in Litres** (with glasses stepper)
  - **Daily Step Count** (integer input + presets: 5,000 / 8,000 Goal / 10,000 / 12,000)
  - **Mood selection** (Terrible, Low, Okay, Good, Great)
  - **Academic Stress level** (1 to 5)
- **Duplicate Prevention:** Checks whether an entry already exists for the selected user and date. If found, displays a notice and offers intentional update/overwrite.
- Instant recalculation of personal and departmental dashboard metrics upon save.

### C. Six-Digit Sync Token (Prototype Workflow)
- **Generate Token:** Authorized student generates a secure 6-digit cryptographic code with live 10-minute expiry countdown.
- **Verify Token:** Mentor or faculty enters the 6-digit code.
- Backend/client verifies:
  1. Token exists
  2. Token is not expired (10-minute validity)
  3. Token has not already been used (**Single-Use Guarantee**)
- Returns a clear **Verified** or **Rejected** status badge with verified timestamp.
- **Privacy First:** Individual raw private health records are **never exposed** through token verification; only authentication and association are validated.

### D. Faculty Weekly Goal Setting
- Dedicated Faculty Dashboard panel where authorized faculty set or update department targets:
  - **Target Sleep:** e.g., 7.0 hours/day
  - **Target Daily Steps:** e.g., 8,000 steps/day
  - **Target Water Intake:** e.g., 2.5 Litres/day
  - Applicable week number and start date
- Saved persistently and displayed prominently to students as their daily benchmarks.
- **Role Permissions:** Student accounts are strictly read-only and cannot modify faculty goals.

### E. Department Health Index (DHI)
- Defined, transparent mathematical formula normalized against faculty targets:
  $$\text{DHI} = 100 \times \left( 0.40 \times \min\left(\frac{\text{Avg Sleep}}{\text{Target Sleep}}, 1.2\right) + 0.35 \times \min\left(\frac{\text{Avg Steps}}{\text{Target Steps}}, 1.2\right) + 0.25 \times \min\left(\frac{\text{Avg Water}}{\text{Target Water}}, 1.2\right) \right)$$
- **Weights:** Sleep (40%), Daily Steps (35%), Hydration (25%).
- Outlier safeguard: Each component is capped at 120% (1.20) so extreme single entries cannot distort the departmental index.
- Missing records are excluded from denominators (never treated as 0 hours).
- Displays live DHI score gauge (0–100), rating (Optimal, Moderate, Needs Attention), metric breakdowns, and participation counts (participating users & valid records).

### F. Department Sleep Red Alert
- Automatically triggers a prominent red warning banner when the department's average sleep drops below **5.0 hours**.
- Displays the exact measured average sleep and triggers faculty advisories to moderate deadline pressure.
- Automatically disappears when the average recovers to $\ge 5.0$ hours.
- Includes a **"⚡ Test Alert"** button in the demo bar to test the active alert and safe baseline dynamically.

### G. Monthly Performance & Leaderboard
- Displays Computer Engineering's monthly DHI, trend, and target progress.
- Labeled as **"Participating Department"**.
- Transparently notes that institute-wide cross-department competition (Civil, Mechanical, Electrical, IT) will activate when additional departments are onboarded.

### H. Wellbeing & Mobile PWA Features
- Preserves the guided breathing and meditation visualizer (Box 4-4-4-4, 4-7-8, Centering, 2-Min Reset).
- Confidential campus support request form routed to the faculty wellness coordinator.
- Mobile PWA installable on Android and iOS devices.

---

## 🗄️ 3. Database Schema & Migration

Execute the migration in [`supabase-migration.sql`](./supabase-migration.sql) or Supabase SQL Editor:
```sql
-- 1. Ensure columns exist on checkins table
alter table if exists public.checkins add column if not exists steps integer default 0;
alter table if exists public.checkins add column if not exists water_liters numeric default 2.0;
alter table if exists public.checkins add column if not exists department text default 'comp_eng';

-- 2. Department Goals table
create table if not exists public.department_goals (
  id serial primary key,
  department text not null default 'comp_eng',
  target_sleep numeric not null default 7.0,
  target_steps integer not null default 8000,
  target_water numeric not null default 2.5,
  start_date text not null,
  week_number integer not null,
  created_by text,
  created_at timestamptz default now()
);

-- 3. 6-Digit Sync Tokens table
create table if not exists public.sync_tokens (
  token text primary key,
  email text not null,
  role text not null default 'STUDENT',
  department text not null default 'comp_eng',
  created_at timestamptz default now(),
  expires_at timestamptz not null,
  used boolean default false,
  used_by text,
  used_at timestamptz
);
```

---

## 🛠️ 4. Running Locally

```bash
# 1. Install dependencies (Node 18+)
npm run dev

# 2. Or start server directly
node server.js
```
Open [http://localhost:3000](http://localhost:3000) in your browser.
Select any of the 5 demo accounts from the login screen to explore the portal.
