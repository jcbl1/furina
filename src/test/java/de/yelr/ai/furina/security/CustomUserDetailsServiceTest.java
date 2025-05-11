package de.yelr.ai.furina.security;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import de.yelr.ai.furina.entity.Account;
import de.yelr.ai.furina.mapper.AccountMapper;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {

    @Test
    void loadsAccountAsPrincipal() {
        AccountMapper mapper = mock(AccountMapper.class);
        Account account = new Account();
        account.setId(1L);
        account.setUsername("user");
        account.setPassword("hash");
        account.setRole("USER");
        account.setEnabled(true);
        when(mapper.selectOne(any(Wrapper.class))).thenReturn(account);

        var principal = new CustomUserDetailsService(mapper).loadUserByUsername("user");

        assertThat(principal.getUsername()).isEqualTo("user");
        assertThat(principal.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
    }

    @Test
    void rejectsUnknownAccount() {
        AccountMapper mapper = mock(AccountMapper.class);
        when(mapper.selectOne(any(Wrapper.class))).thenReturn(null);

        assertThatThrownBy(() -> new CustomUserDetailsService(mapper).loadUserByUsername("ghost"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
