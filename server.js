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
      const { email, token } = await parseJsonBody(req);
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

      // Parse user name from email
      const usernamePart = normalizedEmail.split('@')[0];
      const displayName = usernamePart
        .split('.')
        .map(p => p.charAt(0).toUpperCase() + p.slice(1))
        .join(' ');

      return sendJson(res, 200, {
        success: true,
        user: {
          email: normalizedEmail,
          displayName: displayName || 'K.K. Wagh Student',
          role: 'STUDENT',
          department: 'Computer Engineering',
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
            const records = await sRes.json();
            return sendJson(res, 200, { success: true, records });
          }
        } catch (_) {}
      }

      return sendJson(res, 200, { success: true, records: [] });
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
              'Prefer': 'return=representation'
            },
            body: JSON.stringify({
              id: String(data.id || Date.now()),
              email: data.email,
              date: data.date,
              day: data.day,
              mood: data.mood,
              sleep: data.sleep,
              water: data.water,
              study: data.study,
              stress: data.stress
            })
          });
        } catch (_) {}
      }

      return sendJson(res, 200, { success: true, savedAt: new Date().toISOString() });
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
