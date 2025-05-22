package de.yelr.ai.furina.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("customer_order_item")
public class CustomerOrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("food_data_id")
    private Integer foodDataId;

    @TableField("food_data_item_id")
    private Integer foodDataItemId;

    @TableField("item_name")
    private String itemName;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    @TableField("quantity")
    private Integer quantity;

    @TableField("note")
    private String note;
}
