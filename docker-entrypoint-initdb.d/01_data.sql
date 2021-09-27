INSERT INTO users(id, username, password)
VALUES (1, 'viktor', '$argon2id$v=19$m=4096,t=3,p=1$cZmmNEPadSCpl/vypNTYtw$vEcSuyqYwtGuSilrvztCwQp/aU/rTO2dOwlkEgTtZW8'),
       (2, 'sergey', '$argon2id$v=19$m=4096,t=3,p=1$2qCTq31yTA7jzDfVHZkTkg$/VfyjdrAxb7t576okBuUgUW5iy0jHYRQbnyESYAC6Bg');

ALTER SEQUENCE users_id_seq RESTART WITH 3;

INSERT INTO tokens(token, "userId")
VALUES ('6NSb+2kcdKF44ut4iBu+dm6YLu6pakWapvxHtxqaPgMr5iRhox/HlhBerAZMILPjwnRtXms+zDfVTLCsao9nuw==', 1),
       ('0Y0kjj2tQTexoPYi4OdcTeVQK/94SJgl+AUuRCX4O0jGc/Nq4RoYO0xn3/fXw6Js7X2s3dqaizvi0coM9LVehg==', 2);


INSERT INTO cards(id, "ownerId", number, balance)
VALUES (1, 1, '**** *888', 50000),
       (2, 2, '**** *999', 90000);

ALTER SEQUENCE cards_id_seq RESTART WITH 3;

INSERT INTO roles(id, role)
VALUES (1, 'ROLE_ADMIN'),
       (2, 'ROLE_USER')
