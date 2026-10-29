-- ==============================================================================
-- Campus Care: The Departmental Wellbeing Portal
-- Supabase PostgreSQL Database Schema & Migration
-- Department: Computer Engineering — K. K. Wagh College of Engineering
-- ==============================================================================

-- 1. Ensure columns exist on checkins table
alter table if exists public.checkins add column if not exists steps integer default 0;
alter table if exists public.checkins add column if not exists water_liters numeric default 2.0;
alter table if exists public.checkins add column if not exists department text default 'comp_eng';

-- 2. Department Weekly Goals table (Set by Faculty)
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

-- 3. 6-Digit Sync Tokens table (Single-use, 10-minute expiry)
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

-- 4. Enable Row Level Security (RLS)
alter table public.checkins enable row level security;
alter table public.department_goals enable row level security;
alter table public.sync_tokens enable row level security;

-- 5. RLS Policies for Anon / Authenticated Access
drop policy if exists "Allow read checkins" on public.checkins;
create policy "Allow read checkins" on public.checkins for select using (true);

drop policy if exists "Allow insert checkins" on public.checkins;
create policy "Allow insert checkins" on public.checkins for insert with check (true);

drop policy if exists "Allow update checkins" on public.checkins;
create policy "Allow update checkins" on public.checkins for update using (true);

drop policy if exists "Allow read goals" on public.department_goals;
create policy "Allow read goals" on public.department_goals for select using (true);

drop policy if exists "Allow write goals" on public.department_goals;
create policy "Allow write goals" on public.department_goals for all using (true);

drop policy if exists "Allow read tokens" on public.sync_tokens;
create policy "Allow read tokens" on public.sync_tokens for select using (true);

drop policy if exists "Allow write tokens" on public.sync_tokens;
create policy "Allow write tokens" on public.sync_tokens for all using (true);

-- 6. Insert Initial Faculty Goals if not present
insert into public.department_goals (department, target_sleep, target_steps, target_water, start_date, week_number, created_by)
values ('comp_eng', 7.0, 8000, 2.5, '2026-10-26', 44, 'Dr. V. K. Wagh (HOD)')
on conflict do nothing;
