#!/bin/sh

java -jar target/frsync-0.0.1-SNAPSHOT-jar-with-dependencies.jar  --remote='-e ssh xwx@192.168.1.18:/home/xingwx/test/frsync/' --localpath='/home/xingwx/test/backup' test- 


#java -jar target/frsync-0.0.1-SNAPSHOT-jar-with-dependencies.jar  --remote='-e ssh xwx@llzg.cn:/home/xwx/backup/' --localpath='/home/xwx/svr001/www/webapps'  ssllpro jeecms wpn
