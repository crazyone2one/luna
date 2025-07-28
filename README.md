~~~
cd /home/app/luna
docker build -t luna:1.0 .
docker run -d -p 18086:8086 --name luna -v /home/app/ftp:/app/ftp luna:1.0
~~~