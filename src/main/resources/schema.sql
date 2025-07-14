CREATE TABLE products
(
    id            BIGINT AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    price         INT          NOT NULL,
    imageUrl      VARCHAR(255) NOT NULL,
    kakaoApproval BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);

CREATE TABLE users
(
    memberId BIGINT AUTO_INCREMENT,
    email    VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(10)  NOT NULL DEFAULT 'USER',
    PRIMARY KEY (memberId)
);

CREATE TABLE wishes
(
    wishId    BIGINT AUTO_INCREMENT,
    memberId  BIGINT NOT NULL,
    productId BIGINT NOT NULL,
    FOREIGN KEY (memberId) REFERENCES users (memberId) ON DELETE CASCADE,
    FOREIGN KEY (productId) REFERENCES products (id) ON DELETE CASCADE,
    PRIMARY KEY (wishId),
        UNIQUE (memberId, productId)
);