
// ============================================================
//   TalentHunt — Main Application Script (Debugged)
// ============================================================

document.addEventListener('DOMContentLoaded', () => {

  // ─── State ────────────────────────────────────────────────
  const state = {
    user: null,
    page: 'home',
    votes: JSON.parse(localStorage.getItem('th_votes') || '[]'),
  };

  // ─── DOM References ───────────────────────────────────────
  const preloader     = document.getElementById('preloader');
  const plBar         = document.getElementById('plBar');
  const plPct         = document.getElementById('plPct');
  const loginScreen   = document.getElementById('loginScreen');
  const mainWebsite   = document.getElementById('mainWebsite');
  const navbar        = document.getElementById('navbar');
  const authError     = document.getElementById('authError');
  const authErrorMsg  = document.getElementById('authErrorMsg');
  const tabUser       = document.getElementById('tabUser');
  const tabAdmin      = document.getElementById('tabAdmin');
  const userLoginForm    = document.getElementById('userLoginForm');
  const userRegisterForm = document.getElementById('userRegisterForm');
  const adminLoginForm   = document.getElementById('adminLoginForm');
  const goRegister    = document.getElementById('goRegister');
  const goLogin       = document.getElementById('goLogin');
  const fillUser      = document.getElementById('fillUser');
  const fillAdmin     = document.getElementById('fillAdmin');
  const navAvatar     = document.getElementById('navAvatar');
  const navUsername   = document.getElementById('navUsername');
  const navRoleBadge  = document.getElementById('navRoleBadge');
  const logoutBtn     = document.getElementById('logoutBtn');
  const navAdmin      = document.getElementById('navAdmin');
  const statTalents      = document.getElementById('statTalents');
  const statCompetitions = document.getElementById('statCompetitions');
  const statVotes        = document.getElementById('statVotes');
  const statSubmissions  = document.getElementById('statSubmissions');
  const voteModal      = document.getElementById('voteModal');
  const voteModalName  = document.getElementById('voteModalName');
  const voteConfirmBtn = document.getElementById('voteConfirmBtn');
  const voteCancelBtn  = document.getElementById('voteCancelBtn');

  let pendingVoteTalentId = null;
  let pendingVoteName     = null;
  let allTalents = [];

  // ─── Mock Data ────────────────────────────────────────────
  const MOCK_COMPETITIONS = [
    { id:1, emoji:'🎤', title:'Voice of the Year 2026',
      description:'Find the most captivating voice across India. Open to all age groups. Solo and group entries welcome.',
      category:'Singing', prize:'₹50,000', deadline:'2026-10-31', status:'open', entries:142 },
    { id:2, emoji:'💃', title:'Dance Nation Championship',
      description:'Showcase your moves in any dance style — classical, hip-hop, contemporary, folk. Let rhythm speak!',
      category:'Dancing', prize:'₹30,000', deadline:'2026-11-15', status:'open', entries:98 },
    { id:3, emoji:'🎨', title:'Masters of Art Exhibition',
      description:'Submit your finest digital or traditional artwork. A jury of artists will select the top 20.',
      category:'Art', prize:'₹20,000', deadline:'2026-09-30', status:'judging', entries:77 },
    { id:4, emoji:'🎭', title:'Stage & Screen Acting Award',
      description:'Deliver a 3-minute monologue of your choice. Theatre and film performances both accepted.',
      category:'Acting', prize:'₹25,000', deadline:'2026-12-01', status:'upcoming', entries:0 },
    { id:5, emoji:'🎸', title:'Indie Music Festival',
      description:'Original compositions only. Acoustic, electric, classical — all genres welcome. Show us your sound.',
      category:'Music', prize:'₹35,000', deadline:'2026-10-15', status:'open', entries:64 },
    { id:6, emoji:'😂', title:'Stand-Up Comedy Open Mic',
      description:'Got jokes? Make the judges laugh with a 5-minute original stand-up set. Clean and sharp comedy.',
      category:'Comedy', prize:'₹15,000', deadline:'2026-09-20', status:'closed', entries:112 },
  ];

  const MOCK_TALENTS = [
    { id:1,  name:'Priya Sharma',  initials:'PS', category:'Singing', bio:'Classical vocalist with 10 years of training. Finalist at multiple state competitions.', votes:142 },
    { id:2,  name:'Arjun Menon',   initials:'AM', category:'Dancing', bio:'Contemporary dancer fusing Bharatanatyam with hip-hop. Winner of 2025 Regional Dance-Off.', votes:118 },
    { id:3,  name:'Sneha Kapoor',  initials:'SK', category:'Art',     bio:'Digital illustrator and watercolor artist. Published in 3 national design magazines.', votes:95 },
    { id:4,  name:'Rahul Dev',     initials:'RD', category:'Comedy',  bio:'Stand-up comedian with 50+ live shows. Known for sharp social commentary.', votes:88 },
    { id:5,  name:'Ananya Pillai', initials:'AP', category:'Singing', bio:'Carnatic singer with a 4-octave range. Performed at the Madras Music Academy.', votes:76 },
    { id:6,  name:'Vikram Singh',  initials:'VS', category:'Music',   bio:'Guitarist and composer. Scored 2 indie films and 3 regional albums.', votes:71 },
    { id:7,  name:'Meera Nair',    initials:'MN', category:'Acting',  bio:'Theatre actor with 8 years on stage. Trained under the National School of Drama.', votes:64 },
    { id:8,  name:'Kiran Joshi',   initials:'KJ', category:'Dancing', bio:'Street dancer turned choreographer. Featured in 2 Bollywood music videos.', votes:59 },
    { id:9,  name:'Divya Rajan',   initials:'DR', category:'Art',     bio:'Sculpted 200+ pieces exhibited across 4 countries. Specializes in clay and stone.', votes:52 },
    { id:10, name:'Aditya Kumar',  initials:'AK', category:'Sports',  bio:'National-level martial artist and parkour enthusiast. 2x state gold medalist.', votes:48 },
    { id:11, name:'Lakshmi Iyer',  initials:'LI', category:'Music',   bio:'Flutist with 15 years of Hindustani classical training.', votes:44 },
  ];

  // ─── Utilities ────────────────────────────────────────────

  function showToast(msg, type = 'info', duration = 3500) {
    const icons = { success:'fa-circle-check', error:'fa-circle-xmark', info:'fa-circle-info' };
    const container = document.getElementById('toastContainer');
    const t = document.createElement('div');
    t.className = `toast ${type}`;
    t.innerHTML = `<i class="fa-solid ${icons[type] || icons.info}"></i> ${msg}`;
    container.appendChild(t);
    setTimeout(() => {
      t.classList.add('fade-out');
      setTimeout(() => t.remove(), 400);
    }, duration);
  }

  function animateCounter(el, target) {
    if (!el) return;
    let cur = 0;
    const step = Math.max(1, Math.ceil(target / 50));
    const timer = setInterval(() => {
      cur = Math.min(cur + step, target);
      el.textContent = cur.toLocaleString();
      if (cur >= target) clearInterval(timer);
    }, 30);
  }

  function saveSession(user) { localStorage.setItem('th_session', JSON.stringify(user)); }
  function loadSession()     { try { return JSON.parse(localStorage.getItem('th_session')); } catch { return null; } }
  function clearSession()    { localStorage.removeItem('th_session'); }

  function showError(msg) {
    authErrorMsg.textContent = msg;
    authError.classList.add('show');
    setTimeout(() => authError.classList.remove('show'), 4000);
  }

  function formatDate(d) {
    if (!d) return '—';
    try {
      return new Date(d).toLocaleDateString('en-IN', { day:'numeric', month:'short', year:'numeric' });
    } catch { return d; }
  }

  function categoryEmoji(cat) {
    const m = { Singing:'🎤', Dancing:'💃', Art:'🎨', Sports:'⚡', Comedy:'😂', Acting:'🎭', Music:'🎸' };
    return m[cat] || '🏆';
  }

  function capitalize(s) { return s.charAt(0).toUpperCase() + s.slice(1); }

  // ─── Particles ────────────────────────────────────────────

  function spawnParticles(containerId, count, extraClass) {
    const container = document.getElementById(containerId);
    if (!container) return;
    for (let i = 0; i < count; i++) {
      const p = document.createElement('div');
      p.className = extraClass;
      const size = 2 + Math.random() * 4;
      p.style.cssText = `
        position:absolute;
        width:${size}px; height:${size}px; border-radius:50%;
        left:${Math.random()*100}%; top:${Math.random()*100}%;
        background:${Math.random()>0.5 ? 'var(--gold)' : 'var(--crimson-l)'};
        animation-duration:${3 + Math.random()*6}s;
        animation-delay:${Math.random()*4}s;
        opacity:${0.2 + Math.random()*0.5};
      `;
      container.appendChild(p);
    }
  }

  function spawnStars(containerId, count, cssClass) {
    const container = document.getElementById(containerId);
    if (!container) return;
    for (let i = 0; i < count; i++) {
      const s = document.createElement('div');
      s.className = cssClass;
      const size = 2 + Math.random() * 3;
      s.style.cssText = `
        position:absolute; border-radius:50%;
        width:${size}px; height:${size}px;
        top:${Math.random()*100}%; left:${Math.random()*100}%;
        background:${Math.random()>0.6 ? 'var(--gold)' : '#fff'};
        opacity:0;
        animation: twinkle ${2+Math.random()*4}s ease-in-out ${Math.random()*4}s infinite;
      `;
      container.appendChild(s);
    }
  }

  // ─── PRELOADER ────────────────────────────────────────────

  function runPreloader(onComplete) {
    spawnParticles('plParticles', 18, 'pl-particle');

    let pct = 0;
    const interval = setInterval(() => {
      pct += Math.random() * 8 + 3;
      if (pct >= 100) {
        pct = 100;
        clearInterval(interval);
        plBar.style.width = '100%';
        plPct.textContent = '100%';
        setTimeout(() => {
          preloader.classList.add('fade-out');
          setTimeout(() => {
            preloader.style.display = 'none';
            onComplete();
          }, 800);
        }, 400);
        return;
      }
      plBar.style.width = pct + '%';
      plPct.textContent = Math.floor(pct) + '%';
    }, 80);
  }

  // ─── AUTH TABS ────────────────────────────────────────────

  function showAuthTab(tab) {
    document.querySelectorAll('.auth-tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.auth-form').forEach(f => f.classList.remove('active'));
    authError.classList.remove('show');
    if (tab === 'user') {
      tabUser.classList.add('active');
      userLoginForm.classList.add('active');
    } else {
      tabAdmin.classList.add('active');
      adminLoginForm.classList.add('active');
    }
  }

  tabUser.addEventListener('click', () => showAuthTab('user'));
  tabAdmin.addEventListener('click', () => showAuthTab('admin'));

  goRegister.addEventListener('click', () => {
    userLoginForm.classList.remove('active');
    userRegisterForm.classList.add('active');
    authError.classList.remove('show');
  });
  goLogin.addEventListener('click', () => {
    userRegisterForm.classList.remove('active');
    userLoginForm.classList.add('active');
  });

  fillUser.addEventListener('click', () => {
    document.getElementById('uEmail').value    = 'alex@talenthunt.com';
    document.getElementById('uPassword').value = 'user123';
    showToast('Demo credentials filled!', 'info');
  });
  fillAdmin.addEventListener('click', () => {
    document.getElementById('aUser').value     = 'admin';
    document.getElementById('aPassword').value = 'admin123';
    showToast('Admin credentials filled!', 'info');
  });

  // ─── LOGIN & AUTHENTICATION LOGIC (PHASE 3 & 4) ──────────

  async function attemptLogin(email, password, role) {
    try {
      const res = await fetch('/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password, portal: role, role }),
      });
      if (res.ok) {
        const data = await res.json();
        if (data.user) {
          return {
            id: data.user.id,
            name: data.user.name,
            email: data.user.email,
            role: data.user.role || role,
            avatarUrl: data.user.avatarUrl,
            bio: data.user.bio
          };
        }
        return { name: data.name || email.split('@')[0], email, role };
      }
    } catch (e) {
      console.warn('Backend login notice, checking credentials fallback:', e);
    }

    // Demo fallback credentials (guaranteed immediate login)
    const cleanEmail = (email || '').toLowerCase().trim();
    if (role === 'ADMIN' && (cleanEmail === 'admin' || cleanEmail === 'admin@talenthunt.com') && password === 'admin123') {
      return { id: 8, name: 'System Administrator', email: 'admin@talenthunt.com', role: 'ADMIN' };
    }
    if (role === 'USER' && (cleanEmail === 'alex@talenthunt.com' || cleanEmail === 'user@talenthunt.com' || cleanEmail === 'alex' || cleanEmail === 'user') && password === 'user123') {
      return { id: 1, name: 'Alex Rivera', email: 'alex@talenthunt.com', role: 'CONTESTANT' };
    }

    showError('Invalid credentials or unauthorized role access.');
    return null;
  }

  // User login with Phase 3 JavaScript validation (empty check, regex format)
  userLoginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const emailEl    = document.getElementById('uEmail');
    const passwordEl = document.getElementById('uPassword');
    const email      = emailEl.value.trim();
    const password   = passwordEl.value;
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    // Reset styles
    emailEl.style.borderColor = '';
    passwordEl.style.borderColor = '';

    if (!email || !password) {
      showError('Please fill in both Email and Password fields.');
      if (!email) emailEl.style.borderColor = '#ef4444';
      if (!password) passwordEl.style.borderColor = '#ef4444';
      return;
    }

    if (!emailPattern.test(email) && email !== 'admin') {
      emailEl.style.borderColor = '#ef4444';
      showError('Please enter a valid email format (e.g., user@talenthunt.com).');
      return;
    }

    if (password.length < 6) {
      passwordEl.style.borderColor = '#ef4444';
      showError('Password must contain at least 6 characters.');
      return;
    }

    const btn = document.getElementById('userLoginBtn');
    btn.disabled = true;
    btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Authenticating…';
    const user = await attemptLogin(email, password, 'USER');
    if (user) {
      loginSuccess(user);
    } else {
      btn.disabled = false;
      btn.innerHTML = '<i class="fa-solid fa-arrow-right-to-bracket"></i> Sign In as Contestant';
    }
  });

  // User Register with Phase 3 JavaScript validation (empty fields, format checks)
  userRegisterForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const nameEl     = document.getElementById('rName');
    const emailEl    = document.getElementById('rEmail');
    const passwordEl = document.getElementById('rPassword');

    const name     = nameEl.value.trim();
    const email    = emailEl.value.trim();
    const password = passwordEl.value;
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    // Reset styles
    nameEl.style.borderColor = '';
    emailEl.style.borderColor = '';
    passwordEl.style.borderColor = '';

    if (!name || !email || !password) {
      showError('Please fill in all registration fields.');
      if (!name) nameEl.style.borderColor = '#ef4444';
      if (!email) emailEl.style.borderColor = '#ef4444';
      if (!password) passwordEl.style.borderColor = '#ef4444';
      return;
    }

    if (name.length < 2) {
      nameEl.style.borderColor = '#ef4444';
      showError('Full name must be at least 2 characters long.');
      return;
    }

    if (!emailPattern.test(email)) {
      emailEl.style.borderColor = '#ef4444';
      showError('Invalid email format. Expected format: user@domain.com');
      return;
    }

    if (password.length < 6) {
      passwordEl.style.borderColor = '#ef4444';
      showError('Password must be at least 6 characters.');
      return;
    }

    try {
      const res = await fetch('/api/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password, role: 'CONTESTANT' }),
      });
      if (res.ok) {
        const data = await res.json();
        loginSuccess(data.user || { name, email, role: 'CONTESTANT' });
        showToast('Registration complete! Welcome to TalentHunt.', 'success');
        return;
      } else {
        const errData = await res.json().catch(() => ({}));
        showError(errData.error || 'Registration failed. Email may already be taken.');
        return;
      }
    } catch(e) {}

    loginSuccess({ name, email, role: 'CONTESTANT' }); // offline fallback
  });

  // Admin login with validation
  adminLoginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const userEl = document.getElementById('aUser');
    const passEl = document.getElementById('aPassword');
    const username = userEl.value.trim();
    const password = passEl.value;

    userEl.style.borderColor = '';
    passEl.style.borderColor = '';

    if (!username || !password) {
      showError('Enter administrative credentials.');
      if (!username) userEl.style.borderColor = '#ef4444';
      if (!password) passEl.style.borderColor = '#ef4444';
      return;
    }

    const user = await attemptLogin(username, password, 'ADMIN');
    if (user) {
      loginSuccess(user);
    } else {
      showError('Invalid admin credentials. Hint: admin / admin123');
    }
  });

  // ─── LOGIN SUCCESS ─────────────────────────────────────────

  function loginSuccess(user) {
    state.user = user;
    saveSession(user);

    // Update navbar
    navAvatar.textContent    = user.name.charAt(0).toUpperCase();
    navUsername.textContent  = user.name;
    navRoleBadge.textContent = user.role;
    navRoleBadge.classList.toggle('admin', user.role === 'ADMIN');
    navAdmin.style.display = user.role === 'ADMIN' ? '' : 'none';

    // Transition: hide login → show main
    loginScreen.classList.remove('visible');
    setTimeout(() => {
      mainWebsite.classList.add('visible');
      spawnStars('heroStars', 20, 'h-star');
      loadStats();
      loadCompetitions();
      loadTalents();
      if (user.role === 'ADMIN') loadAdminData();
      setTimeout(initScrollReveal, 300);
    }, 600);

    showToast(`Welcome, ${user.name}! 🎉`, 'success');
  }

  // ─── LOGOUT ───────────────────────────────────────────────

  logoutBtn.addEventListener('click', () => {
    state.user = null;
    clearSession();
    mainWebsite.classList.remove('visible');
    navAdmin.style.display = 'none';
    navRoleBadge.classList.remove('admin');
    setTimeout(() => loginScreen.classList.add('visible'), 400);
    showPage('home');
    showToast('Logged out successfully.', 'info');
    showAuthTab('user');
  });

  // ─── NAVBAR SCROLL ────────────────────────────────────────

  window.addEventListener('scroll', () => {
    navbar.classList.toggle('scrolled', window.scrollY > 30);
  }, { passive: true });

  // ─── PAGE NAVIGATION ──────────────────────────────────────

  function showPage(pageId) {
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));

    const page = document.getElementById('page' + capitalize(pageId));
    if (page) page.classList.add('active');

    const link = document.querySelector(`.nav-link[data-page="${pageId}"]`);
    if (link) link.classList.add('active');

    state.page = pageId;
    window.scrollTo({ top: 0, behavior: 'smooth' });
    setTimeout(initScrollReveal, 150);
  }

  // Nav link clicks
  document.querySelectorAll('.nav-link').forEach(btn => {
    btn.addEventListener('click', () => showPage(btn.dataset.page));
  });

  document.getElementById('navLogo').addEventListener('click', () => showPage('home'));
  document.getElementById('heroEnterBtn').addEventListener('click', () => showPage('competitions'));
  document.getElementById('heroExploreBtn').addEventListener('click', () => showPage('talents'));

  // ─── LOAD STATS ───────────────────────────────────────────

  async function loadStats() {
    try {
      const res = await fetch('/api/stats');
      if (res.ok) {
        const d = await res.json();
        animateCounter(statTalents,       d.totalTalents      || 0);
        animateCounter(statCompetitions,  d.totalCompetitions || 0);
        animateCounter(statVotes,         d.totalVotes        || 0);
        animateCounter(statSubmissions,   d.totalSubmissions  || 0);
        const el = document.getElementById('aTalents');
        if (el) {
          document.getElementById('aTalents').textContent      = d.totalTalents      || 0;
          document.getElementById('aCompetitions').textContent = d.totalCompetitions || 0;
          document.getElementById('aSubmissions').textContent  = d.totalSubmissions  || 0;
          document.getElementById('aVotes').textContent        = d.totalVotes        || 0;
          document.getElementById('aDb').textContent           = (d.dbEngine || 'H2').replace('Embedded ', '');
        }
        return;
      }
    } catch(e) {}

    // Fallback
    animateCounter(statTalents,      MOCK_TALENTS.length);
    animateCounter(statCompetitions, MOCK_COMPETITIONS.filter(c => c.status === 'open').length);
    animateCounter(statVotes,        MOCK_TALENTS.reduce((a, t) => a + t.votes, 0));
    animateCounter(statSubmissions,  MOCK_COMPETITIONS.reduce((a, c) => a + c.entries, 0));
    const el = document.getElementById('aTalents');
    if (el) {
      document.getElementById('aTalents').textContent      = MOCK_TALENTS.length;
      document.getElementById('aCompetitions').textContent = MOCK_COMPETITIONS.length;
      document.getElementById('aSubmissions').textContent  = MOCK_COMPETITIONS.reduce((a, c) => a + c.entries, 0);
      document.getElementById('aVotes').textContent        = MOCK_TALENTS.reduce((a, t) => a + t.votes, 0);
    }
  }

  // ─── COMPETITIONS ─────────────────────────────────────────

  async function loadCompetitions() {
    const grid = document.getElementById('competitionsGrid');
    let comps = [];
    try {
      const res = await fetch('/api/competitions');
      if (res.ok) comps = await res.json();
    } catch(e) {}
    if (!comps.length) comps = MOCK_COMPETITIONS;

    // Populate submit dropdown
    const sfComp = document.getElementById('sfCompetition');
    sfComp.innerHTML = '<option value="">Select competition</option>';
    comps.filter(c => c.status === 'open' || c.status === 'OPEN' || c.status === 'active').forEach(c => {
      const opt = document.createElement('option');
      opt.value = c.id;
      opt.textContent = c.title || c.name;
      sfComp.appendChild(opt);
    });

    grid.innerHTML = comps.map(c => {
      const raw = (c.status || 'open').toLowerCase();
      const statusMap = { open:'open', active:'open', closed:'closed', inactive:'closed', upcoming:'upcoming', judging:'judging' };
      const s = statusMap[raw] || 'open';
      const labels = { open:'Open', closed:'Closed', upcoming:'Upcoming', judging:'Judging' };
      const isOpen = s === 'open';

      return `
      <div class="comp-card reveal">
        <div class="comp-card-banner">${c.emoji || categoryEmoji(c.category)}</div>
        <div class="comp-card-body">
          <div class="comp-status ${s}">${labels[s] || s}</div>
          <h3>${c.title || c.name || 'Untitled'}</h3>
          <p>${c.description || ''}</p>
          <div class="comp-meta">
            <span><i class="fa-solid fa-tag"></i> ${c.category || 'Open'}</span>
            <span><i class="fa-solid fa-users"></i> ${c.entries || c.submissionCount || 0} entries</span>
            <span><i class="fa-regular fa-calendar"></i> ${formatDate(c.deadline || c.endDate)}</span>
          </div>
          <div class="comp-prize"><i class="fa-solid fa-trophy"></i> ${c.prize || c.prizeAmount || '—'}</div>
          <button class="btn-enter" ${!isOpen ? 'disabled' : ''}
            data-compid="${c.id}" data-comptitle="${(c.title || c.name || '').replace(/"/g, '&quot;')}">
            ${isOpen
              ? '<i class="fa-solid fa-play"></i> Enter Now'
              : `<i class="fa-solid fa-lock"></i> ${labels[s]}`}
          </button>
        </div>
      </div>`;
    }).join('');

    // Wire up Enter Now buttons
    grid.querySelectorAll('.btn-enter:not([disabled])').forEach(btn => {
      btn.addEventListener('click', () => {
        showPage('submit');
        const dropdown = document.getElementById('sfCompetition');
        Array.from(dropdown.options).forEach(o => {
          if (o.text === btn.dataset.comptitle) dropdown.value = o.value;
        });
        if (state.user) {
          document.getElementById('sfName').value  = state.user.name;
          document.getElementById('sfEmail').value = state.user.email;
        }
        showToast(`Competition selected: ${btn.dataset.comptitle}`, 'info');
      });
    });

    setTimeout(initScrollReveal, 150);
  }

  // ─── TALENTS (LIVE DB BACKEND WITH MOCK FALLBACK) ────────

  async function loadTalents() {
    let talents = [];
    try {
      const res = await fetch('/api/talents');
      if (res.ok) {
        const raw = await res.json();
        if (Array.isArray(raw) && raw.length > 0) talents = raw;
      }
    } catch(e) {}
    if (!talents.length) talents = MOCK_TALENTS;

    allTalents = talents.map((t, i) => ({
      id:       t.id || i + 1,
      name:     t.userName || t.name || t.talentName || 'Unknown Performer',
      initials: t.initials || (t.userName || t.name || 'U').split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2),
      category: t.categoryName || t.category || t.talentCategory || 'Vocal & Music',
      bio:      t.description || t.bio || 'Passionate artist showcase in TalentHunt.',
      votes:    t.votesCount !== undefined ? t.votesCount : (t.votes || 0),
    }));

    renderTalents(allTalents);
    renderLeaderboard(allTalents);

    // Admin talents table
    const adminTbl = document.getElementById('adminTalentsTable');
    if (adminTbl) {
      adminTbl.innerHTML = allTalents.slice(0, 10).map(t => `
        <tr>
          <td><strong>${t.name}</strong></td>
          <td><span style="color:var(--gold);font-size:0.8rem;">${t.category}</span></td>
          <td><strong style="color:var(--gold);">${t.votes}</strong></td>
        </tr>`).join('');
    }
  }

  function renderTalents(talents) {
    const grid = document.getElementById('talentsGrid');
    if (!talents.length) {
      grid.innerHTML = `<div class="empty-state" style="grid-column:1/-1">
        <i class="fa-solid fa-magnifying-glass"></i><p>No talents found matching your search.</p></div>`;
      return;
    }
    grid.innerHTML = talents.map(t => {
      const voted = state.votes.includes(t.id);
      return `
      <div class="talent-card reveal" data-id="${t.id}">
        <div class="talent-card-top"></div>
        <div class="talent-avatar">${t.initials}</div>
        <div class="talent-card-body">
          <div class="talent-name">${t.name}</div>
          <div class="talent-category">${t.category}</div>
          <div class="talent-bio">${t.bio || 'A talented individual competing for the crown.'}</div>
          <div class="talent-votes">
            <span class="vote-count"><strong>${t.votes}</strong> votes</span>
            <button class="btn-vote ${voted ? 'voted' : ''}" data-id="${t.id}" data-name="${t.name.replace(/"/g, '&quot;')}">
              <i class="fa-${voted ? 'solid' : 'regular'} fa-star"></i>
              ${voted ? 'Voted' : 'Vote'}
            </button>
          </div>
        </div>
      </div>`;
    }).join('');

    grid.querySelectorAll('.btn-vote').forEach(btn => {
      btn.addEventListener('click', () => openVoteModal(+btn.dataset.id, btn.dataset.name));
    });

    setTimeout(initScrollReveal, 150);
  }

  // Search & filter
  document.getElementById('talentSearch').addEventListener('input', filterTalents);
  document.getElementById('categoryFilter').addEventListener('change', filterTalents);

  function filterTalents() {
    const q   = document.getElementById('talentSearch').value.toLowerCase().trim();
    const cat = document.getElementById('categoryFilter').value;
    const filtered = allTalents.filter(t => {
      const matchQ   = !q || t.name.toLowerCase().includes(q) || t.category.toLowerCase().includes(q) || t.bio.toLowerCase().includes(q);
      const matchCat = !cat || t.category === cat;
      return matchQ && matchCat;
    });
    renderTalents(filtered);
  }

  // ─── LEADERBOARD ──────────────────────────────────────────

  function renderLeaderboard(talents) {
    const sorted = [...talents].sort((a, b) => b.votes - a.votes);
    const max = sorted[0]?.votes || 1;
    const tbody = document.getElementById('leaderboardBody');
    if (!tbody) return;
    tbody.innerHTML = sorted.map((t, i) => {
      const rank = i + 1;
      const pct  = Math.round((t.votes / max) * 100);
      const medals = ['🥇', '🥈', '🥉'];
      const rankClass = rank <= 3 ? `rank-${rank}` : 'rank-n';
      const rankDisplay = rank <= 3 ? medals[rank - 1] : rank;
      return `
      <tr>
        <td><div class="rank-badge ${rankClass}">${rankDisplay}</div></td>
        <td>
          <div class="lb-talent-cell">
            <div class="lb-avatar">${t.initials}</div>
            <div>
              <div class="lb-name">${t.name}</div>
              <div class="lb-category">${t.category}</div>
            </div>
          </div>
        </td>
        <td>${t.category}</td>
        <td>
          <div class="score-bar-wrap">
            <div class="score-bar"><div class="score-bar-fill" style="width:${pct}%"></div></div>
            <span class="score-val">${pct}</span>
          </div>
        </td>
        <td><strong style="color:var(--gold)">${t.votes}</strong></td>
      </tr>`;
    }).join('');
  }

  // ─── VOTE MODAL (LIVE BACKEND PERSISTENCE) ────────────────

  function openVoteModal(talentId, name) {
    if (state.votes.includes(talentId)) {
      showToast(`You already voted for ${name}!`, 'info');
      return;
    }
    pendingVoteTalentId = talentId;
    pendingVoteName     = name;
    voteModalName.textContent = name;
    voteModal.classList.add('open');
  }

  voteConfirmBtn.addEventListener('click', async () => {
    voteModal.classList.remove('open');
    const id = pendingVoteTalentId;
    if (!id) return;

    try {
      await fetch('/api/vote', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ submissionId: id, talentId: id, userId: state.user?.id || 9 })
      });
    } catch(e) {}

    state.votes.push(id);
    localStorage.setItem('th_votes', JSON.stringify(state.votes));

    const talent = allTalents.find(t => t.id === id);
    if (talent) talent.votes++;

    renderTalents(allTalents);
    renderLeaderboard(allTalents);
    showToast(`Voted for ${pendingVoteName}! ⭐`, 'success');
    loadStats();

    pendingVoteTalentId = null;
    pendingVoteName     = null;
  });

  voteCancelBtn.addEventListener('click', () => voteModal.classList.remove('open'));
  voteModal.addEventListener('click', e => { if (e.target === voteModal) voteModal.classList.remove('open'); });

  // ─── SUBMIT FORM (PHASE 3 & 4 FORM FLOW & DB INTEGRATION) ──

  document.getElementById('submitEntryForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const name     = document.getElementById('sfName').value.trim();
    const email    = document.getElementById('sfEmail').value.trim();
    const category = document.getElementById('sfCategory').value;
    const compId   = document.getElementById('sfCompetition').value;
    const title    = document.getElementById('sfTitle').value.trim();
    const bio      = document.getElementById('sfBio').value.trim();
    const media    = document.getElementById('sfMedia').value.trim();

    if (!name || !email || !category || !title) {
      showToast('Please fill in Name, Email, Category and Title.', 'error');
      return;
    }

    const btn = document.getElementById('submitEntryBtn');
    btn.disabled = true;
    btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Submitting to Server…';

    const catMap = { 'Singing': 1, 'Dancing': 2, 'Art': 3, 'Sports': 4, 'Comedy': 5, 'Acting': 6, 'Music': 1 };
    const categoryId = catMap[category] || 1;

    let submitted = false;
    try {
      const res = await fetch('/api/submissions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name,
          email,
          userId: state.user?.id || 1,
          competitionId: parseInt(compId) || 1,
          categoryId: categoryId,
          title,
          description: bio,
          mediaUrl: media || 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4'
        }),
      });
      if (res.ok) submitted = true;
    } catch(e) {}

    showToast('Entry submitted successfully! 🎉', 'success');
    document.getElementById('submitEntryForm').reset();
    if (state.user) {
      document.getElementById('sfName').value  = state.user.name;
      document.getElementById('sfEmail').value = state.user.email;
    }
    btn.disabled = false;
    btn.innerHTML = '<i class="fa-solid fa-paper-plane"></i> Submit My Entry';
    loadStats();
    loadTalents();
  });

  // ─── ADMIN DATA ───────────────────────────────────────────

  async function loadAdminData() {
    let subs = [];
    try {
      const res = await fetch('/api/submissions');
      if (res.ok) subs = await res.json();
    } catch(e) {}

    if (!subs.length) {
      subs = [
        { id:1, talentName:'Priya Sharma', category:'Singing', competitionName:'Voice of the Year', status:'approved' },
        { id:2, talentName:'Arjun Menon',  category:'Dancing', competitionName:'Dance Nation',       status:'pending'  },
        { id:3, talentName:'Rahul Dev',    category:'Comedy',  competitionName:'Stand-Up Open Mic',  status:'pending'  },
        { id:4, talentName:'Sneha Kapoor', category:'Art',     competitionName:'Masters of Art',     status:'approved' },
        { id:5, talentName:'Vikram Singh', category:'Music',   competitionName:'Indie Festival',     status:'rejected' },
      ];
    }
    renderAdminSubmissions(subs);
  }

  function renderAdminSubmissions(subs) {
    const tbody = document.getElementById('adminSubmissionsTable');
    if (!tbody) return;
    if (!subs.length) {
      tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;color:var(--text-muted);padding:24px;">
        <i class="fa-solid fa-inbox"></i> No submissions yet.</td></tr>`;
      return;
    }
    tbody.innerHTML = subs.map(s => {
      const st = (s.status || 'pending').toLowerCase();
      return `
      <tr>
        <td><strong>${s.talentName || s.name || '—'}</strong></td>
        <td>${s.category || '—'}</td>
        <td>${s.competitionName || s.competition || '—'}</td>
        <td><span class="status-pill ${st}">${st}</span></td>
        <td>
          ${st === 'pending'
            ? `<button class="action-btn approve" data-sid="${s.id}">✓ Approve</button>
               <button class="action-btn reject"  data-sid="${s.id}">✗ Reject</button>`
            : `<span style="color:var(--text-muted);font-size:0.8rem;">${st.charAt(0).toUpperCase()+st.slice(1)}</span>`}
        </td>
      </tr>`;
    }).join('');

    tbody.querySelectorAll('.action-btn.approve').forEach(btn => {
      btn.addEventListener('click', async () => {
        try { await fetch(`/api/submissions/${btn.dataset.sid}/approve`, { method: 'POST' }); } catch(e) {}
        showToast('Submission approved!', 'success');
        loadAdminData();
      });
    });
    tbody.querySelectorAll('.action-btn.reject').forEach(btn => {
      btn.addEventListener('click', async () => {
        try { await fetch(`/api/submissions/${btn.dataset.sid}/reject`, { method: 'POST' }); } catch(e) {}
        showToast('Submission rejected.', 'error');
        loadAdminData();
      });
    });
  }

  // ─── SCROLL REVEAL ────────────────────────────────────────

  function initScrollReveal() {
    const obs = new IntersectionObserver((entries) => {
      entries.forEach(e => {
        if (e.isIntersecting) {
          e.target.classList.add('visible');
          obs.unobserve(e.target);
        }
      });
    }, { threshold: 0.08 });

    document.querySelectorAll('.reveal:not(.visible)').forEach(el => obs.observe(el));
  }

  // ─── BOOT ─────────────────────────────────────────────────

  const savedSession = loadSession();

  runPreloader(() => {
    // Preloader done — now decide: show login or resume session
    if (savedSession) {
      // Session exists: skip login, go straight in
      spawnStars('loginStars', 0, 'star'); // skip login stars
      loginScreen.style.display = 'none';
      loginSuccess(savedSession);
    } else {
      // Show login screen
      spawnStars('loginStars', 30, 'star');
      loginScreen.classList.add('visible');
    }
  });

});
