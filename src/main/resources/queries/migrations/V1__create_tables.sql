CREATE TABLE beanstalk_users
(
    uuid CHAR(36) NOT NULL,
    flightRemaining DOUBLE,
    flying TINYINT(1),

    PRIMARY KEY (uuid)
);
