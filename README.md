# frsync 每日增量备份数据库文件

mysqldump 数据库的备份文件为完全备份文件。每天远程复制文件需要大量的时间和带宽。
但实际上每天变化的只是数据中的一小部分。在不使用数据库自身的备份机制的前提下，
可以只备份变化的那一小部分么？ 本软件尝试做这件事。


## 本软件的思想是: 第一天是完全备份, 将远程的备份完整地复制到本地。

		time="$(date +"%Y%m%d")"
		MYSQLDUMP="$(which mysqldump)"
		$MYSQLDUMP -u $db_user -h $db_host -p$db_passwd $db | $GZIP -9 --rsyncable > "$backup_dir/$db.$time.gz"

注意：rsyncable选项，这是能增量同步的关键。


## 第二天在本地服务器用前一天的备份文件复制一个假备份， aaa-20160110.gz -> aaa-20160111.gz


## 同本地的假备份与远程的备份文件进行rsync同步

		rsync  user@remoteserver:/backupdir/aaa-20160111.gz  /localbackupdir/aaa-20160111.gz


## 这种备份可能有点用，但更好的可能还是日志增量备份，而不是这种技巧


## jar包编译
      mvn assembly:assembly
      
## 脚本运行, 服务器端
  
		rsync --daemon  
 
rsync以服务的形式监听873端口
服务器端配置文件/etc/rsyncd.conf

		uid = nobody
		gid = nobody
		use chroot = no
		max connections = 4
		pid file =/var/run/rsyncd.pid
		lock file =/var/run/rsync.lock
		log file =/var/log/rsyncd.log
		
		[data]
		path = /home/data/
		ignore errors
		read only = true
		list = false	
		hosts allow = 12.34.56.78
		hosts deny = 0.0.0.0/32
		auth users = backup
		secrets file = /etc/backserver.pas

 /etc/backserver.pas为明文密码文件，权限应为600, 里面为文本格式：
 
	 		username1:password1
	 		username2:password2
 		

客户端以指定密码文件的方式，可以免输入密码

		#!/bin/sh

		server_dir=/data
		local_dir=/home/xwx/svr001/www/data
		JAVA=$(which java)
		$JAVA -jar frsync-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
		       	--remote="--password-file rsync.pass  rsync://backup@llzg.cn:${server_dir}" \
			--localpath="${local_dir}" ssllpro. jeecms_2012. wpn.

rsync.pass 明文保存对应服务器端指定的密码,权限也必须为600，这里用户名是backup


