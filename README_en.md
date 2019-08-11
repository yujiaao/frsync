`Frsync` daily incremental backup database file
The backup file of the mysqldump database is a full backup file. Copying files remotely every day requires a lot of time and bandwidth. But in reality, what changes every day is only a small part of the data. Under the premise of not using the database's own backup mechanism, can you only back up the small part of the change? The software tries to do this.

The idea of this software is: The first day is a full backup, copying the remote backup completely to the local.
Backup script
```
#!/bin/bash
time="$(date +"%Y%m%d")"
MYSQLDUMP="$(which mysqldump)"
$MYSQLDUMP -u $db_user -h $db_host -p$db_passwd $db | $GZIP -9 --rsyncable > "$backup_dir/$db.$time.gz"
```
Note: The rsyncable option, which is the key to incremental synchronization.

The previous day's file is the backup basis
The next day, the local server copies a fake backup with the backup file of the previous day.
```
cp  aaa-20160110.gz  aaa-20160111.gz
```
Or use the script below
```
#!/bin/bash
time="$(date +"%Y%m%d")"
time_lastday="$(date --date='1 days ago' +"%Y%m%d")"


for db in `cat /home/user1/scripts/list.txt`
do
    /bin/cp  "$backup_dir/$db.$time_lastday.gz"  "$backup_dir/$db.$time.gz"
done
```
The above operations are automatically implemented in this program .

Synchronize local fake backups with remote backup files
rsync  user@remoteserver:/backupdir/aaa-20160111.gz  /localbackupdir/aaa-20160111.gz
This kind of backup might be a bit useful, but it might be better to log incremental backups instead of this trick.

Jar package compilation
```
  mvn assembly:assembly
```  
Script running, server side
```
	rsync --daemon  
```  
Rsync listens to the 873 port server configuration file /etc/rsyncd.conf as a service
```
Uid = nobody
 gid = nobody 
use chroot = no 
max connections = 4 
pid file =/var/run/rsyncd.pid 
lock file =/var/run/rsync.lock 
log file =/var/log/rsyncd.log [data] Path = /home/data/ 
ignore errors 
read only = true
 list = false	 
hosts allow = 12.34.56.78 
hosts deny = 0.0.0.0/32 
auth users = backup 
secrets file = /etc/backserver.pas
```

`/etc/backserver.pas`For a plain text password file, the permission should be 600, which is in text format:
```
username1:password1
username2:password2
```
Client script
By specifying the password file, you can avoid entering the password. Finally, you can run the following script every day. The java program in the jar will automatically find the latest backup file in the local directory of the backup machine, and start the synchronization with the latest file as the blueprint.
```
#!/bin/sh
server_dir=/data
local_dir=/home/data
JAVA=$(which java)
$JAVA -jar frsync-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
		--remote="--password-file rsync.pass  rsync://username@myserver.com:${server_dir}" \
	--localpath="${local_dir}" db1. db2. db3.
 ```
`rsync.pass` The plain text saves the password specified by the server. The password must also be 600. The username is username.

The actual measurement shows that a backup file of about 1G, if the increment is only a few M, it takes only a few seconds to complete the remote backup.

The code is open source, welcome to star, fork

[Translated by Google Translate]
