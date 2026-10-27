# Campus Care — K.K. Wagh Student Wellbeing

A modern, responsive, mobile-first student wellbeing web app and Progressive Web App (PWA) built for **K.K. Wagh Institute of Engineering Education and Research**.

🌐 **Live Demo:** [https://public-kappa-nine-28.vercel.app](https://public-kappa-nine-28.vercel.app)

---

## 📱 Features

- **Mobile-First Responsive Design**: Optimized specifically for mobile viewports (360–430px) with clean navigation and single-hand usability.
- **Installable PWA**: Can be installed to home screens on Android and iOS with native app feel, offline caching, and standalone display.
- **College Email Authentication**: Restricted to verified `@kkwagh.edu.in` student and staff accounts.
- **Daily Health Check-in**:
  - Mood selection (Terrible, Low, Okay, Good, Great)
  - Sleep duration tracker
  - Hydration stepper (glasses / liters)
  - Daily study hours logging
  - Academic tension / stress level rating (1 to 5)
- **Weekly Progress & Charts**:
  - 7-day averages for Sleep, Water, and Study
  - Daily dual-bar comparison chart (Sleep vs Study)
  - Chronological check-in history logs
- **Guided Breathing & Meditation**:
  - Interactive animated breathing visualizer
  - 4 customizable sessions: Box Breathing (4-4-4-4), 4-7-8 Relaxing Breath, Centering & Focus, and 2-Minute Reset
  - Real-time Phase transitions (Inhale, Hold, Exhale) and timer controls
- **Campus Support & Resource Directory**:
  - Live instant search across all campus mental health resources and meditation practices
  - Confidential Support Request Form routed to student wellness coordinators
- **Cloud Database (Supabase)**:
  - Persistent real-time PostgreSQL database with Row Level Security (RLS) policies.

---

## 🛠️ Tech Stack

- **Frontend**: HTML5, Tailwind CSS, Vanilla JavaScript, Material Symbols
- **PWA**: Web App Manifest (`manifest.json`), Service Worker (`sw.js`)
- **Backend / Database**: Supabase (PostgreSQL, Row Level Security, Auth REST API)
- **Local Server**: Node.js HTTP server (`server.js`)
- **Hosting**: Vercel

---

## 🚀 Running Locally

### Prerequisites
- [Node.js](https://nodejs.org/) (v18+)

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/<YOUR-USERNAME>/campuscare.git
   cd campuscare
   ```
2. Set up environment variables:
   Copy `.env.example` to `.env` and fill in your Supabase credentials:
   ```env
   SUPABASE_URL=https://your-project.supabase.co
   SUPABASE_ANON_KEY=your_anon_key
   ```
3. Run the application:
   ```bash
   node server.js
   ```
4. Open [http://localhost:3000](http://localhost:3000) in your browser. (Press `F12` and toggle Device Toolbar for mobile view).

---

## 📄 License
Created for academic and campus wellbeing demonstration at K.K. Wagh Institute.
