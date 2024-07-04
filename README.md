Lealone 高度兼容 PostgreSQL 的协议和 SQL 语法，可以使用 PostgreSQL 的各种客户端访问 Lealone。


## 编译运行需要

* [JDK 1.8+](https://www.oracle.com/java/technologies/downloads/)

* Maven 3.8+

* PostgreSQL 版本支持 9.x 和 14.x 系列


## 打包插件

运行 `mvn clean package -Dmaven.test.skip=true`

生成 jar 包 `target\lealone-postgresql-plugin-6.0.0.jar`

假设 jar 包的绝对路径是 `E:\lealone\lealone-plugins\postgresql\target\lealone-postgresql-plugin-6.0.0.jar`

也可以直接下载插件 [lealone-postgresql-plugin-6.0.0.jar](https://github.com/lealone-plugins/.github/releases/download/lealone-plugins-6.0.0/lealone-postgresql-plugin-6.0.0.jar)


## 下载 Lealone

[lealone-6.0.0.jar](https://github.com/lealone/Lealone/releases/download/lealone-6.0.0/lealone-6.0.0.jar)

Lealone 只有一个 jar 包，下载下来之后随意放到一个目录即可

也可以从源代码构建最新版本，请阅读文档: [从源码构建 Lealone](https://github.com/lealone/Lealone-Docs/blob/master/%E5%BA%94%E7%94%A8%E6%96%87%E6%A1%A3/%E4%BB%8E%E6%BA%90%E7%A0%81%E6%9E%84%E5%BB%BALealone.md)


## 启动 Lealone 数据库

打开一个新的命令行窗口，运行: `java -jar lealone-6.0.0.jar`

```java
Lealone version: 6.0.0
Use default config
Base dir: ./lealone_data
Init storage engines: 5 ms
Init transaction engines: 46 ms
Init sql engines: 4 ms
Init protocol server engines: 13 ms
Init lealone database: 119 ms
TcpServer started, host: 127.0.0.1, port: 9210
Total time: 207 ms (Load config: 2 ms, Init: 201 ms, Start: 4 ms)
Exit with Ctrl+C
```

要停止 Lealone，直接按 Ctrl + C


## 运行插件

打开一个新的命令行窗口，运行: `java -jar lealone-6.0.0.jar -client`

然后执行以下命令创建并启动插件：

```sql
create plugin postgresql
  implement by 'com.lealone.plugins.postgresql.PgPlugin' 
  class path 'E:\lealone\lealone-plugins\postgresql\target\lealone-postgresql-plugin-6.0.0.jar'
  --端口号默认就是5432，如果被其他进程占用了可以改成别的
  parameters (port=5432);
 
start plugin postgresql;
```

要 stop 和 drop 插件可以执行以下命令：

```sql
stop plugin postgresql;

drop plugin postgresql;
```

执行 stop plugin 只是把插件对应的服务停掉，可以再次通过执行 start plugin 启动插件

执行 drop plugin 会把插件占用的内存资源都释放掉，需要再次执行 create plugin 才能重新启动插件


## 用 PostgreSQL 客户端访问 Lealone 数据库

执行以下命令启动 PostgreSQL 客户端:

`psql -h 127.0.0.1 -p 5432 -U postgres -W`

提示口令时输入: postgres

```sql
口令:
psql (14.0, 服务器 8.2.23)
输入 "help" 来获取帮助信息.

postgres=> create table if not exists pet(name varchar(20), age int);
UPDATE 0
postgres=> insert into pet values('pet1', 2);
CommandInterfaceINSERT 0 1
postgres=> select count(*) from pet;
 count(*)
----------
        1
(1 行记录)

postgres=>
```
