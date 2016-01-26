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
