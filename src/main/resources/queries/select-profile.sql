SELECT uuid, flightRemaining, flying
FROM beanstalk_users
WHERE uuid = :uuid;
