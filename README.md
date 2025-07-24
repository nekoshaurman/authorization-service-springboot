## Сервис регистрации и авторизации пользователей на SpringBoot

Сервис позволяет регистрироваться, логиниться и получать токены (основной и рефреш), 
пользователи с ролью ADMIN могут изменять роли других пользователей, механизм отзыва токенов,
обновления токенов, проверка роли пользователя по токену.

Имеется проблема с нахождением функционала в одном сервисе, нужно будет разделить.

Использует 3 таблицы базы данных: users, users_roles, revoked_tokens.

Есть рабочий swagger-ui: http://localhost:8080/swagger-ui/index.html

## Endpoints

### register
**URL:** /auth/register  
**Method:** POST

**Params:**
- `login` (String): Логин нового пользователя.
- `email` (String): Электронная почта нового пользователя.
- `password` (String): Пароль нового пользователя.

**Example:**
```bash
POST http://localhost:8080/auth/register?login=testuser&email=test@example.com&password=password123
```

### login
**URL:** /auth/login  
**Method:** POST

**Params:**
- `login` (String): Логин пользователя.
- `password` (String): Пароль пользователя.

**Example:**
```bash
POST http://localhost:8080/auth/login?login=testuser&password=password123
```

### refresh
**URL:** /auth/refresh  
**Method:** POST

**Params:**
- `refreshToken` (String): Refresh токен пользователя.

**Example:**
```bash
POST http://localhost:8080/auth/refresh?refreshToken=your_refresh_token
```

### revoke
**URL:** /auth/revoke  
**Method:** POST

**Params:**
- `token` (String): JWT токен (можно использовать как `accessToken`, так и `refreshToken`).

**Example:**
```bash
POST http://localhost:8080/auth/revoke?token=your_token
```

### debug-role
**Это чтобы не лезть в бд :)**  
**URL:** /role/debug-role  
**Method:** POST

**Params:**
- `login` (String): Логин пользователя, которому нужно выдать роль **ADMIN**.

**Example:**
```bash
POST http://localhost:8080/role/debug-role?login=testuser
```

### add-role
**URL:** /role/add-role  
**Method:** POST

**Params:**
- `token` (String): JWT токен администратора (для авторизации).
- `login` (String): Логин пользователя, которому нужно добавить роль.
- `role` (String): Роль, которую необходимо назначить (например, **ADMIN**, **GUEST**, **PREMIUM_USER**).

**Example:**
```bash
POST http://localhost:8080/role/add-role?token=admin_token&login=testuser&role=ADMIN
```

### admin
**URL:** /auth/admin  
**Method:** POST

**Params:**
- `token` (String): JWT токен пользователя.

**Example:**
```bash
POST http://localhost:8080/auth/admin?token=admin_token
```

### premium
**URL:** /auth/premium  
**Method:** POST

**Params:**
- `token` (String): JWT токен пользователя.

**Example:**
```bash
POST http://localhost:8080/auth/premium?token=premium_token
```
