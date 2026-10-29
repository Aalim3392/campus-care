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

// Department goals store (Computer Engineering default goals)
let departmentGoals = {
  department: 'comp_eng',
  departmentName: 'Computer Engineering',
  college: 'K. K. Wagh College of Engineering',
  targetSleep: 7.0,
  targetSteps: 8000,
  targetWater: 2.5,
  startDate: '2026-10-26',
  weekNumber: 44,
  setBy: 'Dr. V. K. Wagh (HOD)',
  updatedAt: new Date().toISOString()
};

// Demo seed checkins for the Computer Engineering prototype
const initialSeedCheckins = [
  // 1. Aarav Sharma (Student 1)
  { id: 'seed_101', email: 'aarav.sharma@kkwagh.edu.in', name: 'Aarav Sharma', role: 'STUDENT', department: 'comp_eng', date: '2026-10-27', day: 'Tue', mood: 'Good', sleep: 7.5, water: 10, waterLiters: 2.5, steps: 8500, study: 4.5, stress: 2 },
  { id: 'seed_102', email: 'aarav.sharma@kkwagh.edu.in', name: 'Aarav Sharma', role: 'STUDENT', department: 'comp_eng', date: '2026-10-28', day: 'Wed', mood: 'Great', sleep: 8.0, water: 11, waterLiters: 2.75, steps: 9100, study: 5.0, stress: 1 },
  { id: 'seed_103', email: 'aarav.sharma@kkwagh.edu.in', name: 'Aarav Sharma', role: 'STUDENT', department: 'comp_eng', date: '2026-10-29', day: 'Thu', mood: 'Good', sleep: 7.0, water: 10, waterLiters: 2.5, steps: 8400, study: 4.0, stress: 2 },
  // 2. Aalim Sayyed (Student 2)
  { id: 'seed_201', email: 'aasayyed370125@kkwagh.edu.in', name: 'Aalim Sayyed', role: 'STUDENT', department: 'comp_eng', date: '2026-10-27', day: 'Tue', mood: 'Good', sleep: 6.5, water: 10, waterLiters: 2.5, steps: 7800, study: 5.0, stress: 2 },
  { id: 'seed_202', email: 'aasayyed370125@kkwagh.edu.in', name: 'Aalim Sayyed', role: 'STUDENT', department: 'comp_eng', date: '2026-10-28', day: 'Wed', mood: 'Great', sleep: 7.0, water: 12, waterLiters: 3.0, steps: 10200, study: 4.5, stress: 1 },
  { id: 'seed_203', email: 'aasayyed370125@kkwagh.edu.in', name: 'Aalim Sayyed', role: 'STUDENT', department: 'comp_eng', date: '2026-10-29', day: 'Thu', mood: 'Good', sleep: 6.5, water: 11, waterLiters: 2.75, steps: 8900, study: 5.5, stress: 2 },
  // 3. Neha Patil (Student 3)
  { id: 'seed_301', email: 'neha.patil@kkwagh.edu.in', name: 'Neha Patil', role: 'STUDENT', department: 'comp_eng', date: '2026-10-27', day: 'Tue', mood: 'Okay', sleep: 5.5, water: 7, waterLiters: 1.75, steps: 6200, study: 6.0, stress: 3 },
  { id: 'seed_302', email: 'neha.patil@kkwagh.edu.in', name: 'Neha Patil', role: 'STUDENT', department: 'comp_eng', date: '2026-10-28', day: 'Wed', mood: 'Low', sleep: 4.5, water: 6, waterLiters: 1.5, steps: 5400, study: 6.5, stress: 4 },
  { id: 'seed_303', email: 'neha.patil@kkwagh.edu.in', name: 'Neha Patil', role: 'STUDENT', department: 'comp_eng', date: '2026-10-29', day: 'Thu', mood: 'Okay', sleep: 5.0, water: 8, waterLiters: 2.0, steps: 6600, study: 5.0, stress: 3 }
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

const server = http.createServer(async (req, res) => {
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

  // 2. Server-side Domain Validation & Supabase OTP Dispatch
  if (pathname === '/api/auth/send-otp' && req.method === 'POST') {
    try {
      const { email } = await parseJsonBody(req);
      if (!email || typeof email !== 'string') {
        return sendJson(res, 400, { success: false, error: 'Email address is required.' });
      }

      const normalizedEmail = email.trim().toLowerCase();

      // SECURE SERVER-SIDE DOMAIN RESTRICTION
      if (!normalizedEmail.endsWith('@kkwagh.edu.in')) {
        return sendJson(res, 403, {
          success: false,
          error: 'Access restricted. Only official @kkwagh.edu.in college email addresses are permitted.'
        });
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
      // (ensures students can verify in test/dev environments or when SMTP rate limits apply)
      const generatedCode = String(Math.floor(100000 + Math.random() * 900000));
      devOtpStore.set(normalizedEmail, {
        code: generatedCode,
        expires: Date.now() + 10 * 60 * 1000 // 10 minutes
      });

      return sendJson(res, 200, {
        success: true,
        email: normalizedEmail,
        supabaseSent,
        // Only include devCode when DEV_OTP_VISIBLE is explicitly enabled
        // (e.g. local sandbox without SMTP). Never expose in production.
        devCode: DEV_OTP_VISIBLE ? generatedCode : undefined,
        message: supabaseSent
          ? `Verification OTP sent to ${normalizedEmail}. Please check your college inbox.`
          : `Verification code generated for ${normalizedEmail}. Enter the 6-digit code to continue.`
      });
    } catch (err) {
      return sendJson(res, 500, { success: false, error: err.message });
    }
  }

  // 3. Server-side OTP Verification
  if (pathname === '/api/auth/verify-otp' && req.method === 'POST') {
    try {
      const { email, token, role } = await parseJsonBody(req);
      if (!email || !token) {
        return sendJson(res, 400, { success: false, error: 'Email and verification code are required.' });
      }

      const normalizedEmail = email.trim().toLowerCase();

      // SECURE SERVER-SIDE DOMAIN CHECK
      if (!normalizedEmail.endsWith('@kkwagh.edu.in')) {
        return sendJson(res, 403, {
          success: false,
          error: 'Access restricted. Only official @kkwagh.edu.in college email addresses are permitted.'
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

      // Define standard user profiles for sample demo personas
      const DEMO_PROFILES = {
        'aarav.sharma@kkwagh.edu.in': { displayName: 'Aarav Sharma', role: 'STUDENT', department: 'Computer Engineering' },
        'aasayyed370125@kkwagh.edu.in': { displayName: 'Aalim Sayyed', role: 'STUDENT', department: 'Computer Engineering' },
        'neha.patil@kkwagh.edu.in': { displayName: 'Neha Patil', role: 'STUDENT', department: 'Computer Engineering' },
        'hod.comp@kkwagh.edu.in': { displayName: 'Dr. V. K. Wagh', role: 'FACULTY', department: 'Computer Engineering', title: 'HOD — Computer Engineering' },
        'mentor.comp@kkwagh.edu.in': { displayName: 'Prof. P. R. Rao', role: 'FACULTY', department: 'Computer Engineering', title: 'Faculty Wellness Coordinator' }
      };

      const matchedProfile = DEMO_PROFILES[normalizedEmail];
      const assignedRole = matchedProfile ? matchedProfile.role : (role === 'FACULTY' ? 'FACULTY' : 'STUDENT');
      const displayName = matchedProfile ? matchedProfile.displayName : normalizedEmail.split('@')[0]
        .split(/[._0-9]+/)
        .filter(Boolean)
        .map(p => p.charAt(0).toUpperCase() + p.slice(1))
        .join(' ') || (assignedRole === 'FACULTY' ? 'Faculty Member' : 'K.K. Wagh Student');

      return sendJson(res, 200, {
        success: true,
        user: {
          id: 'user_' + Buffer.from(normalizedEmail).toString('hex').slice(0, 16),
          email: normalizedEmail,
          displayName,
          role: assignedRole,
          department: 'Computer Engineering',
          departmentId: 'comp_eng',
          verifiedAt: new Date().toISOString()
        },
        session
      });
    } catch (err) {
      return sendJson(res, 500, { success: false, error: err.message });
    }
  }

  // 4a. Get Check-in Records
  if (pathname === '/api/checkins' && req.method === 'GET') {
    try {
      const email = parsedUrl.searchParams.get('email');
      if (!email || !email.endsWith('@kkwagh.edu.in')) {
        return sendJson(res, 403, { success: false, error: 'Unauthorized. Valid college email required.' });
      }

      let records = null;
      if (SUPABASE_URL && SUPABASE_ANON_KEY) {
        try {
          const authHeader = req.headers['authorization'] || `Bearer ${SUPABASE_ANON_KEY}`;
          const sRes = await fetch(`${SUPABASE_URL}/rest/v1/checkins?email=eq.${encodeURIComponent(email)}&order=date.asc`, {
            method: 'GET',
            headers: {
              'apikey': SUPABASE_ANON_KEY,
              'Authorization': authHeader
            }
          });
          if (sRes.ok) {
            const fetched = await sRes.json();
            if (Array.isArray(fetched) && fetched.length > 0) {
              records = fetched;
            }
          }
        } catch (_) {}
      }

      // Fallback to in-memory checkins
      if (!records) {
        records = inMemoryCheckins.filter(r => r.email === email.trim().toLowerCase());
      }

      return sendJson(res, 200, { success: true, records: records || [] });
    } catch (err) {
      return sendJson(res, 500, { success: false, error: err.message });
    }
  }

  // 4b. Save Check-in Record
  if (pathname === '/api/checkins' && req.method === 'POST') {
    try {
      const data = await parseJsonBody(req);
      if (!data || !data.email || !data.email.endsWith('@kkwagh.edu.in')) {
        return sendJson(res, 403, { success: false, error: 'Unauthorized. Valid college email required.' });
      }

      const normalizedEmail = data.email.trim().toLowerCase();
      const record = {
        id: String(data.id || Date.now()),
        email: normalizedEmail,
        department: data.department || 'comp_eng',
        date: data.date,
        day: data.day,
        mood: data.mood,
        sleep: parseFloat(data.sleep) || 0,
        water: parseInt(data.water, 10) || 0,
        waterLiters: parseFloat(data.waterLiters) || ((parseInt(data.water, 10) || 0) * 0.25),
        steps: parseInt(data.steps, 10) || 0,
        study: parseFloat(data.study) || 0,
        stress: parseInt(data.stress, 10) || 2,
        updatedAt: new Date().toISOString()
      };

      // Upsert into in-memory store
      const existingIdx = inMemoryCheckins.findIndex(r => r.email === normalizedEmail && r.date === record.date);
      if (existingIdx !== -1) {
        inMemoryCheckins[existingIdx] = record;
      } else {
        inMemoryCheckins.push(record);
      }

      // If Supabase is configured, attempt to save to Supabase checkins table
      if (SUPABASE_URL && SUPABASE_ANON_KEY) {
        try {
          const authHeader = req.headers['authorization'] || `Bearer ${SUPABASE_ANON_KEY}`;
          await fetch(`${SUPABASE_URL}/rest/v1/checkins`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'apikey': SUPABASE_ANON_KEY,
              'Authorization': authHeader,
              'Prefer': 'resolution=merge-duplicates'
            },
            body: JSON.stringify(record)
          });
        } catch (_) {}
      }

      return sendJson(res, 200, { success: true, savedAt: new Date().toISOString(), record });
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
          sampleUsersTotal: 5,
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
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`Campus Care Web server running on port ${PORT}`);
});
