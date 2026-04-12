-- USERS
INSERT INTO users (email, name, password, role) VALUES
                                                    ('admin@admin.com', 'admin', '$2a$10$5Y81ar6ehID9yMjyHm9uHubaua7n/tYq0pZKUyx3h8MN.6LXfhA8u', 'ADMIN'),
                                                    ('user1@test.com', 'Alice In Finland', 'pass1', 'USER'),
                                                    ('user3@test.com', 'Alice', 'pass1', 'USER'),
                                                    ('user2@test.com', 'Bob', 'pass2', 'USER');

---------------------------------------------------------------------

-- CATEGORIES
INSERT INTO categories (name) VALUES
                                  ('IT'),
                                  ('Sport'),
                                  ('Music'),
                                  ('Education');

---------------------------------------------------------------------

-- EVENTS
INSERT INTO events (
    annotation,
    category_id,
    description,
    event_date,
    initiator_id,
    lat,
    lon,
    paid,
    participant_limit,
    published_on,
    state,
    title,
    views
) VALUES
      (
          'Java meetup',
          1,
          'Spring Boot backend meetup',
          CURRENT_TIMESTAMP + INTERVAL '7 day',
          1,
          51.1605,
          71.4704,
          false,
          50,
          CURRENT_TIMESTAMP,
          'PUBLISHED',
          'Java Backend Meetup',
          120
      ),
      (
          'Morning run',
          2,
          '5km friendly run',
          CURRENT_TIMESTAMP + INTERVAL '3 day',
          1,
          51.15,
          71.44,
          false,
          0,
          CURRENT_TIMESTAMP,
          'PUBLISHED',
          'Park Running',
          45
      ),
      (
          'Guitar jam',
          3,
          'Live jam session',
          CURRENT_TIMESTAMP + INTERVAL '10 day',
          1,
          51.17,
          71.43,
          true,
          20,
          NULL,
          'PENDING',
          'Evening Jam',
          0
      ),
      (
          'Math lecture',
          4,
          'Numerical methods lecture',
          CURRENT_TIMESTAMP + INTERVAL '5 day',
          1,
          51.18,
          71.41,
          false,
          30,
          CURRENT_TIMESTAMP,
          'PUBLISHED',
          'Numerical Methods',
          78
      ),
      (
          'Canceled event',
          1,
          'Canceled test',
          CURRENT_TIMESTAMP + INTERVAL '2 day',
          2,
          51.14,
          71.45,
          false,
          10,
          CURRENT_TIMESTAMP,
          'CANCELED',
          'Canceled Meetup',
          12
      );

---------------------------------------------------------------------

-- REQUESTS
INSERT INTO requests (event_id, requester_id, status) VALUES
                                                          (1, 3, 'CONFIRMED'),
                                                          (2, 2, 'CONFIRMED'),
                                                          (4, 2, 'PENDING'),
                                                          (1, 2, 'REJECTED');

---------------------------------------------------------------------

-- COMPILATIONS
INSERT INTO compilations (pinned, title) VALUES
                                             (true, 'Popular events'),
                                             (false, 'Sport & Education');

---------------------------------------------------------------------

-- COMPILATION EVENTS
INSERT INTO compilation_events (compilation_id, event_id) VALUES
                                                              (1, 1),
                                                              (1, 4),
                                                              (2, 2),
                                                              (2, 3);

---------------------------------------------------------------------

-- EVENT REVIEWS
INSERT INTO event_reviews (event_id, comment, status) VALUES
                                                          (1, 'Great meetup, learned a lot!', 'APPROVED'),
                                                          (2, 'Nice run, friendly people', 'APPROVED'),
                                                          (4, 'Good explanation of Newton method', 'APPROVED'),
                                                          (3, 'Waiting for publication', 'RETURNED');
