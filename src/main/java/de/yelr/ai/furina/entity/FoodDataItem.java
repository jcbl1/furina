package de.yelr.ai.furina.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.Data;

@Data
@TableName("food_data_item")
public class FoodDataItem {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("food_data_id")
    private Integer foodDataId;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("price")
    private BigDecimal price;
}
