# 架构说明

## 分层职责

### Controller

- 只处理 HTTP 语义：路径、查询参数、请求体、状态码。
- 使用 `@Valid` / `@Validated` 做边界校验。
- 不访问 Mapper，不包含事务和业务计算。
- 统一返回 `ApiResponse<T>`，分页返回 `PageResponse<T>`。

### Service

- 承载业务规则、事务边界和跨表操作。
- 接口位于 `service`，实现位于 `service.impl`。
- 对外抛 `ApiException`，由 `GlobalExceptionHandler` 转换为统一错误响应。
- `OrderServiceImpl` 是订单价格的唯一来源，HTTP 接口和 AI 工具共用同一实现。

### Mapper / Entity

- `entity` 只表达数据库结构，不承担 API 序列化职责。
- `mapper` 继承 MyBatis-Plus `BaseMapper`，复杂查询通过 Lambda Wrapper 完成。
- DTO 与 Entity 的转换放在 DTO 的静态工厂或 Service 中，避免实体泄露到 API。

## 统一响应与异常

`ApiResponse` 的 `code` 为 `0` 表示成功；失败时使用 HTTP 状态码作为业务码。
`GlobalExceptionHandler` 覆盖：

- `ApiException`：业务异常（404、409、400 等）
- 参数校验异常：返回字段到错误信息的映射
- 认证 / 授权异常：401 / 403
- 请求体不可读、参数类型错误：400
- 未预期异常：500，并记录完整日志

## 安全

- 无状态会话，`SessionCreationPolicy.STATELESS`。
- 登录由 `AuthenticationManager` + `DaoAuthenticationProvider` 完成，密码使用 BCrypt。
- 账号来自 `account` 表，`CustomUserDetailsService` 返回 `AccountPrincipal`。
- `JwtAuthenticationFilter` 从 `Authorization: Bearer` 读取 token，验证签名和过期时间，并检查内存黑名单。
- 角色规则在 `SecurityConfig` 中集中声明：目录写操作与索引重建要求 `ADMIN`。
- 默认放行：登录、token 校验、健康检查、缩略图、`/error`。
- 注销接口把 token 放入 `TokenBlacklistService`，直到 token 自然过期。

## AI 助手

- `AiConfig` 构建 `ChatClient`，加载 `prompts/food-assistant.st` 系统提示词。
- `MessageChatMemoryAdvisor` + `InMemoryChatMemory` 保存会话上下文。
- 每个请求通过 `chat_memory_conversation_id` 参数指定会话；未提供时生成 UUID。
- `AiTools` 向模型暴露三个工具：目录检索、下单、当前时间。
- `FoodSearchService` 通过 `VectorStore` 检索，支持 `EQ/NE/GT/GTE/LT/LTE/IN/NIN` 过滤表达式，多个条件按 AND 组合。
- `FoodIndexService` 将店铺和菜单压缩成文档写入向量库，metadata 保留评分、销量、客单价和配送时间，供过滤使用。

## 订单

- `OrderServiceImpl` 按 `restaurant_id` 和 `item_id` 回查目录，拒绝不存在的组合。
- 行小计和订单总额使用 `BigDecimal`，保留两位小数。
- 订单行保存 `item_name` 和 `unit_price` 快照，菜单后续变更不影响历史订单。
- `immediate=true` 时预计 30 分钟送达；否则使用 `minutes_from_now`。
- 订单号格式：`FUR` + `yyyyMMddHHmmss` + 6 位随机字符。

## 文件服务

- 根目录由 `file-storage.root-dir` 配置。
- 只允许访问 `images/thumbnails` 下的文件。
- 路径先 `normalize`，再校验 `startsWith`，阻止 `..` 目录穿越。
- 响应带一天公共缓存。

## 配置

- 所有环境相关值通过 `${ENV:default}` 注入。
- `JwtProperties`、`CorsProperties`、`FileStorageProperties` 使用 `@ConfigurationProperties` 绑定。
- `application-dev.yml` 只放开发环境默认值，不提交真实密钥。
