package com.github.bschipper.spotifysecurity.features.musictrack;

import com.github.bschipper.spotifysecurity.features.musictrack.MusicTrack;
import com.github.bschipper.spotifysecurity.features.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MusicTrackRepository extends JpaRepository<MusicTrack, Long> {

    void deleteMusicTrackByIdAndArtist(Long id, User artist);

    Boolean existsMusicTrackByIdAndUserLikesContaining(Long trackId, User user);

    Optional<List<MusicTrack>> findMusicTracksByUserLikesContaining(User user);

    Optional<List<MusicTrack>> findMusicTracksByArtist_Id(Long artistId);
}
