# API 参考

基础路径：`/api/v1`

除公开接口外，请求需要携带：

```http
Authorization: Bearer <token>
```

所有响应均为 `ApiResponse`：

```json
{
  "code": 0,
  "message": "OK",
  "data": {}
}
```

## 认证

### POST /auth/login

公开接口。

```json
{
  "username": "admin",
  "password": "password"
}
```

响应：

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "token_type": "Bearer",
    "expires_in": 86400000,
    "user": {
      "id": 1,
      "username": "admin",
      "role": "ADMIN"
    }
  }
}
```

错误用户名或密码返回 `401`。

### GET /auth/me

返回当前登录用户。

### POST /auth/validate

公开接口，用于前端启动时判断本地 token 是否仍然有效。

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

响应 `data` 为布尔值。

### POST /auth/logout

需要登录。请求头中的 Bearer token 会被加入黑名单。

## 系统

### GET /system/health

公开接口。

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "status": "UP",
    "application": "furina",
    "time": "2026-11-04T12:00:00Z"
  }
}
```

## 餐饮目录

### GET /foods

查询参数：

| 参数 | 默认值 | 说明 |
| --- | --- | --- |
| `keyword` | 无 | 匹配标题或精选评论 |
| `page` | 1 | 页码，从 1 开始 |
| `size` | 10 | 每页数量，1–100 |
| `sort` | id | `id`、`title`、`rating`、`salesMonthly`、`averageCost`、`deliveryTime` |
| `direction` | desc | `asc` / `desc` |

响应：

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "items": [
      {
        "id": 1,
        "thumbnail_path": "images/thumbnails/09eca573",
        "title": "子固路拌粉",
        "rating": 4.8,
        "sales_monthly": 1000,
        "average_cost": 20.00,
        "delivery_time": 48,
        "featured_comment": "都吃完了"
      }
    ],
    "total": 1,
    "page": 1,
    "size": 10,
    "pages": 1
  }
}
```

### GET /foods/{id}

返回店铺详情及菜单：

```json
{
  "id": 1,
  "thumbnail_path": "images/thumbnails/09eca573",
  "title": "子固路拌粉",
  "rating": 4.8,
  "sales_monthly": 1000,
  "average_cost": 20.00,
  "delivery_time": 48,
  "featured_comment": "都吃完了",
  "items": [
    {
      "id": 10,
      "name": "肉酱拌粉",
      "description": "一钵子粉,两勺子肉酱",
      "price": 12.80
    }
  ]
}
```

店铺不存在返回 `404`。

### POST /foods

需要 `ADMIN`。创建店铺及其菜单：

```json
{
  "title": "新店",
  "thumbnail_path": "images/thumbnails/new",
  "rating": 4.7,
  "sales_monthly": 100,
  "average_cost": 22.50,
  "delivery_time": 35,
  "featured_comment": "好吃",
  "items": [
    {
      "name": "招牌饭",
      "description": "每日现做",
      "price": 18.00
    }
  ]
}
```

成功返回 `201`。

### POST /foods/batch

需要 `ADMIN`。请求体是上面的对象数组，返回：

```json
{
  "inserted": 42
}
```

### PUT /foods/{id}

需要 `ADMIN`。请求体与创建相同，但不包含 `items`。

### DELETE /foods/{id}

需要 `ADMIN`。关联菜单项由数据库外键级联删除。

### POST /foods/{id}/items

需要 `ADMIN`。

```json
{
  "name": "新菜品",
  "description": "可选",
  "price": 9.90
}
```

### PUT /foods/{id}/items/{itemId}

需要 `ADMIN`。只有属于该店铺的菜单项才能被修改。

### DELETE /foods/{id}/items/{itemId}

需要 `ADMIN`。

## 文件

### GET /files/thumbnails/{fileName}

公开接口。只允许读取 `file-storage.root-dir/images/thumbnails` 下的文件，响应带一天缓存。

## AI

### POST /ai/chat

需要登录。

```json
{
  "session_id": "optional-session-id",
  "message": "推荐一份 30 元以内的午饭"
}
```

`session_id` 为空时服务端生成 UUID。响应：

```json
{
  "session_id": "0f7e...",
  "message": "根据你的预算，推荐……",
  "time": "2026-11-04T12:00:00Z"
}
```

### DELETE /ai/chat/{sessionId}

清空指定会话的上下文。

### POST /ai/search

需要登录。直接调用向量检索。

```json
{
  "query": "牛肉面",
  "top_k": 5,
  "filters": [
    {
      "field": "rating",
      "operator": "GTE",
      "value": "4.5"
    },
    {
      "field": "deliveryTime",
      "operator": "LTE",
      "value": "40"
    }
  ]
}
```

支持的 operator：`EQ`、`NE`、`GT`、`GTE`、`LT`、`LTE`、`IN`、`NIN`。

响应：

```json
{
  "code": 0,
  "message": "OK",
  "data": [
    {
      "id": "1",
      "text": "{\"title\":\"子固路拌粉\",\"items\":[...]}",
      "metadata": {
        "rating": 4.8,
        "salesMonthly": 1000,
        "averageCost": 20.00,
        "deliveryTime": 48
      },
      "score": 0.91
    }
  ]
}
```

### POST /ai/index

需要 `ADMIN`。从 MySQL 重建 Elasticsearch 索引，返回索引的店铺数量。

## 订单

### POST /orders

需要登录。`restaurant_id` 和 `item_id` 必须是目录中真实存在的组合。

```json
{
  "appointment": {
    "immediate": false,
    "minutes_from_now": 45
  },
  "restaurants": [
    {
      "restaurant_id": 1,
      "items": [
        {
          "item_id": 10,
          "quantity": 2,
          "note": "不要香菜"
        }
      ]
    }
  ]
}
```

响应 `201`：

```json
{
  "order_no": "FUR202611041200001A2B3C",
  "status": "PLACED",
  "total_amount": 25.60,
  "eta": "2026-11-04T12:45:00",
  "created_at": "2026-11-04T12:00:00",
  "items": [
    {
      "restaurant_id": 1,
      "item_id": 10,
      "name": "肉酱拌粉",
      "quantity": 2,
      "unit_price": 12.80,
      "line_total": 25.60,
      "note": "不要香菜"
    }
  ]
}
```

### GET /orders

需要登录。查询当前账号的订单，支持 `page` 和 `size`（1–100）。

### GET /orders/{orderNo}

需要登录。只能查看自己的订单，其他账号的订单返回 `404`。

## 错误码

| HTTP | 场景 |
| --- | --- |
| 400 | 参数校验失败、请求体格式错误、非法过滤表达式 |
| 401 | 未登录、token 无效或已注销 |
| 403 | 角色不足 |
| 404 | 店铺、菜单项、订单或文件不存在 |
| 409 | 业务冲突（保留） |
| 500 | 未预期异常 |
