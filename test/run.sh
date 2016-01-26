#!/bin/sh

server_dir=$(pwd)
local_dir=$(pwd)
mkdir -p $server_dir/test/data
mkdir -p $local_dir/test/
dd if=/dev/urandom count=100  of=$server_dir/test/data/test-20160120.gz
dd if=/dev/urandom count=100  of=$server_dir/test/backup/test-20160117.gz
dd if=/dev/urandom count=100  of=$server_dir/test/backup/test-20160119.gz

java -jar target/frsync-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
       	--remote="-e ssh xwx@127.0.0.1:${server_dir}/test/data" \
	--localpath="${local_dir}/test/backup" test-


#java -jar target/frsync-0.0.1-SNAPSHOT-jar-with-dependencies.jar  --remote='-e ssh xwx@llzg.cn:/home/xwx/backup/' --localpath='/home/xwx/svr001/www/webapps'  ssllpro jeecms wpn
