To build docker images:
 $ gradle buildDocker

To push docker image to repo:
 $ docker push awzurn/spac-fusion-api:latest/<TAG>

To run this locally or in dev (on host platform). This will boot up the latest image and a postgres instance.
  $ docker-compose up (with file for wanted env in current directory).

Liquibase will also need to be run against that envs db.

