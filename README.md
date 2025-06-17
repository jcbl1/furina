>也就是说，只要把代码上传到Github就能加分吗？有这种好事怎么不早说，这就传！

这是《企业级应用软件设计与开发》课程项目的后端部分代码，基于Spring Boot框架，结构优雅，仿若街头大爷整洁的背心。

## Quick Start

>万一你实在想不开，想要了解冰美式兑可乐会给人带来何种程度的震惊，请按照下面步骤来。

在Docker或本机上搭建好MySQL和Elasticsearch环境。MySQL配置在`application.yml:spring.datasource`下，主要需要根据实际情况修改`url`、`username`和`password`，Elasticsearch的用户名密码在`application-dev.yml:spring.elasticsearch`下

`application-dev.yml:spring.ai.openai`中需替换自己的API接口

根据`{项目根目录}/src/main/resources/db/schema.sql`中的SQL创建数据库表，并执行`{项目根目录}/src/main/resources/db/seed.sql`初始化本地账号

启动项目并传入`--app.import-seed-data=true`，向MySQL中导入数据（导入仅需要一次）

登录后以管理员身份调用`POST /api/v1/ai/index`，向Elasticsearch中导入数据（导入仅需要一次）
