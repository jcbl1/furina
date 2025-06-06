package de.yelr.ai.furina.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import de.yelr.ai.furina.entity.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {}
