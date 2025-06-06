package de.yelr.ai.furina.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("account")
public class Account {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    /** BCrypt encoded password. */
    @TableField("password")
    private String password;

    @TableField("role")
    private String role;

    @TableField("enabled")
    private Boolean enabled;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
