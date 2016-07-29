To build docker images:
 $ gradle buildDocker

To push docker image to repo:
 $ gradle ???

To run docker images created from this app:
 $ docker run -d -p 8080:8080 --net=host <images_name>

