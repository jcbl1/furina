package de.yelr.ai.furina.security;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import de.yelr.ai.furina.entity.Account;
import de.yelr.ai.furina.mapper.AccountMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountMapper accountMapper;

    public CustomUserDetailsService(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!StringUtils.hasText(username)) {
            throw new UsernameNotFoundException("Username is required");
        }
        Account account =
                accountMapper.selectOne(
                        Wrappers.<Account>lambdaQuery().eq(Account::getUsername, username));
        if (account == null) {
            throw new UsernameNotFoundException("Account not found");
        }
        return AccountPrincipal.from(account);
    }
}
