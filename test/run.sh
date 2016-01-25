#!/bin/sh

java -jar target/frsync-0.0.1-SNAPSHOT-jar-with-dependencies.jar  --remote='-e ssh xwx@llzg.cn:/home/xwx/backup/' --localpath='/home/xwx/svr001/www/webapps'  ssllpro jeecms wpn
