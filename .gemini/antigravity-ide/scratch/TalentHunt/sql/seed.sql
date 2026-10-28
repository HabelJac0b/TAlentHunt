-- =========================================================
-- Talent Hunt System Initial Data Seed
-- =========================================================

-- Clear existing data (order matters for Foreign Keys)
DELETE FROM votes;
DELETE FROM evaluations;
DELETE FROM talent_submissions;
DELETE FROM competitions;
DELETE FROM categories;
DELETE FROM users;

-- 1. Insert Users
INSERT INTO users (id, name, email, password, role, avatar_url, bio) VALUES
(1, 'Alex Rivera', 'alex@talenthunt.com', 'user123', 'CONTESTANT', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80', 'Passionate acoustic guitarist & vocalist from Seattle.'),
(2, 'Elena Rostova', 'elena@talenthunt.com', 'user123', 'CONTESTANT', 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=300&q=80', 'Contemporary classical dancer blending ballet and acrobatics.'),
(3, 'Marcus Vance', 'marcus@talenthunt.com', 'user123', 'CONTESTANT', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=300&q=80', 'Full-stack AI developer creating generative audio art.'),
(4, 'Sophia Chen', 'sophia@talenthunt.com', 'user123', 'CONTESTANT', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=300&q=80', 'Digital 3D artist and visual effects designer.'),
(5, 'David Kim', 'david@talenthunt.com', 'user123', 'CONTESTANT', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=300&q=80', 'Close-up sleight-of-hand illusionist.'),
(6, 'Maestro Simon Drake', 'simon.drake@judges.com', 'judge123', 'JUDGE', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=300&q=80', 'Grammy-nominated producer and head judge for music.'),
(7, 'Sarah Jenkins', 'sarah.j@judges.com', 'judge123', 'JUDGE', 'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=300&q=80', 'Broadway choreographer and performing arts director.'),
(8, 'Admin SuperUser', 'admin@talenthunt.com', 'admin123', 'ADMIN', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=300&q=80', 'System Administrator & Talent Hunt Director.'),
(9, 'Liam O''Connor', 'liam@audience.com', 'user123', 'AUDIENCE', 'https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=300&q=80', 'Avid performing arts enthusiast.'),
(10, 'Habel Jacob (26MCAB25)', 'habel@webcraft.com', 'user123', 'CUSTOMER', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80', 'Lead Presenter on HTML5 & JavaScript (ID: 26MCAB25).'),
(11, 'Customer Member', 'customer@webcraft.com', 'user123', 'CUSTOMER', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=300&q=80', 'Enrolled student exploring interactive web technologies.'),
(12, 'Admin Supervisor', 'admin@webcraft.com', 'admin123', 'ADMIN', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=300&q=80', 'Course Supervisor & WebCraft Administrator.');

-- 2. Insert Categories
INSERT INTO categories (id, name, icon, description) VALUES
(1, 'Vocal & Music', 'fa-music', 'Solo singers, bands, instrumentalists, beatboxers, and songwriters.'),
(2, 'Dance & Movement', 'fa-child-reaching', 'Contemporary, hip-hop, classical ballet, salsa, and freestyle dance.'),
(3, 'Digital Art & VFX', 'fa-palette', '3D animation, concept art, visual effects, and graphic motion design.'),
(4, 'Code & Tech Innovation', 'fa-code', 'Interactive creative coding, generative artwork, and AI projects.'),
(5, 'Magic & Illusion', 'fa-wand-magic-sparkles', 'Close-up sleight of hand, mentalism, and stage illusions.'),
(6, 'Acting & Drama', 'fa-masks-theater', 'Monologues, theatrical scenes, voiceover performances, and comedy.');

-- 3. Insert Competitions
INSERT INTO competitions (id, title, category_id, description, prize, status, start_date, end_date) VALUES
(1, 'Global Acoustic & Vocal Showcase 2026', 1, 'Showcase your soulful vocal performances and acoustic instrumentals.', '$10,000 + Studio Recording Deal', 'ACTIVE', '2026-08-01', '2026-09-30'),
(2, 'Urban & Expressive Dance Masters', 2, 'High-energy choreography and soul-stirring solo movement.', '$7,500 Prize Pool', 'ACTIVE', '2026-08-10', '2026-10-15'),
(3, 'Creative Tech & AI Symphony', 4, 'Innovators building audio-visual AI magic and interactive web experiences.', '$12,000 + Tech Grant', 'ACTIVE', '2026-08-15', '2026-10-30'),
(4, 'Next-Gen Visual Effects Challenge', 3, 'Pushing the boundaries of 3D modeling, lighting, and animation.', '$5,000 Gear Bundle', 'UPCOMING', '2026-09-15', '2026-11-15');

-- 4. Insert Talent Submissions
INSERT INTO talent_submissions (id, title, description, user_id, competition_id, category_id, media_type, media_url, thumbnail_url, views, votes_count, average_score, status) VALUES
(1, 'Midnight Resonance - Original Acoustic Performance', 'An emotional acoustic fingerstyle arrangement paired with raw vocal harmonies recorded live in studio.', 1, 1, 1, 'video', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4', 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=600&q=80', 1420, 245, 9.4, 'APPROVED'),
(2, 'Gravity-Defying Contemporary Solo', 'A powerful dance piece exploring themes of momentum, balance, and liberation set to orchestral strings.', 2, 2, 1, 'video', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4', 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?auto=format&fit=crop&w=600&q=80', 980, 182, 9.1, 'APPROVED'),
(3, 'Neural Harmony - AI Generative Synth Orchestra', 'A real-time Java application converting hand motion camera inputs into rich ambient synth harmonies.', 3, 3, 4, 'code', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4', 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=600&q=80', 2150, 310, 9.7, 'APPROVED'),
(4, 'Cybernetic Metropolis 3D Visual Concept', 'Full 3D scene environment built with procedural shaders and volumetric lighting effects.', 4, 4, 3, 'image', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4', 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=600&q=80', 740, 115, 8.8, 'APPROVED'),
(5, 'Shadows & Mirrors - Impossible Card Illusion', 'An uncut 3-minute sleight of hand routine defying visual logic.', 5, 1, 5, 'video', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4', 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=600&q=80', 1600, 204, 9.2, 'APPROVED');

-- 5. Insert Evaluations (Judges)
INSERT INTO evaluations (submission_id, judge_id, score_technical, score_creativity, score_presentation, total_score, feedback) VALUES
(1, 6, 9, 10, 9, 9.3, 'Breathtaking vocal control and pristine acoustic timbre. Outstanding dynamic range.'),
(1, 7, 9, 9, 10, 9.5, 'Captivating stage presence even in an intimate acoustic setting. Pure artistry.'),
(3, 6, 10, 10, 9, 9.7, 'Mind-blowing technical execution! The seamless synthesis of algorithm and emotion is revolutionary.'),
(2, 7, 9, 9, 9, 9.0, 'Fluid posture and impressive muscular control during dynamic transitions.');

-- 6. Insert Votes
INSERT INTO votes (submission_id, user_id) VALUES
(1, 9),
(1, 2),
(1, 3),
(3, 9),
(3, 1),
(3, 4),
(2, 9),
(2, 5);
