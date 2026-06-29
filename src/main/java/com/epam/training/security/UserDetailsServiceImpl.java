package com.epam.training.security;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;

    private static final SimpleGrantedAuthority ROLE_TRAINEE =
            new SimpleGrantedAuthority("ROLE_TRAINEE");
    private static final SimpleGrantedAuthority ROLE_TRAINER =
            new SimpleGrantedAuthority("ROLE_TRAINER");

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return traineeDao.findByUsername(username)
                .map(te -> new UserPrincipal(te.getUser(), ROLE_TRAINEE))
                .or(() ->
                        trainerDao.findByUsername(username)
                                .map(tr -> new UserPrincipal(tr.getUser(), ROLE_TRAINER))
                )
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username)
                );
    }
}

