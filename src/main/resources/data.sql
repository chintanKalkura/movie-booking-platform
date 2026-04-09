-- Movies
INSERT INTO movies (id, name, category, language, rating) VALUES
    ('aaaaaaaa-0000-0000-0000-000000000001', 'Inception',        'Sci-Fi Thriller', 'English', 'UA'),
    ('aaaaaaaa-0000-0000-0000-000000000002', 'The Dark Knight',  'Action',          'English', 'UA'),
    ('aaaaaaaa-0000-0000-0000-000000000003', 'Interstellar',     'Sci-Fi',          'English', 'U'),
    ('aaaaaaaa-0000-0000-0000-000000000004', 'Oppenheimer',      'Historical Drama','English', 'A');

-- Theatres
INSERT INTO theatres (id, name, address) VALUES
    ('bbbbbbbb-0000-0000-0000-000000000001', 'PVR Cinemas',    '123 MG Road, Bengaluru, Karnataka'),
    ('bbbbbbbb-0000-0000-0000-000000000002', 'INOX Megaplex',  '456 Linking Road, Mumbai, Maharashtra');

-- Prices
INSERT INTO prices (id, cost, offers) VALUES
    ('cccccccc-0000-0000-0000-000000000001', 350.00, '10% off on HDFC Cards,Buy 2 Get 1 Free on Tuesdays'),
    ('cccccccc-0000-0000-0000-000000000002', 550.00, 'IMAX Weekend Special,Complimentary Popcorn'),
    ('cccccccc-0000-0000-0000-000000000003', 250.00, 'Student Discount 15%');

-- Shows
INSERT INTO shows (id, movie_name, movie_id, theatre_id, price_id, screen_type, show_time, show_date, show_status) VALUES
    ('dddddddd-0000-0000-0000-000000000001', 'Inception',       'aaaaaaaa-0000-0000-0000-000000000001', 'bbbbbbbb-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000002', 'IMAX',      '10:30:00', '2026-04-10', 'FILLING_FAST'),
    ('dddddddd-0000-0000-0000-000000000002', 'Inception',       'aaaaaaaa-0000-0000-0000-000000000001', 'bbbbbbbb-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000001', 'SCREEN_3D', '15:00:00', '2026-04-10', 'EMPTY'),
    ('dddddddd-0000-0000-0000-000000000003', 'The Dark Knight', 'aaaaaaaa-0000-0000-0000-000000000002', 'bbbbbbbb-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000001', 'SCREEN_2D', '13:00:00', '2026-04-10', 'FILLING_FAST'),
    ('dddddddd-0000-0000-0000-000000000004', 'The Dark Knight', 'aaaaaaaa-0000-0000-0000-000000000002', 'bbbbbbbb-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000002', 'IMAX',      '19:00:00', '2026-04-10', 'FEW_SEATS_REMAINING'),
    ('dddddddd-0000-0000-0000-000000000005', 'Interstellar',    'aaaaaaaa-0000-0000-0000-000000000003', 'bbbbbbbb-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000003', 'SCREEN_2D', '11:30:00', '2026-04-10', 'EMPTY'),
    ('dddddddd-0000-0000-0000-000000000006', 'Interstellar',    'aaaaaaaa-0000-0000-0000-000000000003', 'bbbbbbbb-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000001', 'FOUR_DX',   '18:30:00', '2026-04-10', 'FILLING_FAST'),
    ('dddddddd-0000-0000-0000-000000000007', 'Oppenheimer',     'aaaaaaaa-0000-0000-0000-000000000004', 'bbbbbbbb-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000002', 'IMAX',      '14:30:00', '2026-04-10', 'EMPTY'),
    ('dddddddd-0000-0000-0000-000000000008', 'Oppenheimer',     'aaaaaaaa-0000-0000-0000-000000000004', 'bbbbbbbb-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000003', 'SCREEN_2D', '21:00:00', '2026-04-10', 'FEW_SEATS_REMAINING');
