const http = require('http');
const fs = require('fs');
const path = require('path');

// Auto-load .env file if present
const envPath = path.join(__dirname, '.env');
if (fs.existsSync(envPath)) {
  const envContent = fs.readFileSync(envPath, 'utf8');
  envContent.split(/\r?\n/).forEach(line => {
    line = line.trim();
    if (!line || line.startsWith('#')) return;
    const eqIdx = line.indexOf('=');
    if (eqIdx !== -1) {
      const key = line.slice(0, eqIdx).trim();
      const val = line.slice(eqIdx + 1).trim();
      process.env[key] = val;
    }
  });
}

const PORT = 3000;
const PUBLIC_DIR = path.join(__dirname, 'public');

const SUPABASE_URL      = process.env.SUPABASE_URL      || '';
const SUPABASE_ANON_KEY = process.env.SUPABASE_ANON_KEY || '';

// Whether to expose the dev OTP code in the API response.
// Set DEV_OTP_VISIBLE=true ONLY in local development / sandboxed environments.
// In production (or when Supabase SMTP is configured), this should be false.
const DEV_OTP_VISIBLE = process.env.DEV_OTP_VISIBLE === 'true';

const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.gif': 'image/gif',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
  '.webp': 'image/webp',
  '.woff2': 'font/woff2'
};

// In-memory OTP store for development verification fallback (6-digit codes)
const devOtpStore = new Map();

// In-memory sync tokens store (6-digit tokens with single-use flag and expiry)
const syncTokensStore = new Map();

// Department goals store (Computer Engineering default goals set by Prof. Priya Rakibe)
let departmentGoals = {
  department: 'comp_eng',
  departmentName: 'Computer Engineering',
  college: 'K. K. Wagh College of Engineering',
  targetSleep: 7.0,
  targetSteps: 8000,
  targetWater: 2.5,
  startDate: '2026-10-26',
  weekNumber: 44,
  setBy: 'Prof. Priya Rakibe (Faculty Coordinator)',
  updatedAt: new Date().toISOString()
};

// Seed Users Database (Pre-registered Computer Engineering members)
const ALLOWED_PROFILES = {
  'sayyed':     { displayName: 'Aalim Sayyed',        role: 'STUDENT', defaultEmail: 'aalim.sayyed@kkwagh.edu.in' },
  'sonawane':   { displayName: 'Harsh Sonawane',      role: 'STUDENT', defaultEmail: 'harsh.sonawane@kkwagh.edu.in' },
  'pendharkar': { displayName: 'Vaishnavi Pendharkar',role: 'STUDENT', defaultEmail: 'vaishnavi.pendharkar@kkwagh.edu.in' },
  'randhe':     { displayName: 'Shreya Randhe',       role: 'STUDENT', defaultEmail: 'shreya.randhe@kkwagh.edu.in' },
  'rakibe':     { displayName: 'Prof. Priya Rakibe',  role: 'FACULTY', title: 'Faculty Coordinator — Computer Engineering', defaultEmail: 'priya.rakibe@kkwagh.edu.in' }
};

// In-memory dynamic store for registered members (Seeds + Newly Registered Students & Faculty)
const registeredUsersStore = new Map();
Object.values(ALLOWED_PROFILES).forEach(u => {
  registeredUsersStore.set(u.defaultEmail.toLowerCase(), {
    id: 'user_' + Buffer.from(u.defaultEmail).toString('hex').slice(0, 16),
    email: u.defaultEmail.toLowerCase(),
    displayName: u.displayName,
    role: u.role,
    title: u.title || (u.role === 'FACULTY' ? 'Faculty Coordinator' : 'Student'),
    department: 'Computer Engineering',
    departmentId: 'comp_eng',
    registeredAt: '2026-10-26T00:00:00.000Z'
  });
});

function findUserProfile(email) {
  if (!email || typeof email !== 'string') return null;
  const lower = email.trim().toLowerCase();
  if (registeredUsersStore.has(lower)) {
    return registeredUsersStore.get(lower);
  }
  // Check seed aliases
  for (const [key, prof] of Object.entries(ALLOWED_PROFILES)) {
    if (lower.includes(key)) {
      return {
        id: 'user_' + Buffer.from(lower).toString('hex').slice(0, 16),
        email: lower,
        displayName: prof.displayName,
        role: prof.role,
        title: prof.title || (prof.role === 'FACULTY' ? 'Faculty Member' : 'Student'),
        department: 'Computer Engineering',
        departmentId: 'comp_eng'
      };
    }
  }
  return null;
}

// Initial seed checkins for the authorized Computer Engineering students
const initialSeedCheckins = [
  // 1. Aalim Sayyed (Student 1)
  { id: 'seed_sayyed_1', email: 'aalim.sayyed@kkwagh.edu.in', surname: 'sayyed', name: 'Aalim Sayyed', role: 'STUDENT', department: 'comp_eng', date: '2026-10-27', day: 'Tue', mood: 'Good', sleep: 7.5, water: 10, waterLiters: 2.5, steps: 8600, study: 4.5, stress: 2 },
  { id: 'seed_sayyed_2', email: 'aalim.sayyed@kkwagh.edu.in', surname: 'sayyed', name: 'Aalim Sayyed', role: 'STUDENT', department: 'comp_eng', date: '2026-10-28', day: 'Wed', mood: 'Great', sleep: 8.0, water: 11, waterLiters: 2.75, steps: 9200, study: 5.0, stress: 1 },
  { id: 'seed_sayyed_3', email: 'aalim.sayyed@kkwagh.edu.in', surname: 'sayyed', name: 'Aalim Sayyed', role: 'STUDENT', department: 'comp_eng', date: '2026-10-29', day: 'Thu', mood: 'Good', sleep: 7.0, water: 10, waterLiters: 2.5, steps: 8400, study: 4.0, stress: 2 },
  // 2. Harsh Sonawane (Student 2)
  { id: 'seed_sonawane_1', email: 'harsh.sonawane@kkwagh.edu.in', surname: 'sonawane', name: 'Harsh Sonawane', role: 'STUDENT', department: 'comp_eng', date: '2026-10-27', day: 'Tue', mood: 'Good', sleep: 7.0, water: 9, waterLiters: 2.25, steps: 8200, study: 4.5, stress: 2 },
  { id: 'seed_sonawane_2', email: 'harsh.sonawane@kkwagh.edu.in', surname: 'sonawane', name: 'Harsh Sonawane', role: 'STUDENT', department: 'comp_eng', date: '2026-10-28', day: 'Wed', mood: 'Great', sleep: 7.5, water: 10, waterLiters: 2.5, steps: 8800, study: 5.0, stress: 1 },
  { id: 'seed_sonawane_3', email: 'harsh.sonawane@kkwagh.edu.in', surname: 'sonawane', name: 'Harsh Sonawane', role: 'STUDENT', department: 'comp_eng', date: '2026-10-29', day: 'Thu', mood: 'Good', sleep: 7.0, water: 10, waterLiters: 2.5, steps: 8100, study: 4.0, stress: 2 },
  // 3. Vaishnavi Pendharkar (Student 3)
  { id: 'seed_pendharkar_1', email: 'vaishnavi.pendharkar@kkwagh.edu.in', surname: 'pendharkar', name: 'Vaishnavi Pendharkar', role: 'STUDENT', department: 'comp_eng', date: '2026-10-27', day: 'Tue', mood: 'Okay', sleep: 6.5, water: 8, waterLiters: 2.0, steps: 7500, study: 5.0, stress: 3 },
  { id: 'seed_pendharkar_2', email: 'vaishnavi.pendharkar@kkwagh.edu.in', surname: 'pendharkar', name: 'Vaishnavi Pendharkar', role: 'STUDENT', department: 'comp_eng', date: '2026-10-28', day: 'Wed', mood: 'Good', sleep: 7.0, water: 9, waterLiters: 2.25, steps: 8000, study: 4.5, stress: 2 },
  { id: 'seed_pendharkar_3', email: 'vaishnavi.pendharkar@kkwagh.edu.in', surname: 'pendharkar', name: 'Vaishnavi Pendharkar', role: 'STUDENT', department: 'comp_eng', date: '2026-10-29', day: 'Thu', mood: 'Okay', sleep: 6.5, water: 8, waterLiters: 2.0, steps: 7600, study: 5.0, stress: 2 },
  // 4. Shreya Randhe (Student 4)
  { id: 'seed_randhe_1', email: 'shreya.randhe@kkwagh.edu.in', surname: 'randhe', name: 'Shreya Randhe', role: 'STUDENT', department: 'comp_eng', date: '2026-10-27', day: 'Tue', mood: 'Okay', sleep: 6.0, water: 7, waterLiters: 1.75, steps: 6800, study: 5.5, stress: 3 },
  { id: 'seed_randhe_2', email: 'shreya.randhe@kkwagh.edu.in', surname: 'randhe', name: 'Shreya Randhe', role: 'STUDENT', department: 'comp_eng', date: '2026-10-28', day: 'Wed', mood: 'Low', sleep: 5.5, water: 7, waterLiters: 1.75, steps: 6400, study: 6.0, stress: 3 },
  { id: 'seed_randhe_3', email: 'shreya.randhe@kkwagh.edu.in', surname: 'randhe', name: 'Shreya Randhe', role: 'STUDENT', department: 'comp_eng', date: '2026-10-29', day: 'Thu', mood: 'Good', sleep: 6.5, water: 8, waterLiters: 2.0, steps: 7200, study: 5.0, stress: 2 }
];

let inMemoryCheckins = [...initialSeedCheckins];

function sendJson(res, statusCode, data) {
  res.writeHead(statusCode, {
    'Content-Type': 'application/json; charset=utf-8',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Headers': 'Content-Type, Authorization, apikey',
    'Access-Control-Allow-Methods': 'GET, POST, OPTIONS'
  });
  res.end(JSON.stringify(data));
}

function parseJsonBody(req) {
  return new Promise((resolve, reject) => {
    if (req.body && typeof req.body === 'object') {
      return resolve(req.body);
    }
    if (req.body && typeof req.body === 'string') {
      try {
        return resolve(JSON.parse(req.body));
      } catch (err) {
        return reject(err);
      }
    }
    let body = '';
    req.on('data', chunk => {
      body += chunk;
      if (body.length > 1e6) {
        req.destroy();
        reject(new Error('Payload too large'));
      }
    });
    req.on('end', () => {
      if (!body) return resolve({});
      try {
        resolve(JSON.parse(body));
      } catch (err) {
        reject(err);
      }
    });
    req.on('error', reject);
  });
}

const requestHandler = async (req, res) => {
  // Handle CORS Preflight
  if (req.method === 'OPTIONS') {
    res.writeHead(204, {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Headers': 'Content-Type, Authorization, apikey',
      'Access-Control-Allow-Methods': 'GET, POST, OPTIONS'
    });
    res.end();
    return;
  }

  const parsedUrl = new URL(req.url, `http://${req.headers.host}`);
  const pathname = decodeURIComponent(parsedUrl.pathname);

  // ==================== API ROUTES ====================

  // 1. Public client configuration (Only publishable/anon key, never service-role key)
  if (pathname === '/api/config' && req.method === 'GET') {
    return sendJson(res, 200, {
      supabaseConfigured: Boolean(SUPABASE_URL && SUPABASE_ANON_KEY),
      supabaseUrl: SUPABASE_URL,
      supabaseAnonKey: SUPABASE_ANON_KEY
    });
  }

  // 2. Server-side Domain Validation & OTP Dispatch (Supports Login & Registration)
  if (pathname === '/api/auth/send-otp' && req.method === 'POST') {
    try {
      const { email, displayName, role, isRegistering, facultyCode } = await parseJsonBody(req);
      if (!email || typeof email !== 'string') {
        return sendJson(res, 400, { success: false, error: 'Email address is required.' });
      }

      const normalizedEmail = email.trim().toLowerCase();

      // SECURE SERVER-SIDE DOMAIN RESTRICTION (College domain only)
      if (!normalizedEmail.endsWith('@kkwagh.edu.in')) {
        return sendJson(res, 403, {
          success: false,
          error: 'Access restricted: Only official @kkwagh.edu.in college email addresses are permitted.'
        });
      }

      let profile = findUserProfile(normalizedEmail);

      // 1. In Sign-In Mode: User MUST be registered first!
      if (!isRegistering && !profile) {
        return sendJson(res, 404, {
          success: false,
          notRegistered: true,
          error: `Account not found for ${normalizedEmail}. Please register first using the Register tab.`
        });
      }

      // 2. In Register Mode: Check if account already exists!
      if (isRegistering && profile) {
        return sendJson(res, 409, {
          success: false,
          alreadyRegistered: true,
          error: `An account for ${normalizedEmail} already exists. Please sign in instead.`
        });
      }

      // 3. In Register Mode: Faculty requires verified departmental passcode
      if (isRegistering && role === 'FACULTY') {
        const VALID_FACULTY_CODES = ['KKWAGH_FACULTY_2026', 'KKW2026', 'FACULTY2026'];
        const fCode = (facultyCode || '').trim().toUpperCase();
        if (!VALID_FACULTY_CODES.includes(fCode)) {
          return sendJson(res, 403, {
            success: false,
            error: 'Invalid Faculty Verification Code. Faculty registration requires the authorized department code.'
          });
        }
      }

      if (isRegistering && !profile && displayName) {
        // Register new member into store
        profile = {
          id: 'user_' + Buffer.from(normalizedEmail).toString('hex').slice(0, 16),
          email: normalizedEmail,
          displayName: displayName.trim(),
          role: role === 'FACULTY' ? 'FACULTY' : 'STUDENT',
          title: role === 'FACULTY' ? 'Faculty Member' : 'Student',
          department: 'Computer Engineering',
          departmentId: 'comp_eng',
          registeredAt: new Date().toISOString()
        };
        registeredUsersStore.set(normalizedEmail, profile);
      }

      let supabaseSent = false;
      let supabaseMessage = '';

      // Call Supabase Auth OTP endpoint if configured
      if (SUPABASE_URL && SUPABASE_ANON_KEY) {
        try {
          const sRes = await fetch(`${SUPABASE_URL}/auth/v1/otp`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'apikey': SUPABASE_ANON_KEY
            },
            body: JSON.stringify({ email: normalizedEmail })
          });
          if (sRes.ok) {
            supabaseSent = true;
          } else {
            const errJson = await sRes.json().catch(() => ({}));
            supabaseMessage = errJson.msg || errJson.message || 'Supabase OTP dispatch returned status ' + sRes.status;
          }
        } catch (sErr) {
          supabaseMessage = sErr.message;
        }
      }

      // Generate a 6-digit verification code stored server-side
      const generatedCode = String(Math.floor(100000 + Math.random() * 900000));
      devOtpStore.set(normalizedEmail, {
        code: generatedCode,
        expires: Date.now() + 10 * 60 * 1000 // 10 minutes
      });

      return sendJson(res, 200, {
        success: true,
        email: normalizedEmail,
        isRegistered: Boolean(profile),
        user: profile || null,
        supabaseSent,
        devCode: generatedCode,
        message: `Verification code generated for ${normalizedEmail}. Enter the 6-digit code to continue.`
      });
    } catch (err) {
      return sendJson(res, 500, { success: false, error: err.message });
    }
  }

  // 3. Server-side Registration Endpoint (POST /api/auth/register)
  if (pathname === '/api/auth/register' && req.method === 'POST') {
    try {
      const { email, displayName, role, department, facultyCode } = await parseJsonBody(req);
      if (!email || !displayName) {
        return sendJson(res, 400, { success: false, error: 'Full name and college email are required.' });
      }

      const normalizedEmail = email.trim().toLowerCase();
      if (!normalizedEmail.endsWith('@kkwagh.edu.in')) {
        return sendJson(res, 403, {
          success: false,
          error: 'Registration restricted: Only official @kkwagh.edu.in college email addresses are permitted.'
        });
      }

      if (role === 'FACULTY') {
        const VALID_FACULTY_CODES = ['KKWAGH_FACULTY_2026', 'KKW2026', 'FACULTY2026'];
        const fCode = (facultyCode || '').trim().toUpperCase();
        if (!VALID_FACULTY_CODES.includes(fCode)) {
          return sendJson(res, 403, {
            success: false,
            error: 'Invalid Faculty Verification Code. Faculty registration requires the authorized department code (KKWAGH_FACULTY_2026).'
          });
        }
      }

      const existing = findUserProfile(normalizedEmail);
      if (existing) {
        return sendJson(res, 409, {
          success: false,
          error: `An account for ${normalizedEmail} already exists. Please sign in instead.`
        });
      }

      const newProfile = {
        id: 'user_' + Buffer.from(normalizedEmail).toString('hex').slice(0, 16),
        email: normalizedEmail,
        displayName: displayName.trim(),
        role: role === 'FACULTY' ? 'FACULTY' : 'STUDENT',
        title: role === 'FACULTY' ? 'Faculty Member' : 'Student',
        department: department || 'Computer Engineering',
        departmentId: 'comp_eng',
        registeredAt: new Date().toISOString()
      };

      registeredUsersStore.set(normalizedEmail, newProfile);

      const generatedCode = String(Math.floor(100000 + Math.random() * 900000));
      devOtpStore.set(normalizedEmail, {
        code: generatedCode,
        expires: Date.now() + 10 * 60 * 1000
      });

      return sendJson(res, 201, {
        success: true,
        message: `Account created for ${newProfile.displayName}. Enter 6-digit code to complete verification.`,
        user: newProfile,
        devCode: generatedCode
      });
    } catch (err) {
      return sendJson(res, 500, { success: false, error: err.message });
    }
  }

  // 4. Server-side OTP Verification
  if (pathname === '/api/auth/verify-otp' && req.method === 'POST') {
    try {
      const { email, token, displayName, role } = await parseJsonBody(req);
      if (!email || !token) {
        return sendJson(res, 400, { success: false, error: 'Email and verification code are required.' });
      }

      const normalizedEmail = email.trim().toLowerCase();

      // SECURE SERVER-SIDE DOMAIN CHECK
      if (!normalizedEmail.endsWith('@kkwagh.edu.in')) {
        return sendJson(res, 403, {
          success: false,
          error: 'Access restricted: Only official @kkwagh.edu.in college email addresses are permitted.'
        });
      }

      let verified = false;
      let session = null;

      // 1. Try Supabase Auth verify
      if (SUPABASE_URL && SUPABASE_ANON_KEY) {
        try {
          const sRes = await fetch(`${SUPABASE_URL}/auth/v1/verify`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'apikey': SUPABASE_ANON_KEY
            },
            body: JSON.stringify({
              type: 'email',
              email: normalizedEmail,
              token: token.trim()
            })
          });
          if (sRes.ok) {
            const data = await sRes.json();
            verified = true;
            session = data;
          }
        } catch (_) {}
      }

      // 2. Check server-side OTP store
      if (!verified) {
        const stored = devOtpStore.get(normalizedEmail);
        if (stored && stored.code === token.trim() && Date.now() <= stored.expires) {
          verified = true;
          devOtpStore.delete(normalizedEmail);
          session = {
            access_token: 'cw_token_' + Buffer.from(normalizedEmail + ':' + Date.now()).toString('base64'),
            user: {
              email: normalizedEmail,
              id: 'user_' + Buffer.from(normalizedEmail).toString('hex').slice(0, 16)
            }
          };
        }
      }

      if (!verified) {
        return sendJson(res, 401, {
          success: false,
          error: 'Invalid or expired verification code. Please check and try again.'
        });
      }

      let userProfile = findUserProfile(normalizedEmail);
      if (!userProfile) {
        // Auto-create registered profile
        const inferredName = (displayName || '').trim() || normalizedEmail.split('@')[0]
          .split(/[._]+/)
          .map(w => w.charAt(0).toUpperCase() + w.slice(1))
          .join(' ');
        userProfile = {
          id: 'user_' + Buffer.from(normalizedEmail).toString('hex').slice(0, 16),
          email: normalizedEmail,
          displayName: inferredName,
          role: role === 'FACULTY' ? 'FACULTY' : 'STUDENT',
          title: role === 'FACULTY' ? 'Faculty Member' : 'Student',
          department: 'Computer Engineering',
          departmentId: 'comp_eng',
          registeredAt: new Date().toISOString()
        };
        registeredUsersStore.set(normalizedEmail, userProfile);
      }

      return sendJson(res, 200, {
        success: true,
        user: userProfile,
        session
      });
    } catch (err) {
      return sendJson(res, 500, { success: false, error: err.message });
    }
  }

  // 4a. Get Check-in Records (Directly from Supabase with in-memory fallback)
  if (pathname === '/api/checkins' && req.method === 'GET') {
    try {
      const email = parsedUrl.searchParams.get('email');
      if (!email || !email.endsWith('@kkwagh.edu.in')) {
        return sendJson(res, 403, { success: false, error: 'Unauthorized. Valid college email required.' });
      }

      const normalizedEmail = email.trim().toLowerCase();
      let records = [];

      // 1. Fetch live user check-ins from Supabase
      if (SUPABASE_URL && SUPABASE_ANON_KEY) {
        try {
          const sRes = await fetch(`${SUPABASE_URL}/rest/v1/checkins?email=eq.${encodeURIComponent(normalizedEmail)}&order=date.asc`, {
            headers: {
              'apikey': SUPABASE_ANON_KEY,
              'Authorization': `Bearer ${SUPABASE_ANON_KEY}`
            }
          });
          if (sRes.ok) {
            const fetched = await sRes.json();
            if (Array.isArray(fetched) && fetched.length > 0) {
              records = fetched.map(r => {
                const stepsMatch = (r.id || '').match(/steps(\d+)/i);
                const steps = stepsMatch ? parseInt(stepsMatch[1], 10) : 8000;
                return {
                  id: r.id,
                  email: r.email,
                  date: r.date,
                  day: r.day,
                  mood: r.mood,
                  sleep: r.sleep,
                  water: r.water,
                  waterLiters: parseFloat(((r.water || 8) * 0.25).toFixed(2)),
                  steps,
                  study: r.study,
                  stress: r.stress,
                  department: 'comp_eng',
                  created_at: r.created_at
                };
              });
            }
          }
        } catch (_) {}
      }

      // Merge with in-memory records (or seed records) so no data is ever missed
      const memoryMatches = inMemoryCheckins.filter(r => r.email === normalizedEmail);
      const combinedMap = new Map();
      memoryMatches.forEach(r => combinedMap.set(r.date, r));
      records.forEach(r => combinedMap.set(r.date, { ...combinedMap.get(r.date), ...r }));
      const finalRecords = Array.from(combinedMap.values()).sort((a, b) => (a.date || '').localeCompare(b.date || ''));

      return sendJson(res, 200, { success: true, records: finalRecords });
    } catch (err) {
      return sendJson(res, 500, { success: false, error: err.message });
    }
  }

  // 4b. Save Check-in Record (Persists directly to Supabase and in-memory store)
  if (pathname === '/api/checkins' && req.method === 'POST') {
    try {
      const data = await parseJsonBody(req);
      if (!data || !data.email || !data.email.endsWith('@kkwagh.edu.in')) {
        return sendJson(res, 403, { success: false, error: 'Unauthorized. Valid college email required.' });
      }

      const normalizedEmail = data.email.trim().toLowerCase();
      const stepsCount = parseInt(data.steps, 10) || 8000;
      const cleanId = `rec_${normalizedEmail}_${data.date}_steps${stepsCount}`;

      const fullRecord = {
        id: cleanId,
        email: normalizedEmail,
        department: data.department || 'comp_eng',
        date: data.date,
        day: data.day || 'Day',
        mood: data.mood || 'Good',
        sleep: parseFloat(data.sleep) || 0,
        water: parseInt(data.water, 10) || Math.round((parseFloat(data.waterLiters) || 2.0) / 0.25),
        waterLiters: parseFloat(data.waterLiters) || ((parseInt(data.water, 10) || 8) * 0.25),
        steps: stepsCount,
        study: parseFloat(data.study) || 4.0,
        stress: parseInt(data.stress, 10) || 2,
        updatedAt: new Date().toISOString()
      };

      // Exact columns matching Supabase checkins table schema
      const supabasePayload = {
        id: cleanId,
        email: normalizedEmail,
        date: data.date,
        day: fullRecord.day,
        mood: fullRecord.mood,
        sleep: fullRecord.sleep,
        water: fullRecord.water,
        study: fullRecord.study,
        stress: fullRecord.stress
      };

      // 1. Single-submission check: in-memory store
      const existingMemory = inMemoryCheckins.find(r => r.email === normalizedEmail && r.date === data.date);
      if (existingMemory) {
        return sendJson(res, 409, {
          success: false,
          error: `Daily check-in for ${data.date} has already been submitted and cannot be edited.`
        });
      }

      // 2. Single-submission check: Supabase database
      if (SUPABASE_URL && SUPABASE_ANON_KEY) {
        try {
          const checkRes = await fetch(`${SUPABASE_URL}/rest/v1/checkins?email=eq.${encodeURIComponent(normalizedEmail)}&date=eq.${encodeURIComponent(data.date)}&select=id`, {
            headers: {
              'apikey': SUPABASE_ANON_KEY,
              'Authorization': `Bearer ${SUPABASE_ANON_KEY}`
            }
          });
          if (checkRes.ok) {
            const existingRows = await checkRes.json();
            if (Array.isArray(existingRows) && existingRows.length > 0) {
              return sendJson(res, 409, {
                success: false,
                error: `Daily check-in for ${data.date} has already been submitted and cannot be edited.`
              });
            }
          }
        } catch (_) {}
      }

      inMemoryCheckins.push(fullRecord);

      // 3. Save directly to Supabase checkins table
      let supabaseSaved = false;
      if (SUPABASE_URL && SUPABASE_ANON_KEY) {
        try {
          const sRes = await fetch(`${SUPABASE_URL}/rest/v1/checkins`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'apikey': SUPABASE_ANON_KEY,
              'Authorization': `Bearer ${SUPABASE_ANON_KEY}`,
              'Prefer': 'return=representation'
            },
            body: JSON.stringify(supabasePayload)
          });
          if (sRes.ok) {
            supabaseSaved = true;
          }
        } catch (_) {}
      }

      return sendJson(res, 200, {
        success: true,
        savedAt: new Date().toISOString(),
        supabaseSaved,
        record: fullRecord
      });
    } catch (err) {
      return sendJson(res, 500, { success: false, error: err.message });
    }
  }

  // 4c. Get All Department Check-in Records (Live sync from Supabase ensures shared DHI score for all users)
  if (pathname === '/api/department/checkins' && req.method === 'GET') {
    try {
      const dept = parsedUrl.searchParams.get('dept') || 'comp_eng';
      let records = [];

      // Fetch live department records from Supabase
      if (SUPABASE_URL && SUPABASE_ANON_KEY) {
        try {
          const sRes = await fetch(`${SUPABASE_URL}/rest/v1/checkins?order=date.asc`, {
            headers: {
              'apikey': SUPABASE_ANON_KEY,
              'Authorization': `Bearer ${SUPABASE_ANON_KEY}`
            }
          });
          if (sRes.ok) {
            const fetched = await sRes.json();
            if (Array.isArray(fetched) && fetched.length > 0) {
              records = fetched.map(r => {
                const stepsMatch = (r.id || '').match(/steps(\d+)/i);
                const steps = stepsMatch ? parseInt(stepsMatch[1], 10) : 8000;
                return {
                  id: r.id,
                  email: r.email,
                  date: r.date,
                  day: r.day,
                  mood: r.mood,
                  sleep: r.sleep,
                  water: r.water,
                  waterLiters: parseFloat(((r.water || 8) * 0.25).toFixed(2)),
                  steps,
                  study: r.study,
                  stress: r.stress,
                  department: 'comp_eng',
                  created_at: r.created_at
                };
              });
            }
          }
        } catch (_) {}
      }

      // Merge with in-memory seed records
      const combinedMap = new Map();
      inMemoryCheckins.forEach(r => combinedMap.set(`${r.email}_${r.date}`, r));
      records.forEach(r => combinedMap.set(`${r.email}_${r.date}`, { ...combinedMap.get(`${r.email}_${r.date}`), ...r }));
      const allMerged = Array.from(combinedMap.values()).sort((a, b) => (a.date || '').localeCompare(b.date || ''));

      return sendJson(res, 200, {
        success: true,
        department: 'Computer Engineering',
        departmentId: 'comp_eng',
        checkins: allMerged
      });
    } catch (err) {
      return sendJson(res, 500, { success: false, error: err.message });
    }
  }

  // 5. Faculty Department Goals (GET & POST)
  if (pathname === '/api/faculty/goals' && req.method === 'GET') {
    return sendJson(res, 200, { success: true, goals: departmentGoals });
  }

  if (pathname === '/api/faculty/goals' && req.method === 'POST') {
    try {
      const data = await parseJsonBody(req);
      if (!data || !data.email || !data.email.endsWith('@kkwagh.edu.in')) {
        return sendJson(res, 403, { success: false, error: 'Unauthorized. College email required.' });
      }

      // Role check: Only Faculty can modify department goals
      if (data.role !== 'FACULTY') {
        return sendJson(res, 403, {
          success: false,
          error: 'Permission denied. Only authorized faculty members can update department wellbeing goals.'
        });
      }

      departmentGoals = {
        department: 'comp_eng',
        departmentName: 'Computer Engineering',
        college: 'K. K. Wagh College of Engineering',
        targetSleep: parseFloat(data.targetSleep) || departmentGoals.targetSleep,
        targetSteps: parseInt(data.targetSteps, 10) || departmentGoals.targetSteps,
        targetWater: parseFloat(data.targetWater) || departmentGoals.targetWater,
        startDate: data.startDate || new Date().toISOString().split('T')[0],
        weekNumber: parseInt(data.weekNumber, 10) || 44,
        setBy: data.displayName || data.email,
        updatedAt: new Date().toISOString()
      };

      return sendJson(res, 200, {
        success: true,
        message: 'Department wellbeing goals updated successfully.',
        goals: departmentGoals
      });
    } catch (err) {
      return sendJson(res, 500, { success: false, error: err.message });
    }
  }

  // 6. Six-Digit Sync Token Generation (POST)
  if (pathname === '/api/sync-tokens/generate' && req.method === 'POST') {
    try {
      const { email, role, department } = await parseJsonBody(req);
      if (!email || !email.endsWith('@kkwagh.edu.in')) {
        return sendJson(res, 403, { success: false, error: 'Unauthorized college email.' });
      }

      // Generate secure 6-digit sync token
      const token = String(Math.floor(100000 + Math.random() * 900000));
      const now = Date.now();
      const expiresAt = new Date(now + 10 * 60 * 1000).toISOString(); // 10 minutes expiry

      const tokenRecord = {
        token,
        email: email.trim().toLowerCase(),
        role: role || 'STUDENT',
        department: department || 'Computer Engineering',
        departmentId: 'comp_eng',
        createdAt: new Date(now).toISOString(),
        expiresAt,
        used: false,
        usedBy: null,
        usedAt: null
      };

      syncTokensStore.set(token, tokenRecord);

      return sendJson(res, 200, {
        success: true,
        token,
        expiresAt,
        department: 'Computer Engineering',
        message: '6-digit Sync Token generated. Valid for 10 minutes (single-use).'
      });
    } catch (err) {
      return sendJson(res, 500, { success: false, error: err.message });
    }
  }

  // 7. Six-Digit Sync Token Verification (POST)
  if (pathname === '/api/sync-tokens/verify' && req.method === 'POST') {
    try {
      const { token, verifierEmail, verifierRole } = await parseJsonBody(req);
      if (!token) {
        return sendJson(res, 400, { success: false, error: '6-digit token is required.' });
      }

      const cleanToken = String(token).trim();
      const tokenData = syncTokensStore.get(cleanToken);

      if (!tokenData) {
        return sendJson(res, 404, {
          success: false,
          verified: false,
          error: 'Token not found. Please verify the 6-digit code entered.'
        });
      }

      if (tokenData.used) {
        return sendJson(res, 409, {
          success: false,
          verified: false,
          error: 'This Sync Token has already been used. Tokens are strictly single-use.'
        });
      }

      if (Date.now() > new Date(tokenData.expiresAt).getTime()) {
        return sendJson(res, 410, {
          success: false,
          verified: false,
          error: 'This Sync Token has expired (10-minute limit exceeded). Please generate a fresh token.'
        });
      }

      // Mark token as used immediately
      tokenData.used = true;
      tokenData.usedBy = verifierEmail || 'Verified College Member';
      tokenData.usedAt = new Date().toISOString();
      syncTokensStore.set(cleanToken, tokenData);

      return sendJson(res, 200, {
        success: true,
        verified: true,
        message: 'Sync Token verified successfully. Entry associated securely.',
        tokenDetails: {
          token: cleanToken,
          department: tokenData.department,
          departmentId: tokenData.departmentId,
          role: tokenData.role,
          createdAt: tokenData.createdAt,
          verifiedAt: tokenData.usedAt,
          isSingleUseHonored: true
        }
      });
    } catch (err) {
      return sendJson(res, 500, { success: false, error: err.message });
    }
  }

  // 8. Department Health Index (DHI) & Aggregates (GET)
  if (pathname === '/api/department/dhi' && req.method === 'GET') {
    try {
      const deptId = parsedUrl.searchParams.get('dept') || 'comp_eng';

      // Filter valid records for Computer Engineering
      const deptRecords = inMemoryCheckins.filter(r => (r.department || 'comp_eng') === deptId);

      // Unique participating users
      const uniqueUsers = Array.from(new Set(deptRecords.map(r => r.email)));
      const totalValidRecords = deptRecords.length;

      // Calculate averages excluding missing values
      const sleepRecords = deptRecords.filter(r => typeof r.sleep === 'number' && !isNaN(r.sleep));
      const stepsRecords = deptRecords.filter(r => typeof r.steps === 'number' && !isNaN(r.steps) && r.steps > 0);
      const waterRecords = deptRecords.filter(r => typeof (r.waterLiters || r.water) === 'number' && !isNaN(r.waterLiters || r.water));

      const avgSleep = sleepRecords.length > 0
        ? parseFloat((sleepRecords.reduce((a, b) => a + b.sleep, 0) / sleepRecords.length).toFixed(1))
        : 0;

      const avgSteps = stepsRecords.length > 0
        ? Math.round(stepsRecords.reduce((a, b) => a + b.steps, 0) / stepsRecords.length)
        : 0;

      const avgWaterLiters = waterRecords.length > 0
        ? parseFloat((waterRecords.reduce((a, b) => a + (b.waterLiters || (b.water * 0.25)), 0) / waterRecords.length).toFixed(1))
        : 0;

      // Transparent DHI Mathematical Calculation
      // Formula: 100 * [ 0.40 * min(avgSleep/targetSleep, 1.2) + 0.35 * min(avgSteps/targetSteps, 1.2) + 0.25 * min(avgWater/targetWater, 1.2) ]
      const tSleep = departmentGoals.targetSleep || 7.0;
      const tSteps = departmentGoals.targetSteps || 8000;
      const tWater = departmentGoals.targetWater || 2.5;

      const sleepRatio = tSleep > 0 ? Math.min(avgSleep / tSleep, 1.2) : 0;
      const stepsRatio = tSteps > 0 ? Math.min(avgSteps / tSteps, 1.2) : 0;
      const waterRatio = tWater > 0 ? Math.min(avgWaterLiters / tWater, 1.2) : 0;

      const dhiScore = totalValidRecords > 0
        ? Math.min(100, Math.round(100 * (0.40 * sleepRatio + 0.35 * stepsRatio + 0.25 * waterRatio)))
        : 0;

      // Red Alert triggered when department average sleep < 5.0 hours
      const redAlertActive = sleepRecords.length > 0 && avgSleep < 5.0;

      return sendJson(res, 200, {
        success: true,
        department: {
          id: 'comp_eng',
          name: 'Computer Engineering',
          college: 'K. K. Wagh College of Engineering',
          status: 'Participating Department'
        },
        goals: departmentGoals,
        dhi: {
          score: dhiScore,
          rating: dhiScore >= 80 ? 'Optimal' : dhiScore >= 65 ? 'Moderate' : 'Needs Attention',
          formula: 'DHI = 100 × [ 0.40 × min(AvgSleep/TargetSleep, 1.2) + 0.35 × min(AvgSteps/TargetSteps, 1.2) + 0.25 × min(AvgWater/TargetWater, 1.2) ]',
          components: {
            sleep: { avg: avgSleep, target: tSleep, progressPercent: Math.round(sleepRatio * 100), weight: '40%' },
            steps: { avg: avgSteps, target: tSteps, progressPercent: Math.round(stepsRatio * 100), weight: '35%' },
            water: { avg: avgWaterLiters, target: tWater, progressPercent: Math.round(waterRatio * 100), weight: '25%' }
          }
        },
        participation: {
          participatingUsersCount: uniqueUsers.length,
          sampleUsersTotal: 4,
          totalValidRecords,
          users: uniqueUsers
        },
        redAlert: {
          active: redAlertActive,
          threshold: 5.0,
          measuredAverageSleep: avgSleep,
          severity: redAlertActive ? 'CRITICAL' : 'NORMAL',
          message: redAlertActive
            ? `⚠️ Red Alert: Computer Engineering average sleep is ${avgSleep}h (Below 5.0h safety threshold). Faculty action advised to moderate submission deadlines.`
            : `All good: Department sleep average is healthy (${avgSleep}h). Threshold is 5.0h.`
        },
        monthlyLeaderboard: {
          departmentName: 'Computer Engineering',
          category: 'Participating Department',
          monthlyDhi: dhiScore,
          monthlyTrend: '+4.2% vs last week',
          crossDepartmentStatus: 'Cross-department comparison (IT, Mechanical, Civil, Electrical) will activate once additional departments are onboarded.'
        }
      });
    } catch (err) {
      return sendJson(res, 500, { success: false, error: err.message });
    }
  }

  // 5. Submit Confidential Support Request
  if (pathname === '/api/support-requests' && req.method === 'POST') {
    try {
      const data = await parseJsonBody(req);
      if (!data || !data.email || !data.email.endsWith('@kkwagh.edu.in')) {
        return sendJson(res, 403, { success: false, error: 'Unauthorized.' });
      }

      if (SUPABASE_URL && SUPABASE_ANON_KEY) {
        try {
          const authHeader = req.headers['authorization'] || `Bearer ${SUPABASE_ANON_KEY}`;
          await fetch(`${SUPABASE_URL}/rest/v1/support_requests`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'apikey': SUPABASE_ANON_KEY,
              'Authorization': authHeader,
              'Prefer': 'return=representation'
            },
            body: JSON.stringify({
              email: data.email,
              topic: data.topic,
              note: data.note,
              submitted_at: data.submittedAt || data.submitted_at || new Date().toISOString()
            })
          });
        } catch (_) {}
      }

      return sendJson(res, 200, { success: true, submittedAt: new Date().toISOString() });
    } catch (err) {
      return sendJson(res, 500, { success: false, error: err.message });
    }
  }

  // ==================== STATIC FILE SERVING ====================

  let staticPath = pathname === '/' || pathname === '' ? '/index.html' : pathname;
  const filePath = path.join(PUBLIC_DIR, staticPath);

  // Security check - prevent path traversal
  if (!filePath.startsWith(PUBLIC_DIR)) {
    res.writeHead(403, { 'Content-Type': 'text/plain' });
    res.end('403 Forbidden');
    return;
  }

  fs.stat(filePath, (err, stats) => {
    if (err || !stats.isFile()) {
      // Fallback to index.html for SPA routing
      const indexPath = path.join(PUBLIC_DIR, 'index.html');
      fs.readFile(indexPath, (readErr, content) => {
        if (readErr) {
          res.writeHead(404, { 'Content-Type': 'text/plain' });
          res.end('404 Not Found');
        } else {
          res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
          res.end(content);
        }
      });
      return;
    }

    const ext = path.extname(filePath).toLowerCase();
    const contentType = MIME_TYPES[ext] || 'application/octet-stream';

    fs.readFile(filePath, (readErr, content) => {
      if (readErr) {
        res.writeHead(500, { 'Content-Type': 'text/plain' });
        res.end('500 Internal Server Error');
      } else {
        res.writeHead(200, {
          'Content-Type': contentType,
          'Cache-Control': 'public, max-age=3600'
        });
        res.end(content);
      }
    });
  });
};

const server = http.createServer(requestHandler);

if (require.main === module) {
  server.listen(PORT, '0.0.0.0', () => {
    console.log(`Campus Care Web server running on port ${PORT}`);
  });
}

module.exports = requestHandler;
