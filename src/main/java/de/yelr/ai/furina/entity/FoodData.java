package de.yelr.ai.furina.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("food_data")
public class FoodData {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("thumbnail_path")
    private String thumbnailPath;

    @TableField("title")
    private String title;

    @TableField("rating")
    private Double rating;

    @TableField("sales_monthly")
    private Integer salesMonthly;

    @TableField("average_cost")
    private BigDecimal averageCost;

    /**
     * Estimated delivery time in minutes.
     */
    @TableField("delivery_time")
    private Integer deliveryTime;

    @TableField("featured_comment")
    private String featuredComment;
}
