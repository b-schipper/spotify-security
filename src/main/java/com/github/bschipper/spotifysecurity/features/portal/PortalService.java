package com.github.bschipper.spotifysecurity.features.portal;

import com.github.bschipper.spotifysecurity.features.authentication.ERole;
import com.github.bschipper.spotifysecurity.features.authentication.RoleRepository;
import com.github.bschipper.spotifysecurity.features.music.MusicTrackRepository;
import com.github.bschipper.spotifysecurity.features.portal.response.MetricsResponse;
import com.github.bschipper.spotifysecurity.features.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PortalService {
    private final MusicTrackRepository musicTrackRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public MetricsResponse getApplicationMetrics() {
        int userAmount = userRepository.countAllByRolesContaining(roleRepository.findByName(ERole.ROLE_USER).get());
        int artistAmount = userRepository.countAllByRolesContaining(roleRepository.findByName(ERole.ROLE_ARTIST).get());
        int adminAmount = userRepository.countAllByRolesContaining(roleRepository.findByName(ERole.ROLE_ADMIN).get());

        long musicAmount = musicTrackRepository.count();

        MetricsResponse response = new MetricsResponse();
        response.setUserAmount(userAmount);
        response.setArtistAmount(artistAmount);
        response.setAdminAmount(adminAmount);
        response.setMusicAmount(musicAmount);
        return response;
    }
}
